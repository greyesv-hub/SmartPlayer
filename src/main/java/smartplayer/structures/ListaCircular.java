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
    private Nodo<T> actual; // puntero de reproducción
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
}
