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

    private Nodo insertarRec(Nodo nodo, Cancion c) {
    if (nodo == null) {
        totalNodos++;
        return new Nodo(c);
    }
    int resultado = c.compareTo(nodo.cancion);
    if (resultado < 0) {
        nodo.izquierdo = insertarRec(nodo.izquierdo, c);
    }else if (resultado > 0) {
        nodo.derecho = insertarRec(nodo.derecho, c);
    }
    return nodo;
    }
    
    public Cancion buscar(String nombre) {

    Nodo encontrado = buscarRec(raiz, nombre);
    if (encontrado != null) {
        return encontrado.cancion;
    }
    return null;
}

    private Nodo buscarRec(Nodo nodo, String nombre) {

    if (nodo == null) {
        return null;
    }

    int resultado = nombre.compareToIgnoreCase(nodo.cancion.getNombre());

    if (resultado < 0) {
        return buscarRec(nodo.izquierdo, nombre);
    }

    if (resultado > 0) {
        return buscarRec(nodo.derecho, nombre);
    }
    return nodo;
}

    public List<Cancion> buscarPorArtista(String artista) {

    List<Cancion> resultado = new ArrayList<>();
    buscarArtistaRec(raiz, artista.toLowerCase(), resultado);
    return resultado;
}

    private void buscarArtistaRec(Nodo nodo, String artista, List<Cancion> resultado) {

    if (nodo == null) {
        return;
    }

    if (nodo.cancion.getArtista().toLowerCase().contains(artista)) {
        resultado.add(nodo.cancion);
    }

    buscarArtistaRec(nodo.izquierdo, artista, resultado);
    buscarArtistaRec(nodo.derecho, artista, resultado);
}

    public List<Cancion> buscarPorAlbum(String album) {List<Cancion> resultado = new ArrayList<>();

    buscarAlbumRec(raiz,album.toLowerCase(),resultado);
    return resultado;
}

    private void buscarAlbumRec(Nodo nodo, String album, List<Cancion> resultado) {

    if (nodo == null) {
        return;
    }

    if (nodo.cancion.getAlbum().toLowerCase().contains(album)) {
        resultado.add(nodo.cancion);
    }

    buscarAlbumRec(nodo.izquierdo,album,resultado);
    buscarAlbumRec(nodo.derecho, album, resultado);
}

    public List<Cancion> buscarPorGenero(String genero) {List<Cancion> resultado = new ArrayList<>();

    buscarGeneroRec(raiz, genero.toLowerCase(), resultado);
    return resultado;
}

    private void buscarGeneroRec(Nodo nodo,String genero,List<Cancion> resultado) {

    if (nodo == null) {
        return;
    }

    if (nodo.cancion.getGenero().toLowerCase().contains(genero)) {
        resultado.add(nodo.cancion);
    }

    buscarGeneroRec(nodo.izquierdo, genero, resultado);
    buscarGeneroRec(nodo.derecho, genero, resultado);
}

    public boolean modificar(String nombre, Cancion nueva) {

    Nodo encontrado = buscarRec(raiz, nombre);
    if (encontrado == null) {
        return false;
    }
    encontrado.cancion = nueva;
    return true;
}

    public boolean eliminar(String nombre) {

    int[] contador = {0};
    raiz = eliminarRec(raiz, nombre, contador);
    return contador[0] > 0;
}

    private Nodo eliminarRec(Nodo nodo, String nombre, int[] contador) {

    if (nodo == null) {
        return null;
    }

    int resultado = nombre.compareToIgnoreCase(nodo.cancion.getNombre());

    if (resultado < 0) {
        nodo.izquierdo = eliminarRec(nodo.izquierdo, nombre, contador);
    }else if (resultado > 0) {
        nodo.derecho = eliminarRec(nodo.derecho, nombre, contador);
    }else{
        contador[0]++;
        totalNodos--;

        // Solo tiene hijo derecho
        if (nodo.izquierdo == null) {
            return nodo.derecho;
        }

        // Solo tiene hijo izquierdo
        if (nodo.derecho == null) {
            return nodo.izquierdo;
        }

        // Tiene dos hijos
        Nodo sucesor = minimo(nodo.derecho);
        nodo.cancion = sucesor.cancion;
        nodo.derecho = eliminarRec(nodo.derecho, sucesor.cancion.getNombre(), new int[]{0});
    }
    return nodo;
}

    private Nodo minimo(Nodo n) {
        while (n.izquierdo != null) 
            n = n.izquierdo;
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

    public int getTotalNodos() {
    return totalNodos;
    }

    public boolean isEmpty() {
    return raiz == null;
   }

   public Nodo getRaiz() {
    return raiz;
   }

   public int altura() {
    return alturaRec(raiz);
   }

   private int alturaRec(Nodo nodo) {

    if (nodo == null) {
        return 0;
    }

    return 1 + Math.max(alturaRec(nodo.izquierdo), alturaRec(nodo.derecho)
    );
  }
}
    
