package smartplayer.player;

import smartplayer.model.Cancion;
import smartplayer.structures.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Biblioteca Musical: gestiona la carga de archivos MP3 desde carpetas,
 * e indexa las canciones en ABB, AVL y Lista Simple.
 */
public class BibliotecaMusical {

    private static final String[] EXTENSIONES = {".mp3", ".wav", ".flac", ".ogg", ".aac"};

    // Estructuras de indexación
    private final ListaSimple<Cancion> listaBiblioteca = new ListaSimple<>();

    // Estadísticas de carga
    private int  totalCargadas;

    // ── Carga de archivos ──────────────────────────────────────────────────

    /**
     * Carga recursivamente todos los archivos de audio desde una carpeta.
     * @param ruta ruta de la carpeta raíz
     * @return lista de canciones encontradas
     */
    public List<Cancion> cargarDesde(String ruta) {
        List<File> archivos = new ArrayList<>();
        buscarArchivosRec(new File(ruta), archivos);

        List<Cancion> canciones = new ArrayList<>();
        for (File f : archivos) {
            Cancion c = parsearArchivo(f);
            if (c != null) canciones.add(c);
        }

        // Lista simple (biblioteca general)
        for (Cancion c : canciones) listaBiblioteca.agregarFinal(c);

        totalCargadas = canciones.size();
        return canciones;
    }

    /** Busca archivos de audio recursivamente */
    private void buscarArchivosRec(File dir, List<File> resultado) {
        if (!dir.exists() || !dir.isDirectory()) return;
        File[] hijos = dir.listFiles();
        if (hijos == null) return;
        for (File f : hijos) {
            if (f.isDirectory()) {
                buscarArchivosRec(f, resultado);
            } else if (esAudio(f)) {
                resultado.add(f);
            }
        }
    }

    private boolean esAudio(File f) {
        String nombre = f.getName().toLowerCase();
        for (String ext : EXTENSIONES) if (nombre.endsWith(ext)) return true;
        return false;
    }

    /**
     * Parsea un archivo de audio y extrae metadatos básicos.
     * Para metadatos reales se usaría JAudioTagger; aquí usamos el nombre de archivo.
     */
    private Cancion parsearArchivo(File f) {
        try {
            String nombre   = quitarExtension(f.getName());
            String artista  = extraerArtistaDeCarpeta(f);
            String album    = extraerAlbumDeCarpeta(f);
            long   tamano   = f.length();
            double duracion = estimarDuracion(tamano); // estimación simple
            return new Cancion(nombre, artista, album, "Desconocido",
                               duracion, tamano, f.getAbsolutePath(), 2024);
        } catch (Exception e) {
            return null;
        }
    }

    private String quitarExtension(String nombre) {
        int idx = nombre.lastIndexOf('.');
        return (idx > 0) ? nombre.substring(0, idx) : nombre;
    }

    private String extraerArtistaDeCarpeta(File f) {
        File padre = f.getParentFile();
        if (padre == null) return "Artista Desconocido";
        File abuelo = padre.getParentFile();
        return (abuelo != null) ? abuelo.getName() : padre.getName();
    }

    private String extraerAlbumDeCarpeta(File f) {
        File padre = f.getParentFile();
        return (padre != null) ? padre.getName() : "Álbum Desconocido";
    }

    /** Estimación simple: ~1 MB ≈ 60 segundos de audio MP3 */
    private double estimarDuracion(long bytes) {
        return (bytes / (1024.0 * 1024.0)) * 60.0;
    }

    // ── Detección de duplicados ────────────────────────────────────────────

    /**
     * Detecta archivos duplicados por tamaño y nombre.
     * @return lista de pares (cancion1, cancion2) duplicados
     */
    public List<Cancion[]> detectarDuplicados() {
        List<Cancion> todas = listaBiblioteca.isEmpty()
                ? new ArrayList<>()
                : toList(listaBiblioteca);
        List<Cancion[]> duplicados = new ArrayList<>();
        for (int i = 0; i < todas.size(); i++) {
            for (int j = i + 1; j < todas.size(); j++) {
                Cancion a = todas.get(i);
                Cancion b = todas.get(j);
                if (a.getTamano() == b.getTamano()
                        && a.getNombre().equalsIgnoreCase(b.getNombre())) {
                    duplicados.add(new Cancion[]{a, b});
                }
            }
        }
        return duplicados;
    }

    private List<Cancion> toList(ListaSimple<Cancion> ls) {
        List<Cancion> r = new ArrayList<>();
        for (Cancion c : ls) r.add(c);
        return r;
    }

    // ── Getters ────────────────────────────────────────────────────────────

    public ListaSimple<Cancion> getListaBiblioteca()    { return listaBiblioteca; }
}