package psp03_tarea01;

import java.awt.Color;
import java.io.*;
import java.net.*;
import javax.swing.JTextArea;

public class HiloServer extends Thread {

    Mensaje mensaje;
    ObjectInputStream fentrada;
    Socket socket = null;

    public HiloServer(Socket s) {
        socket = s;
        try {
            fentrada = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            System.out.println("ERROR:\n" + ex.getMessage());
        }
    }

    public void run() {
        Servidor.numJugadores.setText("NUMERO DE JUGADORES: " + Servidor.jugadores);
        String text = Servidor.textarea.getText();
        EnviarMensaje();

        while (true) {
            int respuesta = 0;

            try {
                mensaje = (Mensaje) fentrada.readObject();

                if (mensaje.getTipo().equals("enter")) {
                    Servidor.nuevoJugador(mensaje.getTexto());
                    Servidor.textarea.append("**** El jugador " + mensaje.getTexto() + " ha entrado al juego ****\n");
                    EnviarMensaje();
                } else if (mensaje.getTipo().equals("exit")) {
                    Servidor.saleJugador(mensaje.getTexto());
                    Servidor.textarea.append("**** El jugador " + mensaje.getTexto() + " ha salido del juego ****\n");
                    EnviarMensaje();
                } else {
                    respuesta = mensaje.getRespuesta();

                    if (respuesta == Servidor.NUMERO) {
                        Servidor.numJugadores.setText("¡ SE HA ADIVINADO EL NÚMERO !");
                        Servidor.numJugadores.setForeground(Color.red);
                        Servidor.winner(mensaje.getName());
                        break;
                    }
                    EnviarMensaje();
                    Servidor.textarea.append(mensaje.getName() + " -> " + respuesta + "\n");
                }

            } catch (IOException ioe) {
                System.out.println("ERROR:\n" + ioe.getMessage());
            } catch (ClassNotFoundException cnfe) {
                System.out.println("ERROR:\n" + cnfe.getMessage());
            }
            break;
        }
    }

    private void EnviarMensaje() {

        for (int i = 0; i < Servidor.tabla.size(); i++) {
            Socket s1 = Servidor.tabla.get(i);
            try {
                ObjectOutputStream fsalida = new ObjectOutputStream(s1.getOutputStream());
                mensaje = new Mensaje("historial", Servidor.textarea.getText());
                fsalida.writeObject(mensaje);
            } catch (SocketException se) {
                System.out.println("ERROR:\n" + se.getMessage());
            } catch (IOException ioe) {
                System.out.println("ERROR:\n" + ioe.getMessage());
            }
        }
    }

}
