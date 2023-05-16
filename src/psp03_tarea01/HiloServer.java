package psp03_tarea01;

import java.awt.Color;
import java.io.*;
import java.net.*;

public class HiloServer extends Thread {
    
    Mensaje mensaje;
    ObjectInputStream fentrada;
    ObjectOutputStream fsalida;
    Socket socket = null;
    boolean repeat = true;

    public HiloServer(Socket s) {
        socket = s;
        try {
            fentrada = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            System.out.println("ERROR al crear Hilo:\n" + ex.getMessage());
        }

    }

    public void run() {
        

        while (repeat) {
            int respuesta = -1;
            Servidor.numJugadores.setText("NUMERO DE INTENTOS: " + Servidor.INTENTOS);
            try {
                
                mensaje = (Mensaje) fentrada.readObject();
                if (mensaje.getTipo().equals("enter")) {
                    Servidor.nuevoJugador(mensaje.getTexto(), socket);
                    Servidor.textarea.append("**** El jugador " + mensaje.getTexto() + " ha entrado al juego ****\n");
                    EnviarMensaje("jugadores");

                } else if (mensaje.getTipo().equals("exit")) {
                    Servidor.textarea.append("**** El jugador " + mensaje.getTexto() + " ha salido del juego ****\n");
                    socket.close();
                    repeat = false;
                    Servidor.saleJugador(mensaje.getTexto());

                } else {
                    Servidor.INTENTOS++;
                    respuesta = mensaje.getRespuesta();
                    String nombre = mensaje.getName();
                    if (respuesta == Servidor.NUMERO) {

                        Servidor.numJugadores.setText("¡ SE HA ADIVINADO EL NÚMERO !");
                        Servidor.numJugadores.setForeground(Color.red);
                        Servidor.textarea.append("******** Servidor" + " -> " + respuesta + " era el número oculto!\n******** El ganador es:\n******** " + nombre + " ********");
                        EnviarMensaje("jugadores");
                        EnviarMensaje("historial");
                        EnviarMensaje(nombre);
                        Servidor.winner(nombre);
                        break;
                    }
                    Servidor.textarea.append(nombre + " -> " + respuesta + "\n");
                    if (respuesta < Servidor.NUMERO) {
                        Servidor.textarea.append("---Servidor" + " -> " + respuesta + " es menor que el número oculto!\n");
                    } else {
                        Servidor.textarea.append("---Servidor" + " -> " + respuesta + " es mayor que el número oculto!\n");
                    }

                }
                    EnviarMensaje("historial");
                    
            } catch (IOException ioe) {
                System.out.println("ERROR al recibir de Cliente:\n" + ioe.getMessage());
                    if(fentrada!=null){repeat =false;}
            } catch (ClassNotFoundException cnfe) {
                System.out.println("ERROR al castear la Clase:\n" + cnfe.getMessage());
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
                    mensaje = new Mensaje("jugadores", Servidor.getLista());
                } else {
                    mensaje = new Mensaje("winner", s);
                }
                fsalida.writeObject(mensaje);

            }
        } catch (IOException ex) {
            System.out.println("ASJFIAJGAGJD");
        }

    }

    private void saleJugador() throws IOException {
        socket.close();
    }

}
