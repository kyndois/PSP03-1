package psp03_tarea01;

import java.io.Serializable;
import java.util.ArrayList;

public class Mensaje implements Serializable {

    private String name;
    private String tipo = "mensaje";
    private String texto;
    private int respuesta;
    ArrayList<String> lista = new ArrayList<>();

    public Mensaje(String tipo, String texto) {
        this.tipo = tipo;
        this.texto = texto;
    }

    public Mensaje(String name, int respuesta) {
        this.name = name;
        this.respuesta = respuesta;
    }

    public Mensaje(String tipo, ArrayList<String> lista) {
        this.tipo = tipo;
        this.lista = lista;
    }

    public String getName() {
        return name;
    }

    public String getTipo() {
        return tipo;
    }

    public String getTexto() {
        return texto;
    }

    public int getRespuesta() {
        return respuesta;
    }
    
    public ArrayList<String> getLista(){
        return lista;
    }
}
