/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartplayer.utils;

import smartplayer.structures.ArbolBinarioBusqueda;

/**
 *
 * @author rmari
 */
public class VisualizadorArbol {
    
    public static String generarDotABB(ArbolBinarioBusqueda abb) {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph ABB {\n");
        sb.append("  node [shape=ellipse, style=filled, fillcolor=\"#AED6F1\"];\n");
        sb.append("  graph [label=\"Arbol Binario de Busqueda\", fontsize=16];\n");
        generarNodosABB(abb.getRaiz(), sb);
        sb.append("}\n");
        return sb.toString();
    }
    private static void generarNodosABB(ArbolBinarioBusqueda.Nodo n, StringBuilder sb) {
        if (n == null) return;
        String etiqueta = escapar(n.cancion.getNombre()) + "\\n" + escapar(n.cancion.getArtista());
        sb.append("  \"").append(escapar(n.cancion.getNombre())).append("\" [label=\"").append(etiqueta).append("\"];\n");
        if (n.izquierdo != null) {
            sb.append("  \"").append(escapar(n.cancion.getNombre())).append("\" -> \"")
              .append(escapar(n.izquierdo.cancion.getNombre())).append("\" [label=\"I\"];\n");
            generarNodosABB(n.izquierdo, sb);
        }
        if (n.derecho != null) {
            sb.append("  \"").append(escapar(n.cancion.getNombre())).append("\" -> \"")
              .append(escapar(n.derecho.cancion.getNombre())).append("\" [label=\"D\"];\n");
            generarNodosABB(n.derecho, sb);
        }
    }
    
    
}
