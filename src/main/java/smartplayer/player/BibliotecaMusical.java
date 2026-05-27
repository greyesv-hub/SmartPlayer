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

    private static final String[] EXTENSIONES = {
            ".mp3",
            ".wav",
            ".flac",
            ".ogg",
            ".aac"
    };

    private ListaSimple<Cancion> listaBiblioteca;

    private int totalCargadas;

    public BibliotecaMusical() {

        listaBiblioteca = new ListaSimple<>();
        totalCargadas = 0;
    }

    public List<Cancion> cargarDesde(String ruta) {

        List<File> archivos = new ArrayList<>();

        buscarArchivosRec(new File(ruta), archivos);

        List<Cancion> canciones = new ArrayList<>();

        for (File archivo : archivos) {
            Cancion nueva = parsearArchivo(archivo);

            if (nueva != null) {
                canciones.add(nueva);
                listaBiblioteca.agregarFinal(nueva);
            }
        }

        totalCargadas = canciones.size();
        return canciones;
    }

    private void buscarArchivosRec(File carpeta, List<File> resultado) {

        if (!carpeta.exists()) {
            return;
        }

        if (!carpeta.isDirectory()) {
            return;
        }

        File[] archivos = carpeta.listFiles();
        if (archivos == null) {
            return;
        }

        for (File archivo : archivos) {
            if (archivo.isDirectory()) {
                buscarArchivosRec(archivo, resultado);
            } else {
                if (esAudio(archivo)) {
                    resultado.add(archivo);
                }
            }
        }
    }

    private boolean esAudio(File archivo) {

        String nombre = archivo.getName().toLowerCase();
        for (String extension : EXTENSIONES) {
            if (nombre.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private Cancion parsearArchivo(File archivo) {

        try {
            String nombre = quitarExtension(archivo.getName());
            String artista = extraerArtistaDeCarpeta(archivo);
            String album = extraerAlbumDeCarpeta(archivo);
            long tamano = archivo.length();
            double duracion = estimarDuracion(tamano);

            Cancion nueva = new Cancion(
                    nombre,
                    artista,
                    album,
                    "Desconocido",
                    duracion,
                    tamano,
                    archivo.getAbsolutePath(),
                    2024
            );
            return nueva;
        } catch (Exception e) {
            return null;
        }
    }

    private String quitarExtension(String nombre) {

        int posicion = nombre.lastIndexOf('.');
        if (posicion > 0) {
            return nombre.substring(0, posicion);
        }
        return nombre;
    }

    private String extraerArtistaDeCarpeta(File archivo) {

        File carpeta = archivo.getParentFile();
        if (carpeta == null) {
            return "Artista Desconocido";
        }

        File carpetaSuperior = carpeta.getParentFile();
        if (carpetaSuperior != null) {
            return carpetaSuperior.getName();
        }
        return carpeta.getName();
    }

    private String extraerAlbumDeCarpeta(File archivo) {

        File carpeta = archivo.getParentFile();
        if (carpeta != null) {
            return carpeta.getName();
        }
        return "Album Desconocido";
    }
    
    private double estimarDuracion(long bytes) {
        double mb = bytes / (1024.0 * 1024.0);
        return mb * 60;
    }

    public List<Cancion[]> detectarDuplicados() {

        List<Cancion> todas = toList(listaBiblioteca);

        List<Cancion[]> duplicados = new ArrayList<>();

        for (int i = 0; i < todas.size(); i++) {
            for (int j = i + 1; j < todas.size(); j++) {
                Cancion a = todas.get(i);
                Cancion b = todas.get(j);
                boolean mismoTamano =
                        a.getTamano() == b.getTamano();
                boolean mismoNombre = a.getNombre().equalsIgnoreCase(b.getNombre());
                if (mismoTamano && mismoNombre) {
                    duplicados.add(new Cancion[]{a, b});
                }
            }
        }
        return duplicados;
    }

    private List<Cancion> toList(ListaSimple<Cancion> lista) {

        List<Cancion> nueva = new ArrayList<>();
        for (Cancion c : lista) {
            nueva.add(c);
        }
        return nueva;
    }

    public ListaSimple<Cancion> getListaBiblioteca() {
        return listaBiblioteca;
    }

    public int getTotalCargadas() {
        return totalCargadas;
    }

    public String getResumenComparativaCarga() {
        return "Canciones cargadas: " + totalCargadas;
    }
}