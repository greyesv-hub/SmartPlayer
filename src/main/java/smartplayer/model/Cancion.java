package smartplayer.model;

/**
 * Clase que representa una Canción en el sistema Smart Player.
 * Contiene todos los metadatos necesarios para la gestión musical.
 */
public class Cancion implements Comparable<Cancion> {

    private String nombre;
    private String artista;
    private String album;
    private String genero;
    private double duracion;      // en segundos
    private long tamano;          // en bytes
    private String ruta;
    private int anio;
    private int vecesReproducida;

    public Cancion(String nombre, String artista, String album,
                   String genero, double duracion, long tamano,
                   String ruta, int anio) {
        this.nombre = nombre;
        this.artista = artista;
        this.album = album;
        this.genero = genero;
        this.duracion = duracion;
        this.tamano = tamano;
        this.ruta = ruta;
        this.anio = anio;
        this.vecesReproducida = 0;
    }

    // ── Getters ────────────────────────────────────────────────────────────
    public String getNombre()          { return nombre; }
    public String getArtista()         { return artista; }
    public String getAlbum()           { return album; }
    public String getGenero()          { return genero; }
    public double getDuracion()        { return duracion; }
    public long   getTamano()          { return tamano; }
    public String getRuta()            { return ruta; }
    public int    getAnio()            { return anio; }
    public int    getVecesReproducida(){ return vecesReproducida; }

    // ── Setters ────────────────────────────────────────────────────────────
    public void setNombre(String nombre)    { this.nombre = nombre; }
    public void setArtista(String artista)  { this.artista = artista; }
    public void setAlbum(String album)      { this.album = album; }
    public void setGenero(String genero)    { this.genero = genero; }
    public void setDuracion(double d)       { this.duracion = d; }
    public void setTamano(long t)           { this.tamano = t; }
    public void setRuta(String ruta)        { this.ruta = ruta; }
    public void setAnio(int anio)           { this.anio = anio; }

    /** Incrementa el contador de reproducciones */
    public void incrementarReproducciones() { this.vecesReproducida++; }

    /**
     * Comparación por nombre (para inserción en ABB/AVL).
     */
    @Override
    public int compareTo(Cancion otra) {
        return this.nombre.compareToIgnoreCase(otra.nombre);
    }

    /** Duración formateada mm:ss */
    public String getDuracionFormateada() {
        int min = (int)(duracion / 60);
        int seg = (int)(duracion % 60);
        return String.format("%02d:%02d", min, seg);
    }

    /** Tamaño en MB con 2 decimales */
    public String getTamanoFormateado() {
        double mb = tamano / (1024.0 * 1024.0);
        return String.format("%.2f MB", mb);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%s) | %s | %s",
                genero, artista, nombre, album,
                getDuracionFormateada(), getTamanoFormateado());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Cancion)) return false;
        Cancion otra = (Cancion) obj;
        return this.ruta.equalsIgnoreCase(otra.ruta);
    }

    @Override
    public int hashCode() {
        return ruta.toLowerCase().hashCode();
    }
}