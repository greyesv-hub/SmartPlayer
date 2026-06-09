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
     if (recorrido == TipoRecorrido.PRE_ORDEN) {orden = abb.preOrden();
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
     private static String xorConClave(String texto, String clave) {

      if (clave.isEmpty()) {
        return texto;
    }
  
     char[] tc = texto.toCharArray();
     char[] cc = clave.toCharArray();
     char[] resultado = new char[tc.length];

     for (int i = 0; i < tc.length; i++) {
        char letraTexto = tc[i];
        char letraClave = cc[i % cc.length];
        resultado[i] = (char) (letraTexto ^ letraClave);
    }

     return new String(resultado);
    }
     
     public static String exportarPlaylist(Playlist playlist) {
        StringBuilder sb = new StringBuilder();
        sb.append("#SMARTPLAYER_PLAYLIST\n");
        sb.append("NOMBRE:").append(playlist.getNombre()).append("\n");
        sb.append("ID:").append(playlist.getId()).append("\n");
        sb.append("CANCIONES:\n");
        for (Cancion c : playlist.getCanciones()) {
            sb.append(c.getNombre()).append("|")
              .append(c.getArtista()).append("|")
              .append(c.getAlbum()).append("|")
              .append(c.getGenero()).append("|")
              .append(c.getDuracion()).append("|")
              .append(c.getTamano()).append("|")
              .append(c.getRuta()).append("|")
              .append(c.getAnio()).append("\n");
        }
        return sb.toString();
    }
     
     public static Playlist importarPlaylist(String contenido) {

     String[] lineas = contenido.split("\n");

     String nombre = "PlaylistImportada";
     Playlist p = null;
     boolean enCanciones = false;

     for (String linea : lineas) {
        if (linea.startsWith("NOMBRE:")) {
            nombre = linea.substring(7);
            p = new Playlist(nombre);

        }else if (linea.equals("CANCIONES:")) {
            enCanciones = true;
        }else if (enCanciones && p != null && !linea.isEmpty() && !linea.startsWith("#")) {
            String[] parts = linea.split("\\|");

            if (parts.length >= 8) {
                try {
                    Cancion c = new Cancion(
                        parts[0],
                        parts[1],
                        parts[2],
                        parts[3],
                        Double.parseDouble(parts[4]),
                        Long.parseLong(parts[5]),
                        parts[6],
                        Integer.parseInt(parts[7]));
                    p.agregarCancion(c);

                  }catch (NumberFormatException e) {
                }
            }
        }
    }
        if (p == null) {
        return new Playlist(nombre);
    }
        return p;
    }
     
}
