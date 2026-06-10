/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartplayer.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import smartplayer.encryption.EncriptadorPlaylist;
import smartplayer.model.Playlist;

/**
 *
 * @author rmari
 */
public class GestorArchivos {
    
     private static final String EXTENSION_PLANA     = ".spl";   // SmartPlayer List
     private static final String EXTENSION_CIFRADA   = ".splc";  // SmartPlayer List Cifrada

     public static boolean exportarPlaylist(Playlist playlist, String rutaDestino) {
     try {
        String contenido =EncriptadorPlaylist.exportarPlaylist(playlist);
        String ruta = rutaDestino + File.separator + sanitizar(playlist.getNombre()) + EXTENSION_PLANA;

        Files.writeString(Paths.get(ruta), contenido);
        System.out.println("Playlist exportada: " + ruta);
        return true;

    } catch (IOException e) {
        System.err.println("Error exportando playlist: "+ e.getMessage());
        return false;
      }
    }

    public static boolean exportarPlaylistEncriptada(Playlist playlist, String rutaDestino, EncriptadorPlaylist.TipoRecorrido recorrido) {
        try {
            String contenido = EncriptadorPlaylist.encriptarParaGuardar(playlist, recorrido);
            String ruta = rutaDestino + File.separator + sanitizar(playlist.getNombre()) + EXTENSION_CIFRADA;
            Files.writeString(Paths.get(ruta), contenido);
            
            System.out.println("Playlist encriptada exportada: " + ruta);
            return true;
                
        } catch (IOException e) {
            System.err.println("Error exportando playlist cifrada: " + e.getMessage());
            return false;
        }
    }

    public static Playlist importarPlaylist(String rutaArchivo) {
        try {
            String contenido = Files.readString(Paths.get(rutaArchivo));
            return EncriptadorPlaylist.importarPlaylist(contenido);
            
        } catch (IOException e) {
            System.err.println("Error importando playlist: " + e.getMessage());
            return null;
        }
    }

    public static Playlist importarPlaylistEncriptada(String rutaArchivo, Playlist referencia) {
        try {
            String contenido = Files.readString(Paths.get(rutaArchivo));
            return EncriptadorPlaylist.desencriptarArchivo(contenido, referencia);
            
        } catch (IOException e) {
            System.err.println("Error importando playlist cifrada: " + e.getMessage());
            return null;
        }
    }

    private static String sanitizar(String nombre) {
     return nombre.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }

    public static boolean crearCarpetaSiNoExiste(String ruta) {

    try {
        Files.createDirectories(Paths.get(ruta));
        return true;

    } catch (IOException e) {
        System.err.println("No se pudo crear carpeta: " + ruta);
        return false;
    }
  }
}
