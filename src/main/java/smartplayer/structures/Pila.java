/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartplayer.structures;

import java.util.EmptyStackException;

/**
 *Usada para el historial de reproduccion 
 * @author rmari
 */
public class Pila <T> {

    private static class Nodo<T> {
        T dato;
        Nodo<T> siguiente;
        Nodo(T dato) { this.dato = dato; }
    }

    private Nodo<T> tope;
    private int tamano;

    // Apila un elemento 
    public void push(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.siguiente = tope;
        tope = nuevo; tamano++;
    }
        // desapila el elemento del tope
    public T pop() {
        if (isEmpty()) throw new EmptyStackException();
        T dato = tope.dato;
        tope = tope.siguiente;
        tamano--;
        return dato;
    }

    // devuelve el tope sin desapilar 
    public T peek() {
        if (isEmpty()) throw new EmptyStackException();
        return tope.dato;
    }

    public boolean isEmpty() { return tope == null; }
    public int getTamano()   { return tamano; }

    public void limpiar() { tope = null; tamano = 0; }

    /**
     * Muestra todos los elementos de la pila (tope a base),
     * sin modificar la pila.
     */
    public String mostrarTodos() {
        StringBuilder sb = new StringBuilder();
        Nodo<T> cur = tope;
        int i = 1;
        while (cur != null) {
            sb.append(i++).append(". ").append(cur.dato).append("\n");
            cur = cur.siguiente;
        }
        return sb.toString();
    }
    @Override
    public String toString() {
        return "Pila(tope=" + (tope != null ? tope.dato : "null") +
               ", tamaño=" + tamano + ")";
    }
}
