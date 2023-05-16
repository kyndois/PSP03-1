package psp03_tarea01;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.*;

public class Cliente extends JFrame implements ActionListener {

    Mensaje msg;
    Socket socket = null;
    GridBagConstraints gbc;
    ObjectInputStream fentrada;
    ObjectOutputStream fsalida;
    String nombre;
    static JTextField mensaje = new JTextField();
    private JScrollPane scroll;
    private JScrollPane scroll2;
    static JTextArea textarea;
    static JPanel jugadoresActivos;
    JButton enviar = new JButton("Enviar");
    JButton salir = new JButton("Salir");
    JButton limpiar = new JButton("Limpiar");
    boolean repetir = true;
    ArrayList<String> listajugadores = new ArrayList<>();

    public Cliente(Socket s, String nombre) {

        super("JUGADOR: " + nombre);
        setSize(800, 600);
        setVisible(true);
        addWindowListener(exitListener);
        setLayout(new GridBagLayout());
        socket = s;
        this.nombre = nombre;

        jugadoresActivos = new JPanel();
        jugadoresActivos.setLayout(new BoxLayout(jugadoresActivos, BoxLayout.Y_AXIS));
        scroll2 = new JScrollPane(jugadoresActivos);

        add(mensaje,
                addConstraints(0, 0, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                        1.0, 0.0));
        enviar.addActionListener(this);
        enviar.setPreferredSize(new Dimension(110, 30));
        add(enviar,
                addConstraints(1, 0, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE,
                        0.0, 0.0));

        textarea = new JTextArea();
        scroll = new JScrollPane(textarea);
        add(scroll,
                addConstraints(0, 1, 1, 3, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        1.0, 2.0));

        salir.addActionListener(this);
        salir.setPreferredSize(new Dimension(110, 30));
        add(salir,
                addConstraints(1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE,
                        0.0, 0.0));

        add(scroll2,
                addConstraints(1, 2, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        0.0, 1.0));

        try {

            fsalida = new ObjectOutputStream(socket.getOutputStream());
            msg = new Mensaje("enter", nombre);
            fsalida.writeObject(msg);
            fentrada = new ObjectInputStream(socket.getInputStream());

        } catch (IOException ioe) {
            System.out.println("ERROR:\n" + ioe.getMessage());
        }

    }

    public static void main(String[] args) {
        int puerto = 2000;
        String nombre = "";
        Socket s = null;
        do {
            nombre = JOptionPane.showInputDialog("Introduce tu nombre para participar:");
            if (nombre.length() < 4 || nombre.length() > 10) {
                JOptionPane.showMessageDialog(null, "Debe escribir un nombre con un mínimo de 4 letras y un máximo de 10", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } while (nombre.length() < 4 || nombre.length() > 10);

        try {
            s = new Socket("localhost", puerto);
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "IMPOSIBLE CONECTARSE CON EL SERVIDOR", "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        if (!nombre.trim().equals("")) {
            Cliente cliente = new Cliente(s, nombre);
            cliente.setVisible(true);
            cliente.ejecutar();
        } else {
            System.out.println("Nombre vacio");
        }
    }

    public void ejecutar() {
        Mensaje texto;

        while (repetir) {
            try {
                texto = (Mensaje) fentrada.readObject();
                if (texto.getTipo().equals("historial")) {
                    textarea.selectAll();
                    textarea.replaceSelection("");
                    textarea.append(texto.getTexto());
                }
                if (texto.getTipo().equals("jugadores")) {
                    listajugadores = texto.getLista();
                    actualizarLista(listajugadores);
                }
                if (texto.getTipo().equals("winner")) {
                    winner(texto.getTexto());
                    textarea.setBackground(Color.red);
                    JOptionPane.showConfirmDialog(null, "¡TENEMOS GANADOR!\nFELICIADES " + texto.getTexto());
                    repetir = false;
                }

            } catch (IOException ioe) {
                System.out.println("Servidor cerrado\n" + ioe.getMessage());
            } catch (ClassNotFoundException cnfe) {
                System.out.println("ERROR DE CLASE:\n" + cnfe.getMessage());
            }
        }
        try {
            fsalida.close();
            fentrada.close();
            socket.close();
            System.exit(0);
        } catch (IOException ioe) {
            System.out.println("ERROR\n" + ioe.getMessage());
        }

    }


    private GridBagConstraints addConstraints(int gridx, int gridy, int gridwidth, int gridheight, int anchor, int fill, double gridweightx, double gridweighty) {
        gbc = new GridBagConstraints(gridx, gridy, gridwidth, gridheight, gridweightx, gridweighty,
                anchor, fill, new Insets(5, 5, 5, 5), 0, 0);
        return gbc;
    }


    WindowListener exitListener = new WindowAdapter() {

        @Override
        public void windowClosing(WindowEvent e) {
            try {
                msg = new Mensaje("exit", nombre);
                fsalida.writeObject(msg);
                repetir = false;

            } catch (IOException ioe) {
                System.out.println("ERROR:\n" + ioe.getMessage());
            }
        }
    };

    public void actualizarLista(ArrayList<String> lista) {
        jugadoresActivos.removeAll();
        jugadoresActivos.add(new JLabel("JUGADORES: " + listajugadores.size()));
        jugadoresActivos.add(new JLabel("--------------------------"));
        if (!listajugadores.isEmpty()) {
            for (String s : listajugadores) {
                jugadoresActivos.add(new JLabel(s));
            }
        }
        jugadoresActivos.repaint();
    }

    public void winner(String name) {
        jugadoresActivos.removeAll();
        jugadoresActivos.add(new JLabel("JUGADORES:" + listajugadores.size()));
        jugadoresActivos.add(new JLabel("--------------------------"));
        for (String s : listajugadores) {
            if (s.equals(name)) {
                JLabel winner = new JLabel(name);
                winner.setForeground(Color.WHITE);
                winner.setOpaque(true);
                winner.setBackground(Color.red);
            } else {
                jugadoresActivos.add(new JLabel(s));
            }
        }
        jugadoresActivos.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(salir)) {
            try {
                msg = new Mensaje("exit", this.nombre);
                fsalida.writeObject(msg);
                repetir = false;

            } catch (IOException ioe) {
                System.out.println("ERROR:\n" + ioe.getMessage());
            }
        }
        if (e.getSource().equals(enviar)) {
            int respuesta = -1;
            try {
                respuesta = Integer.parseInt(mensaje.getText());
                if (respuesta < 0 || respuesta > 100) {
                    throw new Exception();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "La respuesta debe ser un número del 0 al 100", "ERROR", JOptionPane.ERROR_MESSAGE);
                respuesta = -1;
            }
            mensaje.setText("");
            if (respuesta != -1) {
                try {
                    msg = new Mensaje(nombre, respuesta);
                    fsalida.writeObject(msg);
                } catch (IOException ioe) {
                    System.out.println("ERROR:\n" + ioe.getMessage());

                }
            }
        }
    }
}
