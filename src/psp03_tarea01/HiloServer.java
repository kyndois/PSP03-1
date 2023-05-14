package psp03_tarea01;

import java.awt.Color;
import java.io.*;
import java.net.*;


public class HiloServer extends Thread{

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
        EnviarMensaje(new Mensaje("Servidor",text));

        while (true) {
            int respuesta = 0;

            try {
                mensaje = (Mensaje) fentrada.readObject();

                if (mensaje.getTexto().equals("enter")) {
                    Servidor.nuevoJugador(mensaje.getName());
                    Servidor.textarea.append("**** El jugador " + mensaje.getName() + " ha entrado al juego ****\n");
                } 
                else if (mensaje.getTexto().equals("exit")) {
                    Servidor.saleJugador(mensaje.getName());
                    Servidor.textarea.append("**** El jugador " + mensaje.getName() + " ha salido del juego ****\n");
                } 
                else {
                    respuesta = mensaje.getRespuesta();

                    if (respuesta == Servidor.NUMERO) {
                        Servidor.numJugadores.setText("¡ SE HA ADIVINADO EL NÚMERO !");
                        Servidor.numJugadores.setForeground(Color.red);
                        Servidor.winner(mensaje.getName());
                        break;
                    }

                    Servidor.textarea.append(String.valueOf(mensaje.getName() + " -> " + respuesta) + "\n");
                    text = Servidor.textarea.getText();
                    EnviarMensaje(new Mensaje("Servidor",text));

                }
            } catch (IOException ioe) {
                System.out.println("ERROR:\n" + ioe.getMessage());
            } catch (ClassNotFoundException cnfe) {
                System.out.println("ERROR:\n" + cnfe.getMessage());
            }
            break;
        }
    }

    private void EnviarMensaje(Mensaje msg) {

        for (int i = 0; i < Servidor.tabla.size(); i++) {
            Socket s1 = Servidor.tabla.get(i);
            try {
                ObjectOutputStream fsalida = new ObjectOutputStream(s1.getOutputStream());
                fsalida.writeObject(msg);
            } catch (SocketException se) {
                System.out.println("ERROR:\n" + se.getMessage());
            } catch (IOException ioe) {
                System.out.println("ERROR:\n" + ioe.getMessage());
            }
        }
    }

}
