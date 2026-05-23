/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartplayer.structures;

/**
 *
 * @author rmari
 */
public class ListaDoble<T> implements Iterable<T> {

    // Nodo interno 
    public static class Nodo<T> {
        public T dato;
        public Nodo<T> siguiente;
        public Nodo<T> anterior;
        Nodo(T dato) { this.dato = dato; }
    }

    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private Nodo<T> actual;   // puntero de navegacion 
    private int tamano;
    
      // Insercion 

    public void agregarInicio(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null) { cabeza = cola = nuevo; }
        else {
            nuevo.siguiente = cabeza;
            cabeza.anterior = nuevo;
            cabeza = nuevo;
        }
        if (actual == null) actual = cabeza;
        tamano++;
    }

    public void agregarFinal(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cola == null) { cabeza = cola = nuevo; }
        else {
            cola.siguiente = nuevo;
            nuevo.anterior = cola;
            cola = nuevo;
        }
        if (actual == null) actual = cabeza;
        tamano++;
    }
    
    // Eliminacion

    public boolean eliminarPorValor(T dato) {
        if (dato == null) return false;
        Nodo<T> n = buscarNodo(d -> d.equals(dato));
        if (n == null) return false;
        eliminarNodo(n);
        return true;
    }

    private void eliminarNodo(Nodo<T> n) {
        if (n.anterior != null) n.anterior.siguiente = n.siguiente;
        else cabeza = n.siguiente;
        if (n.siguiente != null) n.siguiente.anterior = n.anterior;
        else cola = n.anterior;
        if (actual == n) actual = (n.siguiente != null) ? n.siguiente : n.anterior;
        tamano--;
    }
    
    // Regresa el dato actual y avanza al siguiente 
    public T siguiente() {
        if (actual == null) return null;
        T dato = actual.dato;
        actual = actual.siguiente;
        return dato;
    }
  } 

