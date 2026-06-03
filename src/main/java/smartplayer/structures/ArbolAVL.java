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
      
      
}
