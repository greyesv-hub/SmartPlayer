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

/**
 * Motor Gestiona: reproduccion, pausa, siguiente, anterior, cola y historial
 *
 * @author rmari
 */
public class ReproductorMusical {

    public enum ModoReproduccion {
        NORMAL, ALEATORIO, CIRCULAR
    }

    // ── Estructuras de reproducción ────────────────────────────────────────
    private final ListaDoble<Cancion> listadoNavegacion = new ListaDoble<>();
    private final ListaCircular<Cancion> listaCircular = new ListaCircular<>();
    private final Cola<Cancion> colaReproduccion = new Cola<>();
    private final Pila<Cancion> historial = new Pila<>();

    // ── Estado de reproducción (Volatile para garantizar comunicación entre hilos) ──
    private volatile Cancion cancionActual;
    private volatile ModoReproduccion modo = ModoReproduccion.NORMAL;
    private volatile boolean reproduciendo = false;
    private volatile boolean pausado = false;
    private volatile boolean detenidoManualmente = false;

    // ── Reproductor JLayer (Volatile para control inmediato de cierres) ──────
    private volatile AdvancedPlayer player;
    private volatile Thread hiloReproduccion;
    private int pauseFrame = 0;
    private BibliotecaMusical biblioteca;
    private int indiceBiblioteca = 0;

    // ── API pública ────────────────────────────────────────────────────────
    /**
     * Agrega una canción a la cola de reproducción
     */
    
    public void setBiblioteca(BibliotecaMusical biblioteca) {
        this.biblioteca = biblioteca;
    }

    public void encolar(Cancion c) {
        colaReproduccion.enqueue(c);
        listadoNavegacion.agregarFinal(c);
        listaCircular.agregar(c);
    }

    /**
     * Reproduce la canción indicada limpiando hilos anteriores de forma segura
     */
    public void reproducir(Cancion c) {

        if (c == null) {
            return;
        }

        detener();

        detenidoManualmente = false;
        cancionActual = c;

        listadoNavegacion.establecerActual(c);

        c.incrementarReproducciones();

        if (historial.isEmpty() || historial.peek() != c) {
            historial.push(c);
        }

        iniciarReproduccion(c);
    }

    /**
     * Pausa la reproducción actual liberando los buffers de la tarjeta de
     * sonido
     */
    public void pausar() {
        if (reproduciendo && !pausado) {
            pausado = true;
            reproduciendo = false;

            // Al cerrar el player, forzamos de inmediato al hilo a saltar al bloque catch/finally
            if (player != null) {
                try {
                    player.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * Continúa la reproducción desde el inicio de la pista actual (Limitación
     * de JLayer estándar)
     */
    public void continuar() {
        if (pausado && cancionActual != null) {
            pausado = false;
            detenidoManualmente = false;
            iniciarReproduccion(cancionActual);
        }
    }

    /**
     * Pasa a la siguiente canción basándose en el modo actual
     */
    public void siguiente() {
        detener();
        Cancion sig = obtenerSiguiente();
        if (sig != null) {
            reproducir(sig);
        }
    }

    /**
     * Regresa a la canción anterior desde el historial de manera limpia
     */
    public void anterior() {
        if (historial.getTamano() < 2) {
            return; // No hay suficiente historial para regresar
        }

        historial.pop(); // Sacamos la canción actual que estaba en la cima
        Cancion ant = historial.peek(); // Tomamos la pista anterior real

        detener();
        cancionActual = ant;

        // Llamamos directamente a iniciar la pista en lugar de reproducir()
        // para evitar que se meta de nuevo en un bucle cíclico en la pila de historial
        iniciarReproduccion(ant);
    }

    /**
     * Detiene la reproducción completamente de forma atómica y síncrona
     */
    public void detener() {
        detenidoManualmente = true;

        // 1. Cerramos el decodificador de audio primero. Esto detiene inmediatamente 
        // cualquier envío de bytes a los altavoces de manera fulminante.
        if (player != null) {
            try {
                player.close();
            } catch (Exception ignored) {
            }
            player = null;
        }

        // 2. Despertamos al hilo y limpiamos la referencia
        if (hiloReproduccion != null) {
            hiloReproduccion.interrupt();
            hiloReproduccion = null;
        }

        reproduciendo = false;
        pausado = false;
        cancionActual = null;
        pauseFrame = 0;
    }

    // ── Reproducción interna y control de hilos ───────────────────────────────
    /**
     * Levanta un hilo demonio exclusivo para decodificar la pista de audio
     */
    private void iniciarReproduccion(Cancion c) {
        hiloReproduccion = new Thread(() -> {
            // Bandera local exclusiva de este bloque. Ningún hilo externo la puede alterar.
            boolean terminadaNaturalmente = false;

            try (FileInputStream fis = new FileInputStream(c.getRuta())) {

                synchronized (this) {
                    // Doble verificación: si quedaba un residuo del player anterior, lo cerramos
                    if (player != null) {
                        try {
                            player.close();
                        } catch (Exception ignored) {
                        }
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

    /**
     * Se dispara automáticamente en segundo plano cuando finaliza el archivo de
     * audio
     */
    private void onCancionTerminada() {
        // Ejecutamos en un hilo independiente para liberar el hilo de audio moribundo 
        // y evitar sobrecargar la pila de llamadas recursivas en la UI
        Thread despachadorSiguiente = new Thread(this::siguiente);
        despachadorSiguiente.start();
    }

    /**
     * Determina cuál es el siguiente elemento a extraer según las estructuras
     * lógicas
     */
    private Cancion obtenerSiguiente() {
        if(biblioteca.listaBiblioteca.getTamano() - 1 < indiceBiblioteca){
            indiceBiblioteca = 0;
        }
            
        switch (modo) {
            case CIRCULAR:
                return listaCircular.isEmpty()
                        ? biblioteca.listaBiblioteca.get(indiceBiblioteca++)
                        : listaCircular.siguiente();

            case ALEATORIO:
                // TODO: implementar con arreglo y shuffle
                return colaReproduccion.isEmpty()
                        ? biblioteca.listaBiblioteca.get(indiceBiblioteca + 2)
                        : colaReproduccion.dequeue();

            default: // NORMAL
                if (!colaReproduccion.isEmpty()) {
                    return colaReproduccion.dequeue();
                }

                return listadoNavegacion.getActual() != null
                        ? listadoNavegacion.siguiente()
                        : biblioteca.listaBiblioteca.get(indiceBiblioteca++);
        }
    }

    // ── Modo de reproducción ───────────────────────────────────────────────
    public void setModo(ModoReproduccion modo) {
        this.modo = modo;
    }

    public ModoReproduccion getModo() {
        return modo;
    }

    // ── Getters de estado ──────────────────────────────────────────────────
    public Cancion getCancionActual() {
        return cancionActual;
    }

    public boolean isReproduciendo() {
        return reproduciendo;
    }

    public boolean isPausado() {
        return pausado;
    }

    public Cola<Cancion> getColaReproduccion() {
        return colaReproduccion;
    }

    public Pila<Cancion> getHistorial() {
        return historial;
    }

    /**
     * Resumen del historial de reproducción
     */
    public String getHistorialString() {
        return historial.isEmpty() ? "Historial vacío" : historial.mostrarTodos();
    }

    /**
     * Resumen de la cola de reproducción
     */
    public String getColaString() {
        return colaReproduccion.isEmpty() ? "Cola vacía" : colaReproduccion.mostrarTodos();
    }
}
