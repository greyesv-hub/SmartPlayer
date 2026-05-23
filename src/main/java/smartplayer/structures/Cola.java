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
}
