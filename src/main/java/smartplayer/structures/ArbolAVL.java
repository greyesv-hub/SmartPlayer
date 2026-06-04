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

    public static class Nodo {

    public Cancion cancion;
    public Nodo izquierdo;
    public Nodo derecho;
    public int altura;

    Nodo(Cancion c) {
        cancion = c;
        altura = 1;
       }
    }

    private Nodo raiz;
    private int totalNodos;

    private int altura(Nodo n) {
    if (n == null) {
        return 0;
    }

    return n.altura;
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
    }

    return altura(n.izquierdo) - altura(n.derecho);
    }

    private Nodo rotacionDerecha(Nodo y) {
    Nodo x = y.izquierdo;
    Nodo T2 = x.derecho;

    x.derecho = y;
    y.izquierdo = T2;

    actualizarAltura(y);
    actualizarAltura(x);

    return x;
    }

    private Nodo rotacionIzquierda(Nodo x) {
    Nodo y = x.derecho;
    Nodo T2 = y.izquierdo;

    y.izquierdo = x;
    x.derecho = T2;

    actualizarAltura(x);
    actualizarAltura(y);

    return y;
    }

    private Nodo rotacionIzquierdaDerecha(Nodo n) {

    Nodo hijoIzquierdo = n.izquierdo;
    n.izquierdo = rotacionIzquierda(hijoIzquierdo);

    return rotacionDerecha(n);
    }

    private Nodo rotacionDerechaIzquierda(Nodo n) {

    Nodo hijoDerecho = n.derecho;
    n.derecho = rotacionDerecha(hijoDerecho);

    return rotacionIzquierda(n);
    }

    private Nodo balancear(Nodo nodo) {actualizarAltura(nodo);
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

    public void insertar(Cancion c) {
        raiz = insertarRec(raiz, c);
    }

    private Nodo insertarRec(Nodo nodo, Cancion c) {
        if (nodo == null) {
        totalNodos++;
        return new Nodo(c);
    }

    int comp = c.compareTo(nodo.cancion);
    if (comp < 0) {
        nodo.izquierdo = insertarRec(nodo.izquierdo,c);
    } else if (comp > 0) {
        nodo.derecho = insertarRec(nodo.derecho,c);
    } else {
        return nodo;
    }
    return balancear(nodo);
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

    List<Cancion> cancionesEncontradas = new ArrayList<>();
    buscarArtistaRec(raiz, artista.toLowerCase(), cancionesEncontradas);

    return cancionesEncontradas;
    }

    private void buscarArtistaRec(Nodo nodo,String artista,List<Cancion> cancionesEncontradas) {

       if (nodo == null) {
        return;
    }

       if (nodo.cancion.getArtista().toLowerCase().contains(artista)) {
        cancionesEncontradas.add(nodo.cancion);
    }

    buscarArtistaRec(nodo.izquierdo, artista, cancionesEncontradas);
    buscarArtistaRec(nodo.derecho, artista, cancionesEncontradas);
        }

        public List<Cancion> buscarPorAlbum(String album) {

         List<Cancion> cancionesEncontradas = new ArrayList<>();
         buscarAlbumRec(raiz, album.toLowerCase(), cancionesEncontradas);

         return cancionesEncontradas;
    }

        private void buscarAlbumRec(Nodo nodo, String album, List<Cancion> cancionesEncontradas) {

        if (nodo == null) {
         return;
    }

        if (nodo.cancion.getAlbum().toLowerCase().contains(album)) {
         cancionesEncontradas.add(nodo.cancion);
      }

        buscarAlbumRec(nodo.izquierdo, album, cancionesEncontradas);
        buscarAlbumRec(nodo.derecho, album, cancionesEncontradas);
    }

        public List<Cancion> buscarPorGenero(String genero) {List<Cancion> cancionesEncontradas = new ArrayList<>();

        buscarGeneroRec(raiz, genero.toLowerCase(), cancionesEncontradas);
 
        return cancionesEncontradas;
      }

         private void buscarGeneroRec(Nodo nodo, String genero, List<Cancion> cancionesEncontradas) {

         if (nodo == null) {
         return;
       }

         if (nodo.cancion.getGenero().toLowerCase().contains(genero)) {cancionesEncontradas.add(nodo.cancion);
      }

         buscarGeneroRec(nodo.izquierdo, genero, cancionesEncontradas);
         buscarGeneroRec(nodo.derecho, genero, cancionesEncontradas);
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

         int cantidadAntes = totalNodos;
         raiz = eliminarRec(raiz, nombre);

         return totalNodos < cantidadAntes;
    }

         private Nodo eliminarRec(Nodo nodo, String nombre) {

         if (nodo == null) {
         return null;
    }

         int resultado = nombre.compareToIgnoreCase(nodo.cancion.getNombre());

         if (resultado < 0) {
         nodo.izquierdo = eliminarRec(nodo.izquierdo,nombre);
         }else if (resultado > 0) {
         nodo.derecho = eliminarRec(nodo.derecho,nombre);
         }else{
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
         nodo.derecho = eliminarRec(nodo.derecho,sucesor.cancion.getNombre());
        }

         return balancear(nodo);
    }

         private Nodo minimo(Nodo n) {
            while (n.izquierdo != null)
              n = n.izquierdo;
          return n;
    }

         public List<Cancion> inOrden() {List<Cancion> lista = new ArrayList<>();

         inOrdenRec(raiz, lista);
         return lista;
    }

         private void inOrdenRec(Nodo nodo, List<Cancion> lista) {
 
         if (nodo == null) {
         return;
    }
         inOrdenRec(nodo.izquierdo, lista);
         lista.add(nodo.cancion);
         inOrdenRec(nodo.derecho, lista);
    }

         public List<Cancion> preOrden() {List<Cancion> lista = new ArrayList<>();

         preOrdenRec(raiz, lista);
         return lista;
    }

         private void preOrdenRec(Nodo nodo, List<Cancion> lista) {

         if (nodo == null) {
         return;
    }
         lista.add(nodo.cancion);
         preOrdenRec(nodo.izquierdo, lista);
         preOrdenRec(nodo.derecho, lista);
    }

         public List<Cancion> postOrden() {List<Cancion> lista = new ArrayList<>();

         postOrdenRec(raiz, lista);
         return lista;
    }

         private void postOrdenRec(Nodo nodo, List<Cancion> lista) {

         if (nodo == null) {
         return;
    }
         postOrdenRec(nodo.izquierdo, lista);
         postOrdenRec(nodo.derecho, lista);
         lista.add(nodo.cancion);
    }

// ── Utilidades ─────────────────────────────────────────────────────────

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
    return altura(raiz);
}

public int factorBalanceRaiz() {
    return factorBalance(raiz);
} 
}
    
