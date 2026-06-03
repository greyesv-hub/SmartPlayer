/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartplayer.structures;

import java.util.ArrayList;
import java.util.List;
import smartplayer.model.Cancion;

/**
 *Mantiene balance en todo momento mediante rotaciones simples y dobles
 * @author rmari
 */
public class ArbolAVL {

    public static class Nodo {
        public Cancion cancion;
        public Nodo izquierdo;
        public Nodo derecho;
        public int altura;
        Nodo(Cancion c) { this.cancion = c; this.altura = 1; }
    }

    private Nodo raiz;
    private int  totalNodos;

    private int altura(Nodo n){        
    if (n == null) {
        return 0;
    } else {
        return n.altura;
      } 
    }
    
    private void actualizarAltura(Nodo n) {    
    int alturaIzquierda = altura(n.izquierdo);
    int alturaDerecha = altura(n.derecho);

    if (alturaIzquierda > alturaDerecha) {
        n.altura = alturaIzquierda + 1;
    } else {
        n.altura = alturaDerecha + 1;
      }
    }
    
    private int factorBalance(Nodo n) {
    if (n == null) {
        return 0;
    } else {
        return altura(n.izquierdo) - altura(n.derecho);
      }
    }
    private Nodo rotacionDerecha(Nodo nodo) {

      Nodo nuevoPadre = nodo.izquierdo;
      Nodo subArbol = nuevoPadre.derecho;

      nuevoPadre.derecho = nodo;
      nodo.izquierdo = subArbol;

      actualizarAltura(nodo);
      actualizarAltura(nuevoPadre);

      return nuevoPadre;
    }
      private Nodo rotacionIzquierda(Nodo nodo) {

      Nodo nuevoPadre = nodo.derecho;
      Nodo subArbol = nuevoPadre.izquierdo;

      nuevoPadre.izquierdo = nodo;
      nodo.derecho = subArbol;

      actualizarAltura(nodo);
      actualizarAltura(nuevoPadre);

      return nuevoPadre;
     }
      
    private Nodo rotacionIzquierdaDerecha(Nodo n) {
        n.izquierdo = rotacionIzquierda(n.izquierdo);
        return rotacionDerecha(n);
    }

    private Nodo rotacionDerechaIzquierda(Nodo n) {
        n.derecho = rotacionDerecha(n.derecho);
        return rotacionIzquierda(n);
    }

    private Nodo balancear(Nodo nodo) {
        actualizarAltura(nodo);
        int fb = factorBalance(nodo);

        if (fb > 1 && factorBalance(nodo.izquierdo) >= 0)
            return rotacionDerecha(nodo);

        if (fb > 1 && factorBalance(nodo.izquierdo) < 0)
            return rotacionIzquierdaDerecha(nodo);

        if (fb < -1 && factorBalance(nodo.derecho) <= 0)
            return rotacionIzquierda(nodo);

        if (fb < -1 && factorBalance(nodo.derecho) > 0)
            return rotacionDerechaIzquierda(nodo);

        return nodo; 
    }
      public void insertar(Cancion cancion) {
      raiz = insertarNodo(raiz, cancion);
    }

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
    } else {
        return nodo; // la canción ya existe
    }

        return balancear(nodo);
    }

    // Buscar una cancion por nombre

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

    if (nodo.cancion.getAlbum().toLowerCase().contains(album)) {
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

    int cantidadAntes = totalNodos;
    raiz = eliminarNodo(raiz, nombre);

    return totalNodos < cantidadAntes;
    }

    private Nodo eliminarNodo(Nodo nodo, String nombre) {

    if (nodo == null) {
        return null;
    }

    int resultado = nombre.compareToIgnoreCase(nodo.cancion.getNombre());

    if (resultado < 0) {
        nodo.izquierdo = eliminarNodo(nodo.izquierdo, nombre);
    } else if (resultado > 0) {
        nodo.derecho = eliminarNodo(nodo.derecho, nombre);
    } else {
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
        Nodo reemplazo = obtenerMenor(nodo.derecho);
        nodo.cancion = reemplazo.cancion;
        nodo.derecho = eliminarNodo(
                nodo.derecho,
                reemplazo.cancion.getNombre()
        );
    }

    return balancear(nodo);
    }

    private Nodo obtenerMenor(Nodo nodo) {

    while (nodo.izquierdo != null) {
        nodo = nodo.izquierdo;
    }

    return nodo;
    }
    
    public List<Cancion> inOrden() {
    List<Cancion> canciones = new ArrayList<>();
    recorrerInOrden(raiz, canciones);

    return canciones;
    }

    private void recorrerInOrden(Nodo nodo, List<Cancion> canciones) {

    if (nodo == null) {
        return;
    }
    recorrerInOrden(nodo.izquierdo, canciones);
    canciones.add(nodo.cancion);
    recorrerInOrden(nodo.derecho, canciones);
    }

    public List<Cancion> preOrden() {

    List<Cancion> canciones = new ArrayList<>();
    recorrerPreOrden(raiz, canciones);

    return canciones;
    }

    private void recorrerPreOrden(Nodo nodo, List<Cancion> canciones) {

    if (nodo == null) {
        return;
    }
    canciones.add(nodo.cancion);
    recorrerPreOrden(nodo.izquierdo, canciones);
    recorrerPreOrden(nodo.derecho, canciones);
    }

    public List<Cancion> postOrden() {

    List<Cancion> canciones = new ArrayList<>();
    recorrerPostOrden(raiz, canciones);

    return canciones;
    }

    private void recorrerPostOrden(Nodo nodo, List<Cancion> canciones) {

    if (nodo == null) {
        return;
    }
    recorrerPostOrden(nodo.izquierdo, canciones);
    recorrerPostOrden(nodo.derecho, canciones);
    canciones.add(nodo.cancion);
    }

    public int getTotalNodos() {
    return totalNodos;
    }

    public boolean estaVacio() {
    return raiz == null;
    }

    public Nodo getRaiz() {
    return raiz;
    }

    public int getAltura() {
    return altura(raiz);
    }

    public int getFactorBalanceRaiz() {
    return factorBalance(raiz);
    }
}
