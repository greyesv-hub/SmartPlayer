/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartplayer.player.Stats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import smartplayer.model.Cancion;
import smartplayer.model.Playlist;
import smartplayer.player.BibliotecaMusical;
import smartplayer.player.GestorPlaylist;
/**
 *Genera reportes sobre la biblioteca y las playlists
 * @author rmari
 */
public class EstadisticasMusical {

       private final BibliotecaMusical biblioteca;
       private final GestorPlaylist   gestor;

       private long ultimaBusquedaABB;
       private long ultimaBusquedaAVL;

       public EstadisticasMusical(BibliotecaMusical b, GestorPlaylist g) {
        this.biblioteca = b;
        this.gestor     = g;
    }

       public Cancion getCancionMasReproducida() {
        Cancion cancionMasReproducida = null;

        for (Cancion cancion : biblioteca.getListaBiblioteca()) {
            if (cancionMasReproducida == null || cancion.getVecesReproducida() > cancionMasReproducida.getVecesReproducida()) {
            cancionMasReproducida = cancion;
        }
    }
        return cancionMasReproducida;
    }

       public String getArtistaMasEscuchado() {Map<String, Integer> conteo = new HashMap<>();

       for (Cancion cancion : biblioteca.getListaBiblioteca()) {
        conteo.merge(cancion.getArtista(),cancion.getVecesReproducida(),Integer::sum);
    }

        return conteo.entrySet()
            .stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
    }

       public String getGeneroMasFrecuente() {Map<String, Integer> conteo = new HashMap<>();

        for (Cancion c : biblioteca.getListaBiblioteca()) {String genero = c.getGenero();
          if (conteo.containsKey(genero)) {
            conteo.put(genero, conteo.get(genero) + 1);
        }else{
            conteo.put(genero, 1);
        }
    }
        String generoMasFrecuente = "N/A";
        int mayor = 0;

        for (Map.Entry<String, Integer> dato : conteo.entrySet()) {

        if (dato.getValue() > mayor) {
            mayor = dato.getValue();
            generoMasFrecuente = dato.getKey();
        }
    }
         return generoMasFrecuente;
    }

        public double getPromedioDuracion() {

        double suma = 0;
        int total = 0;

        for (Cancion c : biblioteca.getListaBiblioteca()) {
          suma += c.getDuracion(); total++;
    }
        if (total == 0) {
           return 0;
    }
         return suma / total;
    }

        public long getTamanoTotalBytes() {
        
          long total = 0;
        
           for (Cancion c : biblioteca.getListaBiblioteca()) 
            total += c.getTamano();
         return total;
    }

        public String getTamanoTotalFormateado() {

        double tamanoGB = getTamanoTotalBytes() / 1024.0 / 1024.0 / 1024.0;
         return tamanoGB + " GB";
    }

        public List<Cancion[]> getDuplicados() {
         return biblioteca.detectarDuplicados();
    }

        public void medirBusqueda(String nombre) {

         long inicioABB = System.nanoTime();
         biblioteca.getAbb().buscar(nombre);
         ultimaBusquedaABB = System.nanoTime() - inicioABB;

         long inicioAVL = System.nanoTime();
         biblioteca.getAvl().buscar(nombre);
         ultimaBusquedaAVL = System.nanoTime() - inicioAVL;
    }

        public String getResumenBusqueda(String nombre) {medirBusqueda(nombre);

        double tiempoABB = ultimaBusquedaABB / 1000000.0;
        double tiempoAVL = ultimaBusquedaAVL / 1000000.0;
        String masRapido;

        if (tiempoABB <= tiempoAVL) {
        masRapido = "ABB";
        }else{
        masRapido = "AVL";
    }
        return String.format(
           " COMPARATIVA DE BUSQUEDA: '%s' \n" +
           "ABB : %.4f ms\n" +
           "AVL : %.4f ms\n" +
           "Mas rapido: %s\n",
           nombre, tiempoABB, tiempoAVL, masRapido);
    }
        
        
    public String getReporteCompleto() {
        Cancion masRep = getCancionMasReproducida();
        Playlist masGrande = gestor.getPlaylistMasGrande();
        Playlist masLarga  = gestor.getPlaylistMasLarga();
        List<Cancion[]> duplicados = getDuplicados();

        long tamDup = 0;
        for (Cancion[] par : duplicados) tamDup += par[1].getTamano();

        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════╗\n");
        sb.append("║        ESTADISTICAS SMART PLAYER     ║\n");
        sb.append("╚══════════════════════════════════════╝\n\n");
        sb.append("► Canciones totales    : ").append(biblioteca.getTotalCargadas()).append("\n");
        sb.append("► Tamaño total         : ").append(getTamanoTotalFormateado()).append("\n");
        sb.append("► Promedio duracion    : ").append(
                String.format("%.1f seg (%.1f min)", getPromedioDuracion(), getPromedioDuracion()/60)).append("\n");
        sb.append("► Artista mas escuchado: ").append(getArtistaMasEscuchado()).append("\n");
        sb.append("► Genero mas frecuente : ").append(getGeneroMasFrecuente()).append("\n");
        sb.append("► Cancion mas reprod  : ").append(
                masRep != null ? masRep.getNombre() + " (" + masRep.getVecesReproducida() + "x)" : "N/A").append("\n");
        sb.append("► Playlist mas grande  : ").append(
                masGrande != null ? masGrande.getNombre() + " (" + masGrande.getTotalCanciones() + " canciones)" : "N/A").append("\n");
        sb.append("► Playlist mas larga   : ").append(
                masLarga != null ? masLarga.getNombre() + " (" + masLarga.getDuracionTotalFormateada() + ")" : "N/A").append("\n");
        sb.append("► Duplicados           : ").append(duplicados.size()).append(
                String.format(" (%.2f MB desperdiciados)", tamDup / (1024.0 * 1024.0))).append("\n\n");
        sb.append(biblioteca.getResumenComparativaCarga());
        return sb.toString();
    }

    public long getUltimaBusquedaABB() { return ultimaBusquedaABB; }
    public long getUltimaBusquedaAVL() { return ultimaBusquedaAVL; }
}

