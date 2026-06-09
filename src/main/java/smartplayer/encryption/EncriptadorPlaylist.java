/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartplayer.encryption;

import java.util.List;
import smartplayer.model.Cancion;
import smartplayer.model.Playlist;
import smartplayer.structures.ArbolBinarioBusqueda;

/**
 *
 * @author rmari
 */
public class EncriptadorPlaylist {
    
     public enum TipoRecorrido { IN_ORDEN, PRE_ORDEN, POST_ORDEN }

    // ── Encriptar / Desencriptar ───────────────────────────────────────────

    /**
     * Encripta (o desencripta) el contenido de texto de una playlist
     * usando un recorrido de árbol como fuente de la clave.
     *
     * @param contenido  texto plano (o cifrado) de la playlist exportada
     * @param playlist   playlist cuyas canciones definen la clave
     * @param recorrido  tipo de recorrido para generar la clave
     * @return texto cifrado (o descifrado)
     */
    public static String encriptar(String contenido, Playlist playlist, TipoRecorrido recorrido) {
        String clave = generarClave(playlist, recorrido);
        return xorConClave(contenido, clave);
    }
    
  private static String generarClave(Playlist playlist, TipoRecorrido recorrido) {

    ArbolBinarioBusqueda abb = new ArbolBinarioBusqueda();

    for (Cancion c : playlist.getCanciones()) {abb.insertar(c);
    }

    List<Cancion> orden;
    if (recorrido == TipoRecorrido.PRE_ORDEN) {
        orden = abb.preOrden();
    }else if (recorrido == TipoRecorrido.POST_ORDEN) {
        orden = abb.postOrden();
    }else{
        orden = abb.inOrden();
    }

    StringBuilder sb = new StringBuilder();

    for (Cancion c : orden) {
        sb.append(c.getNombre());
        sb.append(c.getArtista());
    }
    return sb.toString();
   }
   
}
