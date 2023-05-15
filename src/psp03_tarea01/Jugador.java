
package psp03_tarea01;

import java.io.ObjectOutputStream;


public class Jugador {
    
    Object o;
    String name;
    
    public Jugador(String name, Object o){
        this.name=name;
        this.o=o;
    }
    
    public void setName(String name){
        this.name=name;
    }
    public String getName(){
        return name;
    }
    
    public ObjectOutputStream getStream(){
        return (ObjectOutputStream)o;
    }
}
