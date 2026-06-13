/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartplayer.model;

import java.util.UUID;
import smartplayer.structures.ListaDoble;

/**
 *
 * @author rmari
 */
public class Playlist {
    private String id;
    private String nombre;
    private ListaDoble<Cancion> canciones;
    private boolean encriptada;

    public Playlist(String nombre) {
        this.id       = UUID.randomUUID().toString();//UUID: Genera un identificador Unico automaticamente, randomUUID(): id aleatorio, toString: Convierte UUID a texto  
        this.nombre   = nombre;
        this.canciones = new ListaDoble<>();
        this.encriptada = false;//La playlist inicia NO encriptada
    }

    //  Gestion de canciones 
    public void agregarCancion(Cancion c){ 
        canciones.agregarFinal(c); }

    public boolean eliminarCancion(String nombreCancion) {
        Cancion encontrada = null;

        for (Cancion c : canciones) {
            if (c.getNombre().equalsIgnoreCase(nombreCancion)) {
                encontrada = c;
                break;
            }
        }
        if (encontrada == null) {
            return false;
        }
        return canciones.eliminarPorValor(encontrada);
    }

    public ListaDoble<Cancion> getCanciones() { 
        return canciones; }

    // Numero de canciones en la playlist
    public int getTotalCanciones() { 
        return canciones.getTamano(); }

    // Duracion total en seg
    public double getDuracionTotal() {
        
        double total = 0;
        for (Cancion c : canciones) {//Recorre todas las canciones
            total += c.getDuracion();
        }
        return total;
    }

    public String getDuracionTotalFormateada() {
        double seg = getDuracionTotal();
        int h   = (int)(seg / 3600);
        int min = (int)((seg % 3600) / 60);
        int s   = (int)(seg % 60);
        return String.format("%02d:%02d:%02d", h, min, s);
    }

    public String getId()       { return id; }
    public String getNombre()   { return nombre; }
    public void   setNombre(String n) { this.nombre = n; }
    public boolean isEncriptada()     { return encriptada; }
    public void   setEncriptada(boolean e) { this.encriptada = e; }

    @Override
    public String toString() {
       
        return "Playlist: " + nombre
                + " | Canciones: " + getTotalCanciones()
                + " | Duracion: " + getDuracionTotalFormateada();
    }
}
