package smartplayer.structures;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * Lista Simple enlazada genérica.
 * Implementación manual sin uso de java.util.LinkedList.
 */
public class ListaSimple<T> implements Iterable<T> {

    // ── Nodo interno ───────────────────────────────────────────────────────
    private static class Nodo<T> {
        T dato;
        Nodo<T> siguiente;
        Nodo(T dato) { this.dato = dato; }
    }

    private Nodo<T> cabeza;
    private int tamano;

    // ── Inserción ──────────────────────────────────────────────────────────

    public void agregarInicio(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.siguiente = cabeza;
        cabeza = nuevo;
        tamano++;
    }

    public void agregarFinal(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null) { cabeza = nuevo; }
        else {
            Nodo<T> actual = cabeza;
            while (actual.siguiente != null) actual = actual.siguiente;
            actual.siguiente = nuevo;
        }
        tamano++;
    }

    // ── Eliminación ────────────────────────────────────────────────────────

    public T eliminarInicio() {
        if (cabeza == null) throw new NoSuchElementException("Lista vacía");
        T dato = cabeza.dato;
        cabeza = cabeza.siguiente;
        tamano--;
        return dato;
    }

    public boolean eliminar(T dato) {
        if (cabeza == null) return false;
        if (cabeza.dato.equals(dato)) { eliminarInicio(); return true; }
        Nodo<T> actual = cabeza;
        while (actual.siguiente != null) {
            if (actual.siguiente.dato.equals(dato)) {
                actual.siguiente = actual.siguiente.siguiente;
                tamano--;
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    // ── Búsqueda ───────────────────────────────────────────────────────────

    public T buscar(Predicate<T> condicion) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (condicion.test(actual.dato)) return actual.dato;
            actual = actual.siguiente;
        }
        return null;
    }

    public boolean contiene(T dato) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (actual.dato.equals(dato)) return true;
            actual = actual.siguiente;
        }
        return false;
    }

    // ── Acceso ─────────────────────────────────────────────────────────────

    public T getPrimero() {
        if (cabeza == null) return null;
        return cabeza.dato;
    }

    public T get(int indice) {
        if (indice < 0 || indice >= tamano)
            throw new IndexOutOfBoundsException("Índice " + indice);
        Nodo<T> actual = cabeza;
        for (int i = 0; i < indice; i++) actual = actual.siguiente;
        return actual.dato;
    }

    public int getTamano() { return tamano; }
    public boolean isEmpty() { return tamano == 0; }

    public void limpiar() { cabeza = null; tamano = 0; }

    // ── Iterador ───────────────────────────────────────────────────────────

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Nodo<T> actual = cabeza;
            @Override public boolean hasNext() { return actual != null; }
            @Override public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T dato = actual.dato;
                actual = actual.siguiente;
                return dato;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Nodo<T> actual = cabeza;
        while (actual != null) {
            sb.append(actual.dato);
            if (actual.siguiente != null) sb.append(" -> ");
            actual = actual.siguiente;
        }
        return sb.append("]").toString();
    }
}