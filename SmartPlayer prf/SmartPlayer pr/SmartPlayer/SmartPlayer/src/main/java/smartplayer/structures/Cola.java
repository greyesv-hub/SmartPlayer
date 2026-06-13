/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartplayer.structures;

import java.util.NoSuchElementException;

/**
 *
 * @author rmari
 */
public class Cola <T> {
    

    private static class Nodo<T> {
        T dato;
        Nodo<T> siguiente;
        Nodo(T dato) { this.dato = dato; }
    }

    private Nodo<T> frente;
    private Nodo<T> final_;
    private int tamano;

    // Encola un elemento al final 
    public void enqueue(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (final_ == null) { frente = final_ = nuevo; }
        else { final_.siguiente = nuevo; final_ = nuevo; }
        tamano++;
    }

    // Desencola el elemento del frente 
    public T dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Cola vacía");
        T dato = frente.dato;
        frente = frente.siguiente;
        if (frente == null) final_ = null;
        tamano--;
        return dato;
    }
      // Devuelve el frente sin desencolarlo 
    public T peek() {
        if (isEmpty()) throw new NoSuchElementException("Cola vacía");
        return frente.dato;
    }

    public boolean isEmpty() { return frente == null; }
    public int getTamano()   { return tamano; }

    public void limpiar() { frente = final_ = null; tamano = 0; }

    // Muestra todos los elementos de la cola (frente → final) 
    public String mostrarTodos() {
        StringBuilder sb = new StringBuilder();
        Nodo<T> cur = frente;
        int i = 1;
        while (cur != null) {
            sb.append(i++).append(". ").append(cur.dato).append("\n");
            cur = cur.siguiente;
        }
        return sb.toString();
    }
    @Override
    public String toString() {
        return "Cola(frente=" + (frente != null ? frente.dato : "null") +
               ", tamaño=" + tamano + ")";
    }
}
