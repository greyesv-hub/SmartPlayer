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
public class ArbolBinarioBusqueda {
    
    public static class Nodo {
        public Cancion cancion;
        public Nodo izquierdo;
        public Nodo derecho;
        Nodo(Cancion c) { this.cancion = c; }
    }

    private Nodo raiz;
    private int  totalNodos;

    public void insertar(Cancion c) {
        raiz = insertarNodo(raiz, c);
    }

   // Insertar una cancion en el arbol
   private Nodo insertarNodo(Nodo nodo, Cancion cancion) {

    if (nodo == null) {
        totalNodos++;
        return new Nodo(cancion);
    }

    int resultado = cancion.compareTo(nodo.cancion);

    if (resultado < 0) {
        nodo.izquierdo = insertarNodo(nodo.izquierdo, cancion);
    } else if (resultado > 0) {
        nodo.derecho = insertarNodo(nodo.derecho, cancion);
    }

    return nodo;
    }

    public Cancion buscar(String nombre) {
    Nodo encontrado = buscarNodo(raiz, nombre);

    if (encontrado != null) {
        return encontrado.cancion;
    }

    return null;
    }

    private Nodo buscarNodo(Nodo nodo, String nombre) {

    if (nodo == null) {
        return null;
    }

    int resultado = nombre.compareToIgnoreCase(
            nodo.cancion.getNombre());

    if (resultado < 0) {
        return buscarNodo(nodo.izquierdo, nombre);
    }

    if (resultado > 0) {
        return buscarNodo(nodo.derecho, nombre);
    }

    return nodo;
    }

    public List<Cancion> buscarPorArtista(String artista) {

    List<Cancion> cancionesEncontradas = new ArrayList<>();
    buscarArtista(raiz, artista.toLowerCase(), cancionesEncontradas);

    return cancionesEncontradas;
}

    private void buscarArtista(Nodo nodo, String artista, List<Cancion> cancionesEncontradas) {

    if (nodo == null) {
        return;
    }

    if (nodo.cancion.getArtista().toLowerCase().contains(artista)) {
        cancionesEncontradas.add(nodo.cancion);
    }

    buscarArtista(nodo.izquierdo, artista, cancionesEncontradas);
    buscarArtista(nodo.derecho, artista, cancionesEncontradas);
    }

    public List<Cancion> buscarPorAlbum(String album) {

    List<Cancion> cancionesEncontradas = new ArrayList<>();
    buscarAlbum(raiz, album.toLowerCase(), cancionesEncontradas);

    return cancionesEncontradas;
    }

    private void buscarAlbum(Nodo nodo, String album, List<Cancion> cancionesEncontradas) {

    if (nodo == null) {
        return;
    }

    if (nodo.cancion.getAlbum() .toLowerCase().contains(album)) {
        cancionesEncontradas.add(nodo.cancion);
    }
    buscarAlbum(nodo.izquierdo, album, cancionesEncontradas);
    buscarAlbum(nodo.derecho, album, cancionesEncontradas);
    }

    public List<Cancion> buscarPorGenero(String genero) {

    List<Cancion> cancionesEncontradas = new ArrayList<>();

    buscarGenero(raiz, genero.toLowerCase(), cancionesEncontradas);
    return cancionesEncontradas;
    }

    private void buscarGenero(Nodo nodo, String genero, List<Cancion> cancionesEncontradas) {

    if (nodo == null) {
        return;
    }

    if (nodo.cancion.getGenero().toLowerCase().contains(genero)) {
        cancionesEncontradas.add(nodo.cancion);
    }

    buscarGenero(nodo.izquierdo, genero, cancionesEncontradas);
    buscarGenero(nodo.derecho, genero, cancionesEncontradas);
    }

    public boolean modificar(String nombre, Cancion nuevaCancion) {
    Nodo encontrado = buscarNodo(raiz, nombre);

    if (encontrado == null) {
        return false;
    }
    encontrado.cancion = nuevaCancion;
    return true;
    }

    public boolean eliminar(String nombre) {

    int[] cancionesEliminadas = {0};
    raiz = eliminarNodo(raiz,nombre, cancionesEliminadas
    );

    return cancionesEliminadas[0] > 0;
    }

    private Nodo eliminarRec(Nodo nodo, String nombre, int[] cont) {
        if (nodo == null) return null;
        int cmp = nombre.compareToIgnoreCase(nodo.cancion.getNombre());
        if (cmp < 0) nodo.izquierdo = eliminarRec(nodo.izquierdo, nombre, cont);
        else if (cmp > 0) nodo.derecho = eliminarRec(nodo.derecho, nombre, cont);
        else {
            cont[0]++;
            totalNodos--;
            if (nodo.izquierdo == null) return nodo.derecho;
            if (nodo.derecho   == null) return nodo.izquierdo;
            // Sucesor inorden (mínimo del subárbol derecho)
            Nodo sucesor = minimo(nodo.derecho);
            nodo.cancion = sucesor.cancion;
            nodo.derecho = eliminarRec(nodo.derecho, sucesor.cancion.getNombre(), new int[]{0});
        }
        return nodo;
    }

    private Nodo minimo(Nodo n) {
        while (n.izquierdo != null) n = n.izquierdo;
        return n;
    }

    public List<Cancion> inOrden() {
        List<Cancion> lista = new ArrayList<>();
        inOrdenRec(raiz, lista);
        return lista;
    }

    private void inOrdenRec(Nodo n, List<Cancion> lista) {
        if (n == null) return;
        inOrdenRec(n.izquierdo, lista);
        lista.add(n.cancion);
        inOrdenRec(n.derecho, lista);
    }

    public List<Cancion> preOrden() {
        List<Cancion> lista = new ArrayList<>();
        preOrdenRec(raiz, lista);
        return lista;
    }

    private void preOrdenRec(Nodo n, List<Cancion> lista) {
        if (n == null) return;
        lista.add(n.cancion);
        preOrdenRec(n.izquierdo, lista);
        preOrdenRec(n.derecho, lista);
    }

    public List<Cancion> postOrden() {
        List<Cancion> lista = new ArrayList<>();
        postOrdenRec(raiz, lista);
        return lista;
    }

    private void postOrdenRec(Nodo n, List<Cancion> lista) {
        if (n == null) return;
        postOrdenRec(n.izquierdo, lista);
        postOrdenRec(n.derecho, lista);
        lista.add(n.cancion);
    }

    public int getTotalNodos() { return totalNodos; }
    public boolean isEmpty()   { return raiz == null; }
    public Nodo getRaiz()      { return raiz; }

    public int altura() { return alturaRec(raiz); }

    private int alturaRec(Nodo n) {
        if (n == null) return 0;
        return 1 + Math.max(alturaRec(n.izquierdo), alturaRec(n.derecho));
    }
}

