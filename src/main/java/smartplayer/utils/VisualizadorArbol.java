/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartplayer.utils;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizCmdLineEngine;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Format;
import smartplayer.structures.ArbolAVL;
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
    
     public static String generarDotAVL(ArbolAVL avl) {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph AVL {\n");
        sb.append("  node [shape=ellipse, style=filled, fillcolor=\"#A9DFBF\"];\n");
        sb.append("  graph [label=\"Árbol AVL (balanceado)\", fontsize=16];\n");
        generarNodosAVL(avl.getRaiz(), sb);
        sb.append("}\n");
        return sb.toString();
    }
     
     private static void generarNodosAVL(ArbolAVL.Nodo n, StringBuilder sb) {
        if (n == null) return;
        String etiqueta = escapar(n.cancion.getNombre()) + "\\nh=" + n.altura;
        sb.append("  \"").append(escapar(n.cancion.getNombre())).append("\" [label=\"").append(etiqueta).append("\"];\n");
        if (n.izquierdo != null) {
            sb.append("  \"").append(escapar(n.cancion.getNombre())).append("\" -> \"")
              .append(escapar(n.izquierdo.cancion.getNombre())).append("\" [label=\"I\"];\n");
            generarNodosAVL(n.izquierdo, sb);
        }
        if (n.derecho != null) {
            sb.append("  \"").append(escapar(n.cancion.getNombre())).append("\" -> \"")
              .append(escapar(n.derecho.cancion.getNombre())).append("\" [label=\"D\"];\n");
            generarNodosAVL(n.derecho, sb);
        }
    }
     public static String renderizarComoPNG(String dotContent, String nombreArchivo, String carpetaSalida) {
        try {
            Files.createDirectories(Paths.get(carpetaSalida));
            
            String rutaDot = carpetaSalida + File.separator + nombreArchivo + ".dot";
            String rutaPng = carpetaSalida + File.separator + nombreArchivo + ".png";

            Files.writeString(Paths.get(rutaDot), dotContent);
            MutableGraph grafo = new Parser().read(dotContent);
            Graphviz.fromGraph(grafo)
                    .width(800) 
                    .render(Format.PNG)
                    .toFile(new File(rutaPng));

            return rutaPng; // Retorna la ruta del PNG creado de forma transparente
            
        } catch (Exception e) {
            System.err.println("Error en la generación automática del PNG: " + e.getMessage()); e.printStackTrace();
        }
        return null;
    }

    private static String escapar(String s) {
        return s.replace("\"", "\\\"").replace("\n", " ");
    }
}
