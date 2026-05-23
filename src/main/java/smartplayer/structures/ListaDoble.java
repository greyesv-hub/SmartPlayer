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
  } 
