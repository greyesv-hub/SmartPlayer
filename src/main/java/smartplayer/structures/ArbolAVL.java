/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartplayer.structures;

import java.util.ArrayList;
import java.util.List;
import smartplayer.model.Cancion;

/**
 *
 * @author rmari
 */
public class ArbolAVL {

    // ── Nodo interno ───────────────────────────────────────────────────────
    public static class Nodo {
        public Cancion cancion;
        public Nodo izquierdo;
        public Nodo derecho;
        public int altura;
        Nodo(Cancion c) { this.cancion = c; this.altura = 1; }
    }

    private Nodo raiz;
    private int  totalNodos;

    // ── Altura y factor de balance ─────────────────────────────────────────

    private int altura(Nodo n)       { return (n == null) ? 0 : n.altura; }

    private void actualizarAltura(Nodo n) {
        n.altura = 1 + Math.max(altura(n.izquierdo), altura(n.derecho));
    }

    private int factorBalance(Nodo n) {
        return (n == null) ? 0 : altura(n.izquierdo) - altura(n.derecho);
    }

      // ── Rotaciones ─────────────────────────────────────────────────────────

    /** Rotación Simple Derecha (RD) */
    private Nodo rotacionDerecha(Nodo y) {
        Nodo x  = y.izquierdo;
        Nodo T2 = x.derecho;
        x.derecho   = y;
        y.izquierdo = T2;
        actualizarAltura(y);
        actualizarAltura(x);
        return x;
    }

    /** Rotación Simple Izquierda (RI) */
    private Nodo rotacionIzquierda(Nodo x) {
        Nodo y  = x.derecho;
        Nodo T2 = y.izquierdo;
        y.izquierdo = x;
        x.derecho   = T2;
        actualizarAltura(x);
        actualizarAltura(y);
        return y;
    }

    /** Rotación Doble Izquierda-Derecha (RID) */
    private Nodo rotacionIzquierdaDerecha(Nodo n) {
        n.izquierdo = rotacionIzquierda(n.izquierdo);
        return rotacionDerecha(n);
    }

    /** Rotación Doble Derecha-Izquierda (RDI) */
    private Nodo rotacionDerechaIzquierda(Nodo n) {
        n.derecho = rotacionDerecha(n.derecho);
        return rotacionIzquierda(n);
    }

      /** Aplica la rotación correcta según el factor de balance */
    private Nodo balancear(Nodo nodo) {
        actualizarAltura(nodo);
        int fb = factorBalance(nodo);

        // Caso izquierda-izquierda → RD
        if (fb > 1 && factorBalance(nodo.izquierdo) >= 0)
            return rotacionDerecha(nodo);

        // Caso izquierda-derecha → RID
        if (fb > 1 && factorBalance(nodo.izquierdo) < 0)
            return rotacionIzquierdaDerecha(nodo);

        // Caso derecha-derecha → RI
        if (fb < -1 && factorBalance(nodo.derecho) <= 0)
            return rotacionIzquierda(nodo);

        // Caso derecha-izquierda → RDI
        if (fb < -1 && factorBalance(nodo.derecho) > 0)
            return rotacionDerechaIzquierda(nodo);

        return nodo; // ya balanceado
    }

    // ── Inserción ──────────────────────────────────────────────────────────

    public void insertar(Cancion c) {
        raiz = insertarRec(raiz, c);
    }

    private Nodo insertarRec(Nodo nodo, Cancion c) {
        if (nodo == null) { totalNodos++; return new Nodo(c); }
        int cmp = c.compareTo(nodo.cancion);
        if      (cmp < 0) nodo.izquierdo = insertarRec(nodo.izquierdo, c);
        else if (cmp > 0) nodo.derecho   = insertarRec(nodo.derecho,   c);
        else return nodo; // duplicado
        return balancear(nodo);
    }

    // ── Búsqueda ───────────────────────────────────────────────────────────

    public Cancion buscar(String nombre) {
        Nodo n = buscarRec(raiz, nombre);
        return (n != null) ? n.cancion : null;
    }

    private Nodo buscarRec(Nodo n, String nombre) {
        if (n == null) return null;
        int cmp = nombre.compareToIgnoreCase(n.cancion.getNombre());
        if      (cmp < 0) return buscarRec(n.izquierdo, nombre);
        else if (cmp > 0) return buscarRec(n.derecho,   nombre);
        else              return n;
    }

