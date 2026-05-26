/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartplayer.player;

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
    
    public enum ModoReproduccion { NORMAL, ALEATORIO, CIRCULAR }

    //  Estructuras de reproduccion
    private final ListaDoble<Cancion>   listadoNavegacion = new ListaDoble<>();
    private final ListaCircular<Cancion> listaCircular    = new ListaCircular<>();
    private final Cola<Cancion>          colaReproduccion = new Cola<>();
    private final Pila<Cancion>          historial        = new Pila<>();

    //  Estado de reproduccion
    private Cancion           cancionActual;
    private ModoReproduccion  modo = ModoReproduccion.NORMAL;
    private boolean           reproduciendo = false;
    private boolean           pausado       = false;

    //  Reproductor JLayer 
    private AdvancedPlayer player;
    private Thread         hiloReproduccion;
    private int            pauseFrame = 0;

}
