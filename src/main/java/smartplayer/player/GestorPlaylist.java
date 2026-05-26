/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartplayer.player;

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
    
}
