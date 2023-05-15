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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class Servidor extends JFrame implements ActionListener {

    static GridBagConstraints gbc;

    static ServerSocket servidor;
    static final int PUERTO = 2000;
    static final int NUMERO = (int) (Math.random() * 100);
    static int INTENTOS = 0;
    static int jugadores = 0;

    static JLabel number = new JLabel(String.valueOf(NUMERO));
    static JTextField numJugadores = new JTextField();
    static JScrollPane scroll;
    static JScrollPane scroll2;
    static JPanel jugadoresActivos;
    static JTextArea textarea;
    static JPanel textarea2;
    JButton salir = new JButton("Salir");
    static Socket[] tabla = new Socket[100];
    static Object[][] tablacoms = new Object[100][100];
    static ArrayList<Jugador> listajugadores = new ArrayList<>();

    public Servidor() {
        super("SERVIDOR");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        numJugadores.setEditable(false);
        numJugadores.setText("NUMERO DE INTENTOS: " + INTENTOS);
        add(numJugadores,
                addConstraints(0, 0, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                        1.0, 0.0));

        salir.addActionListener(this);
        salir.setPreferredSize(new Dimension(100, 30));
        add(salir,
                addConstraints(2, 0, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE,
                        0.0, 0.0));

        textarea = new JTextArea();
        textarea.setEditable(false);
        scroll = new JScrollPane(textarea);
        add(textarea,
                addConstraints(0, 1, 1, 2, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        1.0, 1.0));

        textarea2 = new JPanel();
        textarea2.setLayout(new GridLayout(2, 1));
        textarea2.add(new JLabel("Número a adivinar:"));
        number.setFont(new Font("Times New Roman", Font.BOLD, 24));
        textarea2.add(number);

        add(textarea2,
                addConstraints(2, 1, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                        0.0, 0.0));

        jugadoresActivos = new JPanel();
        jugadoresActivos.setLayout(new BoxLayout(jugadoresActivos, BoxLayout.Y_AXIS));
        actualizarLista();
        scroll2 = new JScrollPane(jugadoresActivos);

        add(scroll2,
                addConstraints(2, 2, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        0.0, 1.0));

    }

    public static void main(String[] args) throws IOException {

        servidor = new ServerSocket(PUERTO);
        Servidor display = new Servidor();
        display.setVisible(true);

        while (true) {
            Socket s = new Socket();

            try {
                s = servidor.accept();

            } catch (Exception e) {
                System.out.println("ERROR:\n" + e.getMessage());
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
            System.out.println("ERROR:\n" + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(salir)) {
            try {
                servidor.close();
            } catch (IOException ex) {
                System.out.println("ERROR:\n" + ex.getMessage());
            }
            System.exit(0);
        }
    }

    public static void nuevoJugador(String s, Socket o) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(o.getOutputStream());
            Jugador jugador = new Jugador(s, oos);

            int repetido = 0;
            for (Jugador s1 : listajugadores) {
                if (s == s1.getName()) {
                    repetido++;
                }
            }
            if (repetido != 0) {
                s = s.concat(" " + String.valueOf(repetido));
            }
            jugador.setName(s);
            listajugadores.add(jugador);
            actualizarLista();
        } catch (IOException ioe) {
            System.out.println("ERROR:\n" + ioe.getMessage());
        }
    }

    public static void saleJugador(String s1) {
        Iterator it = listajugadores.iterator();
        Jugador jugador;
        while (it.hasNext()) {
            jugador = (Jugador) it.next();
            String name = jugador.getName();
            if (name.equals(s1)) {
                try {
                    jugador.getStream().close();
                } catch (IOException ex) {
                    System.out.println("loquenado");
                }
                it.remove();
                break;
            }
            System.out.println("Ohosdg");
        }
        jugadores--;
        actualizarLista();
    }

    public static void actualizarLista() {
        jugadoresActivos.removeAll();
        jugadoresActivos.add(new JLabel("JUGADORES: " + jugadores));
        jugadoresActivos.add(new JLabel("--------------------------"));
        for (Jugador s : listajugadores) {
            jugadoresActivos.add(new JLabel(s.getName()));
        }
        jugadoresActivos.repaint();
    }

    public static void winner(String name) {
        jugadoresActivos.removeAll();
        jugadoresActivos.add(new JLabel("JUGADORES:"));
        jugadoresActivos.add(new JLabel("--------------------------"));
        for (Jugador s : listajugadores) {
            if (s.equals(name)) {
                JLabel winner = new JLabel(name);
                winner.setForeground(Color.WHITE);
                winner.setOpaque(true);
                winner.setBackground(Color.red);
            } else {
                jugadoresActivos.add(new JLabel(s.getName()));
            }
        }
        jugadoresActivos.repaint();
    }

    private GridBagConstraints addConstraints(int gridx, int gridy, int gridwidth, int gridheight, int anchor, int fill, double gridweightx, double gridweighty) {
        gbc = new GridBagConstraints(gridx, gridy, gridwidth, gridheight, gridweightx, gridweighty,
                anchor, fill, new Insets(5, 5, 5, 5), 0, 0);
        return gbc;
    }
}
