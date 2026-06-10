/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartplayer.player;

import java.io.FileInputStream;
import javazoom.jl.player.advanced.AdvancedPlayer;
import smartplayer.model.Cancion;
import smartplayer.structures.Cola;
import smartplayer.structures.ListaCircular;
import smartplayer.structures.ListaDoble;
import smartplayer.structures.Pila;

/**Motor 
 *Gestiona: reproduccion, pausa, siguiente, anterior, cola y historial
 * @author rmari
 */
public class ReproductorMusical {
    
    public enum ModoReproduccion { NORMAL, ALEATORIO, CIRCULAR } //que es un enum? Un enum es un conjunto de valores constantes

    private final ListaDoble<Cancion> listadoNavegacion = new ListaDoble<>();
    private final ListaCircular<Cancion> listaCircular = new ListaCircular<>();
    private final Cola<Cancion> colaReproduccion = new Cola<>();
    private final Pila<Cancion> historial = new Pila<>();

    private volatile Cancion cancionActual;
    private volatile ModoReproduccion modo = ModoReproduccion.NORMAL;
    private volatile boolean reproduciendo = false;
    private volatile boolean pausado = false;
    private volatile boolean detenidoManualmente = false;

    private volatile AdvancedPlayer player;
    private volatile Thread hiloReproduccion;
    private int pauseFrame = 0;

    public void encolar(Cancion c) {
        colaReproduccion.enqueue(c);
        listadoNavegacion.agregarFinal(c);
        listaCircular.agregar(c);
    }
    
    public void reproducir(Cancion c) {
        if (c == null) return;
        detener();

        detenidoManualmente = false;
        cancionActual = c;
        c.incrementarReproducciones();

        if (historial.isEmpty() || historial.peek() != c) {
            historial.push(c);
        }

        iniciarReproduccion(c);
    }

    public void pausar() {
        if (reproduciendo && !pausado) {
            pausado = true;
            reproduciendo = false;

            if (player != null) {
                try {
                    player.close();
                } catch (Exception ignored) {}
            }
        }
    }
      // Continua desde donde se pauso
   public void continuar() {
        if (pausado && cancionActual != null) {
            pausado = false;
            detenidoManualmente = false;
            iniciarReproduccion(cancionActual);
        }
    }
    
       public void siguiente() {
        detener();
        Cancion sig = obtenerSiguiente();
        if (sig != null) {
            reproducir(sig);
        }
    }

     public void anterior() {
        if (historial.getTamano() < 2) {
            return; 
        }
        historial.pop(); 
        Cancion ant = historial.peek(); 
        detener();
        cancionActual = ant;
        iniciarReproduccion(ant);
    }

    // Detiene la reproduccion completamente 
    public void detener() {
        if (player != null) {//verifica si lo archivos se estan reproduciendo correctamente
            player.close();//detiene los audios 
            player = null;//libera la memoria 
        }
        if (hiloReproduccion != null) {
            hiloReproduccion.interrupt();//Que es interrupt()? Envaa una señal de interrupcion al thread.
            hiloReproduccion = null;
        }
        reproduciendo = false;
        pausado = false;
        cancionActual = null;
        pauseFrame = 0;
    }
    
   //  Reproduccion interna
    private void iniciarReproduccion(Cancion c) {
        hiloReproduccion = new Thread(() -> {
            // Bandera local exclusiva de este bloque. Ningún hilo externo la puede alterar.
            boolean terminadaNaturalmente = false;

            try (FileInputStream fis = new FileInputStream(c.getRuta())) {
                
                synchronized (this) {
                    // Doble verificación: si quedaba un residuo del player anterior, lo cerramos
                    if (player != null) {
                        try { player.close(); } catch (Exception ignored) {}
                    }
                    player = new AdvancedPlayer(fis);
                    reproduciendo = true;
                }

                // El hilo se suspende aquí ejecutando el flujo nativo de la canción
                player.play();

                // Si llega aquí de forma lineal es porque leyó el archivo mp3 completo
                terminadaNaturalmente = true;

            } catch (Exception e) {
                // Al presionar stop/pause/next, saltará un BitstreamException/IOException aquí.
                // Como detenidoManualmente cambia a true, evitamos imprimir trazas falsas de error.
                if (!detenidoManualmente) {
                    System.err.println("Error reproduciendo: " + e.getMessage());
                }
            } finally {
                reproduciendo = false;

                // Verificamos de forma segura si la pista terminó sola y por completo
                if (terminadaNaturalmente && !pausado && !detenidoManualmente) {
                    onCancionTerminada();
                }
            }
        });

        hiloReproduccion.setDaemon(true); // Hace que el hilo muera si se cierra la app principal
        hiloReproduccion.start();
    }

    private void onCancionTerminada() {
        Cancion sig = obtenerSiguiente();
        if (sig != null) reproducir(sig);
    }

    private Cancion obtenerSiguiente() {
       
    switch (modo) {
        case CIRCULAR:
            return listaCircular.siguiente();

        case ALEATORIO:
            if (colaReproduccion.isEmpty()) {
                return null;
            }
            return colaReproduccion.dequeue();

        default: // NORMAL
            
            if (!colaReproduccion.isEmpty()) {
                return colaReproduccion.dequeue();
            }
            if (listadoNavegacion.getActual() != null) {
                return listadoNavegacion.siguiente();
            }
            return null;
        }
    }
    
     //  Modo de reproduccion 
    public void setModo(ModoReproduccion modo){ 
        this.modo = modo; 
    }//Cambia el modo de reproduccion actual
    public ModoReproduccion getModo(){ 
        return modo; 
    }

    //  Getters de estado//Porque las variables son privadas
    public Cancion        getCancionActual(){ 
        return cancionActual; }
    public boolean        isReproduciendo(){ //tipo boolean
        return reproduciendo; }
    public boolean        isPausado(){ 
        return pausado; }
    public Cola<Cancion>  getColaReproduccion(){ 
        return colaReproduccion; }
    public Pila<Cancion>  getHistorial(){ 
        return historial; }

    // Resumen del historial de reproduccion 
    public String getHistorialString() {
        
         if (historial.isEmpty()) {
        return "Historial vacio";
    } else {
        return historial.mostrarTodos();
       }
    }

    // Resumen de la cola de reproduccion 
    public String getColaString() {
         if (colaReproduccion.isEmpty()) {
        return "Cola vacia";
    } else {
        return colaReproduccion.mostrarTodos();
      }
    }
}