    public List<Cancion> buscarPorArtista(String artista) {
        List<Cancion> r = new ArrayList<>();
        buscarArtistaRec(raiz, artista.toLowerCase(), r);
        return r;
    }

    private void buscarArtistaRec(Nodo n, String a, List<Cancion> r) {
        if (n == null) return;
        if (n.cancion.getArtista().toLowerCase().contains(a)) r.add(n.cancion);
        buscarArtistaRec(n.izquierdo, a, r);
        buscarArtistaRec(n.derecho, a, r);
    }

    public List<Cancion> buscarPorAlbum(String album) {
        List<Cancion> r = new ArrayList<>();
        buscarAlbumRec(raiz, album.toLowerCase(), r);
        return r;
    }

    private void buscarAlbumRec(Nodo n, String al, List<Cancion> r) {
        if (n == null) return;
        if (n.cancion.getAlbum().toLowerCase().contains(al)) r.add(n.cancion);
        buscarAlbumRec(n.izquierdo, al, r);
        buscarAlbumRec(n.derecho, al, r);
    }

    public List<Cancion> buscarPorGenero(String genero) {
        List<Cancion> r = new ArrayList<>();
        buscarGeneroRec(raiz, genero.toLowerCase(), r);
        return r;
    }

    private void buscarGeneroRec(Nodo n, String g, List<Cancion> r) {
        if (n == null) return;
        if (n.cancion.getGenero().toLowerCase().contains(g)) r.add(n.cancion);
        buscarGeneroRec(n.izquierdo, g, r);
        buscarGeneroRec(n.derecho, g, r);
    }

    // ── Modificación ───────────────────────────────────────────────────────

    public boolean modificar(String nombre, Cancion nueva) {
        Nodo n = buscarRec(raiz, nombre);
        if (n == null) return false;
        n.cancion = nueva;
        return true;
    }

    // ── Eliminación ────────────────────────────────────────────────────────

    public boolean eliminar(String nombre) {
        int antes = totalNodos;
        raiz = eliminarRec(raiz, nombre);
        return totalNodos < antes;
    }

    private Nodo eliminarRec(Nodo nodo, String nombre) {
        if (nodo == null) return null;
        int cmp = nombre.compareToIgnoreCase(nodo.cancion.getNombre());
        if      (cmp < 0) nodo.izquierdo = eliminarRec(nodo.izquierdo, nombre);
        else if (cmp > 0) nodo.derecho   = eliminarRec(nodo.derecho,   nombre);
        else {
            totalNodos--;
            if (nodo.izquierdo == null) return nodo.derecho;
            if (nodo.derecho   == null) return nodo.izquierdo;
            Nodo sucesor = minimo(nodo.derecho);
            nodo.cancion = sucesor.cancion;
            nodo.derecho = eliminarRec(nodo.derecho, sucesor.cancion.getNombre());
        }
        return balancear(nodo);
    }

    private Nodo minimo(Nodo n) {
        while (n.izquierdo != null) n = n.izquierdo;
        return n;
    }

    // ── Recorridos ─────────────────────────────────────────────────────────

    public List<Cancion> inOrden() {
        List<Cancion> l = new ArrayList<>(); inOrdenRec(raiz, l); return l;
    }
    private void inOrdenRec(Nodo n, List<Cancion> l) {
        if (n == null) return;
        inOrdenRec(n.izquierdo, l); l.add(n.cancion); inOrdenRec(n.derecho, l);
    }

    public List<Cancion> preOrden() {
        List<Cancion> l = new ArrayList<>(); preOrdenRec(raiz, l); return l;
    }
    private void preOrdenRec(Nodo n, List<Cancion> l) {
        if (n == null) return;
        l.add(n.cancion); preOrdenRec(n.izquierdo, l); preOrdenRec(n.derecho, l);
    }

    public List<Cancion> postOrden() {
        List<Cancion> l = new ArrayList<>(); postOrdenRec(raiz, l); return l;
    }
    private void postOrdenRec(Nodo n, List<Cancion> l) {
        if (n == null) return;
        postOrdenRec(n.izquierdo, l); postOrdenRec(n.derecho, l); l.add(n.cancion);
    }

    // ── Utilidades ─────────────────────────────────────────────────────────

    public int getTotalNodos() { return totalNodos; }
    public boolean isEmpty()   { return raiz == null; }
    public Nodo getRaiz()      { return raiz; }

    public int altura()            { return altura(raiz); }
    public int factorBalanceRaiz() { return factorBalance(raiz); }
}
    
