/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartplayer.structures;

import smartplayer.model.Cancion;

/**
 *Mantiene balance en todo momento mediante rotaciones simples y dobles
 * @author rmari
 */
public class ArbolAVL {
    
      // Nodo interno 
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
      
}
