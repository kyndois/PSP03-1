package psp03_tarea01;

public class Mensaje {

    private String name;
    private String texto;
    private int respuesta;

    public Mensaje(String name, String texto) {
        this.name = name;
        this.texto = texto;
    }

    public Mensaje(String name, int respuesta) {
        this.name = name;
        this.respuesta = respuesta;
    }

    public String getName() {
        return name;
    }

    public String getTexto() {
        return texto;
    }

    public int getRespuesta() {
        return respuesta;
    }
}
