/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartplayer.structures;

/**
 *
 * @author rmari
 */
public class ListaCircular <T> implements Iterable <T> {
     
    private static class Nodo <T> {
        T dato;
        Nodo<T> siguiente;
        Nodo(T dato) { this.dato = dato; }
    }

    private Nodo<T> cola;   // cola.siguiente = cabeza
    private Nodo<T> actual; // puntero de reproduccion
    private int tamano;

    //  Insercion 
    public void agregar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cola == null) {
            nuevo.siguiente = nuevo;   // apunta a si mismo
            cola = nuevo;
        } else {
            nuevo.siguiente = cola.siguiente;
            cola.siguiente  = nuevo;
            cola = nuevo;
        }
        if (actual == null) actual = cola.siguiente; // cabeza
        tamano++;
    }
    
      //  Eliminacion 
    public boolean eliminar(T dato) {
        if (cola == null) return false;
        Nodo<T> cabeza = cola.siguiente;
        Nodo<T> prev   = cola;
        Nodo<T> cur    = cabeza;
        do {
            if (cur.dato.equals(dato)) {
                if (tamano == 1) { cola = null; actual = null; tamano = 0; 
                return true; }
                prev.siguiente = cur.siguiente;
                if (cur == cola) cola = prev;
                if (actual == cur) actual = cur.siguiente; tamano--;
                return true;
            }
            prev = cur;
            cur  = cur.siguiente;
        } while (cur != cabeza);
        return false;
    }

}
