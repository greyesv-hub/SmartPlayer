/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartplayer.structures;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

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

        Nodo(T dato) {
            this.dato = dato;
        }
    }

    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private Nodo<T> actual;   // puntero de navegacion 
    private int tamano;

    // Insercion 
    public void agregarInicio(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = cola = nuevo;
        } else {
            nuevo.siguiente = cabeza;
            cabeza.anterior = nuevo;
            cabeza = nuevo;
        }
        if (actual == null) {
            actual = cabeza;
        }
        tamano++;// Si no hay nodo actual: se coloca al inicio
    }

    public void agregarFinal(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cola == null) {
            cabeza = cola = nuevo;
        } else {
            cola.siguiente = nuevo;
            nuevo.anterior = cola;
            cola = nuevo;
        }
        if (actual == null) {
            actual = cabeza;
        }
        tamano++;
    }

    // Eliminacion
    public boolean eliminarPorValor(T dato) {//Busca y elimina un dato
        if (dato == null) {
            return false;
        }
        Nodo<T> n = buscarNodo(d -> d.equals(dato));//buscar un nodo cuyo dato sea igual al recibido
        if (n == null) {
            return false;
        }
        eliminarNodo(n);
        return true;
    }

    private void eliminarNodo(Nodo<T> n) {
        if (n.anterior != null) {
            n.anterior.siguiente = n.siguiente;
        } else {
            cabeza = n.siguiente;
        }
        if (n.siguiente != null) {
            n.siguiente.anterior = n.anterior;
        } else {
            cola = n.anterior;
        }
        if (actual == n) {
            actual = (n.siguiente != null) ? n.siguiente : n.anterior;
        }
        tamano--;
    }

    // Regresa el dato actual y avanza al siguiente 
    public T siguiente() {
        if (actual == null) {
            return null;
        }
        T dato = actual.dato;
        actual = actual.siguiente;
        return dato;
    }
    // Regresa el dato actual y retrocede al anterior 

    public T anterior() {
        if (actual == null) {
            return null;
        }
        T dato = actual.dato;
        actual = actual.anterior;
        return dato;
    }

    public void establecerActual(T dato) {

        Nodo<T> cur = cabeza;

        while (cur != null) {

            if (cur.dato.equals(dato)) {
                actual = cur;
                return;
            }

            cur = cur.siguiente;
        }
    }

    public T getActual() {
        return (actual != null) ? actual.dato : null;
    }

    public void irAlInicio() {
        actual = cabeza;
    }

    public void irAlFinal() {
        actual = cola;
    }

    public boolean hayAnterior() {

        return actual != null && actual.anterior != null;
    }

    public boolean haySiguiente() {

        return actual != null && actual.siguiente != null;
    }

    // Buscar
    public T buscar(Predicate<T> condicion) {//Devuelve el dato actual y avanza
        Nodo<T> n = buscarNodo(condicion);
        return (n != null)
                ? n.dato : null;
    }

    private Nodo<T> buscarNodo(Predicate<T> condicion) {
        Nodo<T> cur = cabeza;
        while (cur != null) {
            if (condicion.test(cur.dato)) {
                return cur;
            }
            cur = cur.siguiente;
        }
        return null;
    }

    //  Acceso 
    public T getPrimero() {
        return (cabeza != null) ? cabeza.dato : null;
    }

    public T getUltimo() {
        return (cola != null) ? cola.dato : null;
    }

    public int getTamano() {
        return tamano;
    }

    public boolean isEmpty() {
        return tamano == 0;
    }

    public void limpiar() {
        cabeza = cola = actual = null;
        tamano = 0;
    }

    // Iterador
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Nodo<T> cur = cabeza;

            @Override
            public boolean hasNext() {
                return cur != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T d = cur.dato;
                cur = cur.siguiente;
                return d;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Nodo<T> cur = cabeza;
        while (cur != null) {
            sb.append(cur.dato);
            if (cur.siguiente != null) {
                sb.append(" <-> ");
            }
            cur = cur.siguiente;
        }
        return sb.append("]").toString();
    }
}
