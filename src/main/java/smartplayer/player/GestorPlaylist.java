/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartplayer.player;

import java.util.ArrayList;
import java.util.List;
import smartplayer.model.Cancion;
import smartplayer.model.Playlist;
import smartplayer.structures.ListaSimple;

/**
 *
 * @author rmari
 */
public class GestorPlaylist {
    
     private final ListaSimple<Playlist> playlists = new ListaSimple<>();

    // CRUD 
    public Playlist crearPlaylist(String nombre) {//Crea una nueva playlist y la guarda
        Playlist p = new Playlist(nombre);
        playlists.agregarFinal(p);
        return p;//regresa la playlist ya creada  
    }

    public boolean eliminarPlaylist(String nombre) {
        Playlist p = buscarPlaylist(nombre);
        if (p == null) //por que null? devuelve null si no encuentra nada
         return false;
        return playlists.eliminar(p);//Elimina el objeto de la lista
    }

    public boolean agregarCancion(String nombrePlaylist, Cancion c) {
        Playlist p = buscarPlaylist(nombrePlaylist);
        if (p == null) 
            return false;
        p.agregarCancion(c);//La playlist agrega la cancion internamente
        return true;
    }
     public boolean eliminarCancion(String nombrePlaylist, String nombreCancion) { //Elimina una cancion especifica de una playlist especifica
        Playlist p = buscarPlaylist(nombrePlaylist);
        if (p == null)
            return false;
        return p.eliminarCancion(nombreCancion);
    }

    //  Busqueda 
    public Playlist buscarPlaylist(String nombre) {
        return playlists.buscar(p -> p.getNombre().equalsIgnoreCase(nombre));
    }

    public List<Playlist> getTodas() {//devuelve todas las playlist
        List<Playlist> lista = new ArrayList<>();
        for (Playlist p : playlists)
            lista.add(p);
        return lista;
    }

    public int getTotalPlaylists() {
        return playlists.getTamano();
    }

    // La playlist mas grande
    public Playlist getPlaylistMasGrande() {
        Playlist mayor = null;
        for (Playlist p : playlists) {
            if (mayor == null || p.getTotalCanciones() > mayor.getTotalCanciones())
                mayor = p;
        }
        return mayor;
    }
    /** La playlist más larga (mayor duración total) */
    public Playlist getPlaylistMasLarga() {
        Playlist mayor = null;
        for (Playlist p : playlists) {
            if (mayor == null || p.getDuracionTotal() > mayor.getDuracionTotal())
                mayor = p;
        }
        return mayor;
    }

    //  Resumen 

    public String getResumen() {
        StringBuilder sb = new StringBuilder("=== PLAYLISTS ===\n");
        for (Playlist p : playlists) {
            sb.append(p).append("\n");
        }
        return sb.toString();
    }
}
