package psp03_tarea01;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;

public class Servidor extends JFrame implements ActionListener {

    static GridBagConstraints gbc;

    static ServerSocket servidor;
    static final int PUERTO = 2000;
    static int NUMERO = (int) (Math.random() * 100);
    static int INTENTOS = 0;
    static int jugadores = 0;
    static boolean repeat = true;
    static JLabel number = new JLabel(String.valueOf(NUMERO));
    static JTextField numJugadores = new JTextField();
    static JScrollPane scroll;
    static JScrollPane scroll2;
    static JPanel jugadoresActivos;
    static JTextArea textarea;
    static JPanel panelNumero;
    JButton salir = new JButton("Salir");
    JButton limpiar = new JButton("Limpiar");
    static ArrayList<Jugador> listajugadores = new ArrayList<>();
    static ArrayList<Thread> hilos = new ArrayList<>();

    public Servidor() {
        super("SERVIDOR");
        setMinimumSize(new Dimension(400, 400));
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        numJugadores.setEditable(false);
        numJugadores.setText("NUMERO DE INTENTOS: " + INTENTOS);
        add(numJugadores,
                addConstraints(0, 0, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                        1.0, 0.0));

        salir.addActionListener(this);
        salir.setPreferredSize(new Dimension(110, 30));
        add(salir,
                addConstraints(2, 0, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE,
                        0.0, 0.0));

        textarea = new JTextArea();
        textarea.setEditable(false);
        scroll = new JScrollPane(textarea);
        add(scroll,
                addConstraints(0, 1, 1, 3, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        1.0, 1.0));

        panelNumero = new JPanel();
        panelNumero.setLayout(new GridLayout(2, 1));
        panelNumero.add(new JLabel("Número a adivinar:"));
        number.setFont(new Font("Times New Roman", Font.BOLD, 24));
        panelNumero.add(number);

        add(panelNumero,
                addConstraints(2, 1, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                        0.0, 0.0));

        jugadoresActivos = new JPanel();
        jugadoresActivos.setLayout(new BoxLayout(jugadoresActivos, BoxLayout.Y_AXIS));
        actualizarLista();
        scroll2 = new JScrollPane(jugadoresActivos);

        add(scroll2,
                addConstraints(2, 2, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        0.0, 1.0));

        limpiar.addActionListener(this);
        limpiar.setPreferredSize(new Dimension(110, 30));
        add(limpiar,
                addConstraints(2, 3, 1, 1, GridBagConstraints.SOUTH, GridBagConstraints.NONE,
                        0.0, 0.0));

    }

    public static void main(String[] args) throws IOException {
        Servidor display = new Servidor();
        display.setVisible(true);
        ejecutar();
    }

    public static void ejecutar() throws IOException {
        if (servidor == null) {
            servidor = new ServerSocket(PUERTO);
        }

        while (repeat) {
            Socket s = new Socket();

            try {
                s = servidor.accept();

            } catch (Exception e) {
                System.out.println("ERROR al esperar Cliente:\n" + e.getMessage());
                break;
            }

            jugadores++;
            HiloServer hilo = new HiloServer(s);
            hilo.start();

        }
        if (!servidor.isClosed())
        try {
            servidor.close();

        } catch (IOException ex) {
            System.out.println("ERROR al Cerrar Servidor:\n" + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(salir)) {
            try {
                servidor.close();
            } catch (IOException ex) {
                System.out.println("ERROR al Salir:\n" + ex.getMessage());
            }
            System.exit(0);
        }
        if (e.getSource().equals(limpiar)) {
            textarea.selectAll();
            textarea.replaceSelection("");
        }
    }

    public static void reiniciar() throws IOException {
        int reiniciar = JOptionPane.showConfirmDialog(null, "¿Reiniciar?");
        repeat = false;
        if (reiniciar != 0) {
            System.exit(0);
        } else {
            textarea.selectAll();
            textarea.replaceSelection("");
            textarea.setBackground(Color.WHITE);
            INTENTOS = 0;
            numJugadores.setForeground(Color.black);
            numJugadores.setText("NUMERO DE INTENTOS: " + INTENTOS);
            NUMERO = (int) (Math.random() * 100);
            number.setText(String.valueOf(NUMERO));
            listajugadores.clear();
            jugadores = 0;
            hilos.clear();
            actualizarLista();

            ejecutar();
        }

    }

    public static void nuevoJugador(String s, Socket o) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(o.getOutputStream());
            Jugador jugador = new Jugador(s, oos);

            int repetido = 0;
            if (!listajugadores.isEmpty()) {
                for (Jugador s1 : listajugadores) {
                    if (s == s1.getName()) {
                        repetido++;
                    }
                }
            }
            if (repetido != 0) {
                s = s.concat(" " + String.valueOf(repetido));
            }
            jugador.setName(s);
            listajugadores.add(jugador);
            actualizarLista();
        } catch (IOException ioe) {
            System.out.println("ERROR en nuevoJugador:\n" + ioe.getMessage());
        }
        
    }

    public static void saleJugador(String s1) {
        Iterator it = listajugadores.iterator();
        Jugador jugador;
        if (listajugadores.size() == 1) {
            listajugadores.remove(0);
        } else {
            while (it.hasNext()) {
                jugador = (Jugador) it.next();
                String name = jugador.getName();
                if (name.equals(s1)) {
                    it.remove();
                    break;
                }
            }
        }
        jugadores--;
        actualizarLista();
    }

    public static void actualizarLista() {
        jugadoresActivos.removeAll();
        jugadoresActivos.add(new JLabel("JUGADORES: " + jugadores));
        jugadoresActivos.add(new JLabel("--------------------------"));
        if (!listajugadores.isEmpty()) {
            for (Jugador s : listajugadores) {
                jugadoresActivos.add(new JLabel(s.getName()));
            }
        }
        jugadoresActivos.revalidate();
        jugadoresActivos.repaint();
        
    }

    public static void winner(String name) {
        textarea.setBackground(Color.red);
        jugadoresActivos.removeAll();
        jugadoresActivos.add(new JLabel("JUGADORES:" + jugadores));
        jugadoresActivos.add(new JLabel("--------------------------"));
        for (Jugador s : listajugadores) {
            JLabel winner = new JLabel(s.getName());
            if (s.getName().equals(name)) {
                winner.setForeground(Color.WHITE);
                winner.setOpaque(true);
                winner.setBackground(Color.red);
            }
            jugadoresActivos.add(winner);
        }
        jugadoresActivos.revalidate();
        jugadoresActivos.repaint();
        JOptionPane.showMessageDialog(null, "¡TENEMOS GANADOR!\n\nGanador: " + name);
        try {
            reiniciar();
        } catch (IOException ex) {
            System.out.println("ERROR al reiniciar:\n" + ex.getMessage());
        }
    }

    private GridBagConstraints addConstraints(int gridx, int gridy, int gridwidth, int gridheight, int anchor, int fill, double gridweightx, double gridweighty) {
        gbc = new GridBagConstraints(gridx, gridy, gridwidth, gridheight, gridweightx, gridweighty,
                anchor, fill, new Insets(5, 5, 5, 5), 0, 0);
        return gbc;
    }

    public static ArrayList<String> getLista() {
        ArrayList<String> lista = new ArrayList<>();
        for(Jugador j : listajugadores){
            lista.add(j.getName());
        }
        return lista;
    }
}
