package psp03_tarea01;

import java.awt.Color;
import java.io.*;
import java.net.*;

public class HiloServer extends Thread {

    Mensaje mensaje;
    ObjectInputStream fentrada;
    ObjectOutputStream fsalida;
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

        String text = Servidor.textarea.getText();
        //EnviarMensaje();

        while (true) {
            int respuesta = -1;
            Servidor.numJugadores.setText("NUMERO DE INTENTOS: " + Servidor.INTENTOS);
            try {

                mensaje = (Mensaje) fentrada.readObject();
                if (mensaje.getTipo().equals("enter")) {
                    Servidor.nuevoJugador(mensaje.getTexto(), socket);
                    Servidor.textarea.append("**** El jugador " + mensaje.getTexto() + " ha entrado al juego ****\n");
                    EnviarMensaje("historial");

                } else if (mensaje.getTipo().equals("exit")) {
                    EnviarMensaje("historial");
                    Servidor.textarea.append("**** El jugador " + mensaje.getTexto() + " ha salido del juego ****\n");
                    Servidor.saleJugador(mensaje.getTexto());

                } else {
                    Servidor.INTENTOS++;
                    respuesta = mensaje.getRespuesta();

                    if (respuesta == Servidor.NUMERO) {
                        Servidor.numJugadores.setText("¡ SE HA ADIVINADO EL NÚMERO !");
                        Servidor.numJugadores.setForeground(Color.red);
                        Servidor.winner(mensaje.getName());
                        EnviarMensaje(mensaje.getName());
                        break;
                    }
                    Servidor.textarea.append(mensaje.getName() + " -> " + respuesta + "\n");
                    if (respuesta < Servidor.NUMERO) {
                        Servidor.textarea.append("---Servidor" + " -> " + respuesta + " es menor que el número oculto!\n");
                    } else {
                        Servidor.textarea.append("---Servidor" + " -> " + respuesta + " es mayor que el número oculto!\n");
                    }
                    EnviarMensaje("historial");
                }

            } catch (IOException ioe) {
                System.out.println("ERROR:\n" + ioe.getMessage());
            } catch (ClassNotFoundException cnfe) {
                System.out.println("ERROR:\n" + cnfe.getMessage());
            }

        }
    }

    private void EnviarMensaje(String s) {
        try {
            for (Jugador j : Servidor.listajugadores) {
                fsalida = j.getStream();
                if (s.equals("historial")) {
                    mensaje = new Mensaje("historial", Servidor.textarea.getText());
                } else if (s.equals("jugadores")) {
                }
                fsalida.writeObject(mensaje);
            }
        } catch (IOException ex) {
            System.out.println("ASJFIAJGAGJD");
        }

    }

}
