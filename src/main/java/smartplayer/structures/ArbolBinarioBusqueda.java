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
        raiz = insertarRec(raiz, c);
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
        List<Cancion> resultado = new ArrayList<>();
        buscarArtistaRec(raiz, artista.toLowerCase(), resultado);
        return resultado;
    }

    private void buscarArtistaRec(Nodo n, String artista, List<Cancion> res) {
        if (n == null) return;
        if (n.cancion.getArtista().toLowerCase().contains(artista)) res.add(n.cancion);
        buscarArtistaRec(n.izquierdo, artista, res);
        buscarArtistaRec(n.derecho,   artista, res);
    }

    public List<Cancion> buscarPorAlbum(String album) {
        List<Cancion> resultado = new ArrayList<>();
        buscarAlbumRec(raiz, album.toLowerCase(), resultado);
        return resultado;
    }

    private void buscarAlbumRec(Nodo n, String album, List<Cancion> res) {
        if (n == null) return;
        if (n.cancion.getAlbum().toLowerCase().contains(album)) res.add(n.cancion);
        buscarAlbumRec(n.izquierdo, album, res);
        buscarAlbumRec(n.derecho,   album, res);
    }

    public List<Cancion> buscarPorGenero(String genero) {
        List<Cancion> resultado = new ArrayList<>();
        buscarGeneroRec(raiz, genero.toLowerCase(), resultado);
        return resultado;
    }

    private void buscarGeneroRec(Nodo n, String genero, List<Cancion> res) {
        if (n == null) return;
        if (n.cancion.getGenero().toLowerCase().contains(genero)) res.add(n.cancion);
        buscarGeneroRec(n.izquierdo, genero, res);
        buscarGeneroRec(n.derecho,   genero, res);
    }


    public boolean modificar(String nombre, Cancion nueva) {
        Nodo n = buscarRec(raiz, nombre);
        if (n == null) return false;
        n.cancion = nueva;
        return true;
    }


    public boolean eliminar(String nombre) {
        int[] contador = {0};
        raiz = eliminarRec(raiz, nombre, contador);
        return contador[0] > 0;
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

