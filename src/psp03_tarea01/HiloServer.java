package psp03_tarea01;

import java.awt.Color;
import java.io.*;
import java.net.*;

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
        

        while (true) {
            int respuesta = -1;

            try {
                EnviarMensaje();
                mensaje = (Mensaje) fentrada.readObject();
                
                if (mensaje.getTipo().equals("enter")) {
                    Servidor.nuevoJugador(mensaje.getTexto());
                    Servidor.textarea.append("**** El jugador " + mensaje.getTexto() + " ha entrado al juego ****\n");

                } else if (mensaje.getTipo().equals("exit")) {
                    Servidor.saleJugador(mensaje.getTexto());
                    Servidor.textarea.append("**** El jugador " + mensaje.getTexto() + " ha salido del juego ****\n");

                } else {
                    respuesta = mensaje.getRespuesta();

                    if (respuesta == Servidor.NUMERO) {
                        Servidor.numJugadores.setText("¡ SE HA ADIVINADO EL NÚMERO !");
                        Servidor.numJugadores.setForeground(Color.red);
                        Servidor.winner(mensaje.getName());
                        break;
                    }
                    Servidor.textarea.append(mensaje.getName() + " -> " + respuesta + "\n");
                    if (respuesta < Servidor.NUMERO) {
                        Servidor.textarea.append("---Servidor" + " -> " + respuesta + " es menor que el número oculto!\n");
                    } else {
                        Servidor.textarea.append("---Servidor" + " -> " + respuesta + " es mayor que el número oculto!\n");
                    }
                   
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
        for (Socket s1 : Servidor.tabla) {
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
