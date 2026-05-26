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

    //  Estructuras de reproduccion
    private final ListaDoble<Cancion>   listadoNavegacion = new ListaDoble<>();  //funciona para avanzar y retroceder 
    private final ListaCircular<Cancion> listaCircular    = new ListaCircular<>(); //Para la reproduccion infinita 
    private final Cola<Cancion>          colaReproduccion = new Cola<>();// controla las canciones pendientes 
    private final Pila<Cancion>          historial        = new Pila<>();//guarda las canciones reproducidas 

    //  Estado de reproduccion
    private Cancion           cancionActual;//Guarda las canciones que estan sonando 
    private ModoReproduccion  modo = ModoReproduccion.NORMAL; 
    private boolean           reproduciendo = false;//Indica si la musia se esta reproduciendo 
    private boolean           pausado       = false;//Esta pausada la musica 

    //  Reproductor JLayer 
    private AdvancedPlayer player;//reproductor mp3
    private Thread         hiloReproduccion;//porque usar hilo? porque reproducir la musica tarda y la interfaz se congelaria
    private int            pauseFrame = 0;//guarada las posiciones donde se pauso la musica 
    
     // API publica = metodo accesible de otras clases  

    // Agrega una cancion a la cola de reproduccion
    public void encolar(Cancion c) { //agrega la cancion a la reproduccion 
        colaReproduccion.enqueue(c);//mete la cancion a la cola
        listadoNavegacion.agregarFinal(c);//la agrega a la navegacion 
        listaCircular.agregar(c);
    }

    // Reproduce la cancion indicada 
    public void reproducir(Cancion c) {//reproduce una cancion en especifico 
        detener();//detiene la musica 
        cancionActual = c;//guarda 
        c.incrementarReproducciones();//aumenta el contador de reproduccion 
        historial.push(c);//guarda la cancion en el historial 
        iniciarReproduccion(c);
    }

    // Pausa la reproduccion actual 
    public void pausar() {
        if (reproduciendo && !pausado) {
            if (player != null) player.close();//detiene al reproducion //player.close() realmente detiene el audio; Los booleanos solo representan el estado interno
            pausado = true;
            reproduciendo = false;
        }
    }
      // Continua desde donde se pauso
    public void continuar() {
        if (pausado && cancionActual != null) {
            pausado = false;
            iniciarReproduccion(cancionActual); // Reinicia la cancion actual
        }
    }

    // Pasa a la siguiente cancion      
    public void siguiente() {
        detener();
        Cancion sig = obtenerSiguiente();
        if (sig != null) reproducir(sig);
    }

    // Regresa a la cancion anterior (desde historial) 
    public void anterior() {
        if (historial.getTamano() < 2) return;//cancion actual y anterior
        historial.pop(); // Elimina la cancion actual (pop = elimina y devuelve)
        Cancion ant = historial.peek();//Obtiene la nueva cima SIN eliminarla (solo observa)
        detener();
        reproducir(ant);
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
    }
    
   //  Reproduccion interna
    private void iniciarReproduccion(Cancion c) {
        hiloReproduccion = new Thread(() -> {// se reproducira mientras la interfaz siga funcionando 
            try (FileInputStream fis = new FileInputStream(c.getRuta())) {//abre los archivos mp3 y devuelve su ruta 
                player = new AdvancedPlayer(fis);//al usar try evita fugas de memoria //AdvancedPlayer: clase de libreria JLayer 
                reproduciendo = true;
                player.play();//funciona el sonido 
                // Cuando termina, reproduce la siguiente automaticamente
                if (!pausado) onCancionTerminada();//player.play = es bloqueante; el hilo queda ocupado hasta que termine la cancion
            } catch (Exception e) {
                System.err.println("Error reproduciendo: " + e.getMessage());
            }
        });
        hiloReproduccion.setDaemon(true);//Daemon hilo secundario, evita procesos fantasmas 
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
                // TODO: implementar con arreglo y shuffle//codigo pendiente 
                return colaReproduccion.isEmpty() ? null : colaReproduccion.dequeue();//dequeue saca el primer elemento 
            default: // NORMAL
                if (!colaReproduccion.isEmpty()) return colaReproduccion.dequeue();
                return listadoNavegacion.getActual() != null
                       ? listadoNavegacion.siguiente()
                       : null;
        }
    }
    
    
}
