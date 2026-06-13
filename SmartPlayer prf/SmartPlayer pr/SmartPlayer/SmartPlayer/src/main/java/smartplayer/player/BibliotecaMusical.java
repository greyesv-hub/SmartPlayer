package smartplayer.player;

import smartplayer.model.Cancion;
import smartplayer.structures.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

/**
 * Biblioteca Musical: gestiona la carga de archivos MP3 desde carpetas,
 * e indexa las canciones en ABB, AVL y Lista Simple.
 */
public class BibliotecaMusical {

    private static final String[] EXTENSIONES = {".mp3", ".wav", ".flac", ".ogg", ".aac"};

    // Estructuras de indexación
    private final ArbolBinarioBusqueda abb = new ArbolBinarioBusqueda();
    private final ArbolAVL avl             = new ArbolAVL();
    private final ListaSimple<Cancion> listaBiblioteca = new ListaSimple<>();

    // Estadísticas de carga
    private long tiempoCargaABB;
    private long tiempoCargaAVL;
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

        // Insertar en ABB midiendo tiempo
        long t0 = System.nanoTime();
        for (Cancion c : canciones) abb.insertar(c);
        tiempoCargaABB = System.nanoTime() - t0;

        // Insertar en AVL midiendo tiempo
        t0 = System.nanoTime();
        for (Cancion c : canciones) avl.insertar(c);
        tiempoCargaAVL = System.nanoTime() - t0;

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

            AudioFile audioFile = AudioFileIO.read(f);
            Tag tag = audioFile.getTag();

            String titulo = quitarExtension(f.getName());
            String artista = "Artista Desconocido";
            String album = "Álbum Desconocido";
            String genero = "Desconocido";
            int anio = 0;

            if (tag != null) {

                if (!tag.getFirst(FieldKey.TITLE).isBlank()) {
                    titulo = tag.getFirst(FieldKey.TITLE);
                }

                if (!tag.getFirst(FieldKey.ARTIST).isBlank()) {
                    artista = tag.getFirst(FieldKey.ARTIST);
                }

                if (!tag.getFirst(FieldKey.ALBUM).isBlank()) {
                    album = tag.getFirst(FieldKey.ALBUM);
                }

                if (!tag.getFirst(FieldKey.GENRE).isBlank()) {
                    genero = tag.getFirst(FieldKey.GENRE);
                }

                String year = tag.getFirst(FieldKey.YEAR);
                if (year != null && !year.isBlank()) {
                    try {
                        if (year.length() >= 4) {
                            anio = Integer.parseInt(year.substring(0, 4));
                        }
                    } catch (Exception e) {
                        anio = 0;
                    }
                }
            }

            System.out.println("TITLE  : " + tag.getFirst(FieldKey.TITLE));
            System.out.println("ARTIST : " + tag.getFirst(FieldKey.ARTIST));
            System.out.println("ALBUM  : " + tag.getFirst(FieldKey.ALBUM));
            System.out.println("GENRE  : " + tag.getFirst(FieldKey.GENRE));
            System.out.println("YEAR   : " + tag.getFirst(FieldKey.YEAR));

            double duracion = audioFile.getAudioHeader().getTrackLength();

            return new Cancion(
                    titulo,
                    artista,
                    album,
                    genero,
                    duracion,
                    f.length(),
                    f.getAbsolutePath(),
                    anio
            );

        } catch (Exception e) {
            System.err.println("Error leyendo: " + f.getAbsolutePath());
            e.printStackTrace();
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

    public ArbolBinarioBusqueda getAbb()                { return abb; }
    public ArbolAVL             getAvl()                { return avl; }
    public ListaSimple<Cancion> getListaBiblioteca()    { return listaBiblioteca; }

    public long getTiempoCargaABB()  { return tiempoCargaABB; }
    public long getTiempoCargaAVL()  { return tiempoCargaAVL; }
    public int  getTotalCargadas()   { return totalCargadas; }

    /** Resumen de comparativa de carga */
    public String getResumenComparativaCarga() {
        return String.format(
            "=== COMPARATIVA DE CARGA ===\n" +
            "Canciones cargadas : %d\n" +
            "Tiempo ABB         : %.3f ms\n" +
            "Tiempo AVL         : %.3f ms\n" +
            "Altura ABB         : %d\n" +
            "Altura AVL         : %d\n" +
            "Diferencia         : %.3f ms (%s más rápido)\n",
            totalCargadas,
            tiempoCargaABB / 1_000_000.0,
            tiempoCargaAVL / 1_000_000.0,
            abb.altura(),
            avl.altura(),
            Math.abs(tiempoCargaABB - tiempoCargaAVL) / 1_000_000.0,
            (tiempoCargaABB < tiempoCargaAVL) ? "ABB" : "AVL"
        );
       }
    }
