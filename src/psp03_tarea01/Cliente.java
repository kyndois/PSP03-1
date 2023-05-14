package psp03_tarea01;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Cliente extends JFrame implements ActionListener {

    Socket socket = null;
    GridBagConstraints gbc;
    ObjectInputStream fentrada;
    ObjectOutputStream fsalida;
    String nombre;
    static JTextField mensaje = new JTextField();
    private JScrollPane scroll;
    static JTextArea textarea;
    JButton enviar = new JButton("Enviar");
    JButton salir = new JButton("Salir");
    boolean repetir = true;

    public Cliente(Socket s, String nombre) {

        super("JUGADOR: " + nombre);
        setSize(800, 600);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        add(mensaje,
                addConstraints(0, 0, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                        1.0, 0.0));
        enviar.addActionListener(this);
        enviar.setPreferredSize(new Dimension(100, 30));
        add(enviar,
                addConstraints(1, 0, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE,
                        0.0, 0.0));

        textarea = new JTextArea();
        scroll = new JScrollPane(textarea);
        add(scroll,
                addConstraints(0, 1, 1, 2, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        1.0, 2.0));

        salir.addActionListener(this);
        salir.setPreferredSize(new Dimension(100, 30));
        add(salir,
                addConstraints(1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE,
                        0.0, 0.0));

        repaint();
        socket = s;
        System.out.println(s.getPort());
        this.nombre = nombre;
        
        try {
            fentrada = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ioe) {
            System.out.println("ERROR:\n" + ioe.getMessage());
        }
        try {
            fsalida = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ioe) {
            System.out.println("ERROR:\n" + ioe.getMessage());
        }
        try {
            fsalida.writeObject(new Mensaje(nombre, "enter"));
        } catch (IOException ioe) {
            System.out.println("ERROR:\n" + ioe.getMessage());
        }

    }

    public static void main(String[] args) {
        int puerto = 2000;
        String nombre = JOptionPane.showInputDialog("Introduce tu nombre para participar:");
        Socket s = null;

        try {
            s = new Socket("localhost", puerto);
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "IMPOSIBLE CONECTARSE CON EL SERVIDOR", "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        if (!nombre.trim().equals("")) {
            Cliente cliente = new Cliente(s, nombre);
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
                textarea.setText(texto.getTexto());

            } catch (IOException ioe) {
                System.out.println("Servidor cerrado");
            } catch (ClassNotFoundException cnfe) {
                System.out.println("ERROR:\n" + cnfe.getMessage());
            }
        }
    }

    private GridBagConstraints addConstraints(int gridx, int gridy, int gridwidth, int gridheight, int anchor, int fill, double gridweightx, double gridweighty) {
        gbc = new GridBagConstraints(gridx, gridy, gridwidth, gridheight, gridweightx, gridweighty,
                anchor, fill, new Insets(5, 5, 5, 5), 0, 0);
        return gbc;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            fsalida = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ioe) {
            System.out.println("ERROR:\n" + ioe.getMessage());
        }
        if (e.getSource().equals(salir)) {
            try {
                fsalida.writeObject(new Mensaje(nombre, "exit"));
                repetir = false;
                System.exit(0);
            } catch (IOException ioe) {
                System.out.println("ERROR:\n" + ioe.getMessage());
            }
        } else {
            try {
                mensaje.setText("");
                fsalida.writeObject(new Mensaje(nombre, mensaje.getText()));
            } catch (IOException ioe) {
                System.out.println("ERROR:\n" + ioe.getMessage());
            }
        }
    }

}
