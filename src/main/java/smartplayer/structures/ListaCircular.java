/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartplayer.structures;

import java.util.Iterator;
import java.util.NoSuchElementException;

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

    //  Navegacion circular 

    // Devuelve el dato actual y avanza (nunca para) 
    public T siguiente() {
        if (cola == null) return null;
        T dato = actual.dato;
        actual = actual.siguiente;
        return dato;
    }
    public T getActual() { return (actual != null) ? actual.dato : null; }

    // Estado

    public int getTamano()   { return tamano; }
    public boolean isEmpty() { return tamano == 0; }

    public void limpiar() { cola = null; actual = null; tamano = 0; }
    
    //  Iterador (una vuelta) 
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int restantes = tamano;
            Nodo<T> cur = (cola != null) ? cola.siguiente : null;
            @Override public boolean hasNext() { return restantes > 0; }
            @Override public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T d = cur.dato; cur = cur.siguiente; restantes--;
                return d;
            }
        };
    }
    @Override
    public String toString() {
        if (cola == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        Nodo<T> cabeza = cola.siguiente;
        Nodo<T> cur    = cabeza;
        do {
            sb.append(cur.dato);
            cur = cur.siguiente;
            if (cur != cabeza) sb.append(" -> ");
        } while (cur != cabeza);
        return sb.append(" -> (inicio)]").toString();
    }
}
