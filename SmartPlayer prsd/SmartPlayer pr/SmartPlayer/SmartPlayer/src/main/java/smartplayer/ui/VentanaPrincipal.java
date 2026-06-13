package smartplayer.ui;

import smartplayer.model.Cancion;
import smartplayer.player.*;
import smartplayer.Stats.EstadisticasMusical;
import smartplayer.structures.ArbolAVL;
import smartplayer.structures.ArbolBinarioBusqueda;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import smartplayer.encryption.EncriptadorPlaylist;
import smartplayer.model.Playlist;
import smartplayer.utils.GestorArchivos;
import smartplayer.utils.VisualizadorArbol;

public class VentanaPrincipal extends JFrame {

    private static final Color COLOR_FONDO = new Color(18, 18, 18);
    private static final Color COLOR_PANEL = new Color(30, 30, 30);
    private static final Color COLOR_ACENTO = new Color(29, 185, 84);   // verde Spotify
    private static final Color COLOR_ACENTO2 = new Color(0, 120, 215);   // azul
    private static final Color COLOR_TEXTO = new Color(255, 255, 255);
    private static final Color COLOR_TEXTO_SEC = new Color(180, 180, 180);
    private static final Color COLOR_FILA_PAR = new Color(40, 40, 40);
    private static final Color COLOR_FILA_IMPAR = new Color(35, 35, 35);
    private static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FUENTE_GRANDE = new Font("Segoe UI", Font.BOLD, 22);

    private final BibliotecaMusical biblioteca = new BibliotecaMusical();
    private final ReproductorMusical reproductor = new ReproductorMusical();
    private final GestorPlaylist gestor = new GestorPlaylist();
    private final EstadisticasMusical estadisticas = new EstadisticasMusical(biblioteca, gestor);

    private JTabbedPane pestanas;
    private JTable tablaBiblioteca;
    private DefaultTableModel modeloBiblioteca;
    private JTable tablaPlaylist;
    private DefaultTableModel modeloPlaylist;
    private JLabel lblCancionActual;
    private JLabel lblArtista;
    private JLabel lblTiempo;
    private JProgressBar barraProgreso;
    private JButton btnPlay, btnPause, btnStop, btnNext, btnPrev;
    private JComboBox<String> comboModo;
    private JTextField txtBuscar;
    private JComboBox<String> comboBuscarPor;
    private JTextArea areaLog;
    private JList<String> listaPlaylists;
    private DefaultListModel<String> modeloListaPlaylists;
    private JLabel lblEstABB, lblEstAVL;
    private JTextArea areaEstadisticas;

    private Timer timerProgreso;
    private long inicioReproduccion;
    private JTextArea areaColaMini;

    public VentanaPrincipal() {
        super(" Smart Player - Sistema de Gestion Musical");
        configurarVentana();
        inicializarUI();
        setVisible(true);
        reproductor.setBiblioteca(biblioteca);
    }

    private void configurarVentana() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 780);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);
        UIManager.put("TabbedPane.background", COLOR_PANEL);
        UIManager.put("TabbedPane.foreground", COLOR_TEXTO);
        UIManager.put("TabbedPane.selected", COLOR_ACENTO);
        UIManager.put("TabbedPane.contentAreaColor", COLOR_PANEL);
    }

    private void inicializarUI() {
        setLayout(new BorderLayout(0, 0));
        add(crearPanelSuperior(), BorderLayout.NORTH);

        pestanas = new JTabbedPane(JTabbedPane.TOP);
        pestanas.setBackground(COLOR_PANEL);
        pestanas.setForeground(COLOR_TEXTO);
        pestanas.setFont(FUENTE_TITULO);
        pestanas.addTab(" Biblioteca", crearPestanaBiblioteca());
        pestanas.addTab(" Playlists", crearPestanaPlaylists());
        pestanas.addTab(" Arboles", crearPestanaArboles());
        pestanas.addTab(" Estadisticas", crearPestanaEstadisticas());
        pestanas.addTab(" Encriptacion", crearPestanaEncriptacion());
        add(pestanas, BorderLayout.CENTER);

        add(crearPanelReproductor(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        JLabel logo = new JLabel(" SMART PLAYER");
        logo.setFont(FUENTE_GRANDE);
        logo.setForeground(COLOR_ACENTO);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.setBackground(COLOR_PANEL);
        JButton btnCargar = crearBoton(" Cargar Biblioteca", COLOR_ACENTO2);
        btnCargar.addActionListener(e -> accionCargarBiblioteca());
        panelBotones.add(btnCargar);

        panel.add(logo, BorderLayout.WEST);
        panel.add(panelBotones, BorderLayout.EAST);
        panel.add(new JSeparator(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPestanaBiblioteca() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelBusqueda.setBackground(COLOR_FONDO);

        comboBuscarPor = new JComboBox<>(new String[]{"Nombre", "Artista", "Album", "Genero"});
        estilizar(comboBuscarPor);

        txtBuscar = new JTextField(25);
        estilizar(txtBuscar);

        JButton btnBuscar = crearBoton(" Buscar", COLOR_ACENTO);
        btnBuscar.addActionListener(e -> accionBuscar());

        JButton btnLimpiar = crearBoton(" Limpiar", new Color(180, 50, 50));
        btnLimpiar.addActionListener(e -> {
            refrescarTablaBiblioteca(null);
            txtBuscar.setText("");
        });

        panelBusqueda.add(new JLabel("Buscar por:") {
            {
                setForeground(COLOR_TEXTO);
            }
        });
        panelBusqueda.add(comboBuscarPor);
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnLimpiar);

        // Tabla
        String[] columnas = {"Nombre", "Artista", "Album", "Genero", "Duracion", "Tamaño", "Año", "Ruta"};
        modeloBiblioteca = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tablaBiblioteca = crearTabla(modeloBiblioteca);
        tablaBiblioteca.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    accionReproducirSeleccionada();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaBiblioteca);
        scroll.getViewport().setBackground(COLOR_FONDO);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        panelAcciones.setBackground(COLOR_FONDO);
        JButton btnAgregarCola = crearBoton(" Agregar a Cola", new Color(100, 100, 200));
        JButton btnAgregarPlaylist = crearBoton(" Agregar a Playlist", new Color(100, 160, 100));
        btnAgregarCola.addActionListener(e -> accionAgregarACola());
        btnAgregarPlaylist.addActionListener(e -> accionAgregarAPlaylist());
        panelAcciones.add(btnAgregarCola);
        panelAcciones.add(btnAgregarPlaylist);

        panel.add(panelBusqueda, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(panelAcciones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPestanaPlaylists() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloListaPlaylists = new DefaultListModel<>();
        listaPlaylists = new JList<>(modeloListaPlaylists);
        listaPlaylists.setBackground(COLOR_PANEL);
        listaPlaylists.setForeground(COLOR_TEXTO);
        listaPlaylists.setFont(FUENTE_NORMAL);
        listaPlaylists.setSelectionBackground(COLOR_ACENTO);
        listaPlaylists.addListSelectionListener(e -> mostrarCancionesPlaylist());

        JScrollPane scrollPlaylists = new JScrollPane(listaPlaylists);
        scrollPlaylists.setPreferredSize(new Dimension(220, 0));
        titularPanel(scrollPlaylists, "Playlists");

        JPanel panelBotonesP = new JPanel(new GridLayout(4, 1, 4, 4));
        panelBotonesP.setBackground(COLOR_FONDO);
        JButton btnNueva = crearBoton(" Nueva", COLOR_ACENTO);
        JButton btnEliminar = crearBoton(" Eliminar", new Color(180, 50, 50));
        JButton btnExportar = crearBoton(" Exportar", COLOR_ACENTO2);
        JButton btnImportar = crearBoton(" Importar", new Color(150, 100, 200));
        btnNueva.addActionListener(e -> accionNuevaPlaylist());
        btnEliminar.addActionListener(e -> accionEliminarPlaylist());
        btnExportar.addActionListener(e -> accionExportarPlaylist());
        btnImportar.addActionListener(e -> accionImportarPlaylist());
        panelBotonesP.add(btnNueva);
        panelBotonesP.add(btnEliminar);
        panelBotonesP.add(btnExportar);
        panelBotonesP.add(btnImportar);

        JPanel panelIzq = new JPanel(new BorderLayout(4, 4));
        panelIzq.setBackground(COLOR_FONDO);
        panelIzq.add(scrollPlaylists, BorderLayout.CENTER);
        panelIzq.add(panelBotonesP, BorderLayout.SOUTH);

        // Tabla de canciones de la playlist (derecha)
        String[] columnas = {"Nombre", "Artista", "Album", "Genero", "Duracion"};
        modeloPlaylist = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tablaPlaylist = crearTabla(modeloPlaylist);
        JScrollPane scrollCanciones = new JScrollPane(tablaPlaylist);
        scrollCanciones.getViewport().setBackground(COLOR_FONDO);
        titularPanel(scrollCanciones, "Canciones de la Playlist");

        JButton btnEliminarCancion = crearBoton(" Quitar cancion", new Color(180, 50, 50));
        JButton btnReproducir = crearBoton(" Reproducir Playlist", COLOR_ACENTO);
        btnEliminarCancion.addActionListener(e -> accionQuitarCancionPlaylist());
        btnReproducir.addActionListener(e -> accionReproducirPlaylist());
        JPanel panelSurP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        panelSurP.setBackground(COLOR_FONDO);
        panelSurP.add(btnEliminarCancion);
        panelSurP.add(btnReproducir);

        JPanel panelDer = new JPanel(new BorderLayout(4, 4));
        panelDer.setBackground(COLOR_FONDO);
        panelDer.add(scrollCanciones, BorderLayout.CENTER);
        panelDer.add(panelSurP, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzq, panelDer);
        split.setDividerLocation(240);
        split.setBackground(COLOR_FONDO);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPestanaArboles() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        areaLog = new JTextArea();
        areaLog.setBackground(COLOR_PANEL);
        areaLog.setForeground(new Color(0, 255, 100));
        areaLog.setFont(new Font("Consolas", Font.PLAIN, 11));
        areaLog.setEditable(false);
        JScrollPane scrollLog = new JScrollPane(areaLog);
        titularPanel(scrollLog, "Recorridos / Comparativas");

        JPanel panelBotones = new JPanel(new GridLayout(3, 3, 6, 6));
        panelBotones.setBackground(COLOR_FONDO);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        JButton[] botones = {
            crearBoton("ABB InOrden", COLOR_ACENTO2),
            crearBoton("ABB PreOrden", COLOR_ACENTO2),
            crearBoton("ABB PostOrden", COLOR_ACENTO2),
            crearBoton("AVL InOrden", COLOR_ACENTO),
            crearBoton("AVL PreOrden", COLOR_ACENTO),
            crearBoton("AVL PostOrden", COLOR_ACENTO),
            crearBoton(" Comparar Carga", new Color(200, 100, 0)),
            crearBoton(" Graphviz ABB", new Color(100, 0, 200)),
            crearBoton(" Graphviz AVL", new Color(100, 0, 200)),};

        botones[0].addActionListener(e -> mostrarRecorrido("ABB", "INORDEN"));
        botones[1].addActionListener(e -> mostrarRecorrido("ABB", "PREORDEN"));
        botones[2].addActionListener(e -> mostrarRecorrido("ABB", "POSTORDEN"));
        botones[3].addActionListener(e -> mostrarRecorrido("AVL", "INORDEN"));
        botones[4].addActionListener(e -> mostrarRecorrido("AVL", "PREORDEN"));
        botones[5].addActionListener(e -> mostrarRecorrido("AVL", "POSTORDEN"));
        botones[6].addActionListener(e -> mostrarComparativaCarga());
        botones[7].addActionListener(e -> generarGraphviz("ABB"));
        botones[8].addActionListener(e -> generarGraphviz("AVL"));

        for (JButton b : botones) {
            panelBotones.add(b);
        }

        lblEstABB = new JLabel("ABB: 0 nodos | Altura: 0");
        lblEstAVL = new JLabel("AVL: 0 nodos | Altura: 0");
        lblEstABB.setForeground(COLOR_ACENTO2);
        lblEstAVL.setForeground(COLOR_ACENTO);
        lblEstABB.setFont(FUENTE_NORMAL);
        lblEstAVL.setFont(FUENTE_NORMAL);
        JPanel panelEst = new JPanel(new GridLayout(1, 2, 10, 0));
        panelEst.setBackground(COLOR_FONDO);
        panelEst.add(lblEstABB);
        panelEst.add(lblEstAVL);

        JPanel panelSur = new JPanel(new BorderLayout(4, 4));
        panelSur.setBackground(COLOR_FONDO);
        panelSur.add(panelEst, BorderLayout.NORTH);
        panelSur.add(panelBotones, BorderLayout.CENTER);

        panel.add(scrollLog, BorderLayout.CENTER);
        panel.add(panelSur, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPestanaEstadisticas() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        areaEstadisticas = new JTextArea();
        areaEstadisticas.setBackground(COLOR_PANEL);
        areaEstadisticas.setForeground(COLOR_TEXTO);
        areaEstadisticas.setFont(new Font("Consolas", Font.PLAIN, 12));
        areaEstadisticas.setEditable(false);
        areaEstadisticas.setText("Carga una biblioteca para ver estadísticas...");
        JScrollPane scroll = new JScrollPane(areaEstadisticas);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelBotones.setBackground(COLOR_FONDO);
        JButton btnActualizar = crearBoton(" Actualizar", COLOR_ACENTO);
        JButton btnDuplicados = crearBoton(" Ver Duplicados", new Color(200, 100, 0));

        String[] tipos = {"Buscar (nombre):", "Buscar (artista):", "Buscar (album):"};
        JTextField txtMedir = new JTextField(20);
        estilizar(txtMedir);
        JButton btnMedir = crearBoton(" Medir Busqueda", COLOR_ACENTO2);

        btnActualizar.addActionListener(e -> areaEstadisticas.setText(estadisticas.getReporteCompleto()));
        btnDuplicados.addActionListener(e -> mostrarDuplicados());
        btnMedir.addActionListener(e -> {
            String t = txtMedir.getText().trim();
            if (!t.isEmpty()) {
                areaEstadisticas.setText(estadisticas.getResumenBusqueda(t));
            }
        });

        panelBotones.add(btnActualizar);
        panelBotones.add(btnDuplicados);
        panelBotones.add(new JLabel("Nombre Cancion:") {
            {
                setForeground(COLOR_TEXTO);
            }
        });
        panelBotones.add(txtMedir);
        panelBotones.add(btnMedir);

        panel.add(panelBotones, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPestanaEncriptacion() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea areaEnc = new JTextArea();
        areaEnc.setBackground(COLOR_PANEL);
        areaEnc.setForeground(new Color(255, 215, 0));
        areaEnc.setFont(new Font("Consolas", Font.PLAIN, 11));
        areaEnc.setEditable(false);
        areaEnc.setText("Selecciona una playlist para encriptar/exportar...");

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        panelBotones.setBackground(COLOR_FONDO);

        JComboBox<String> comboRecorrido = new JComboBox<>(
                new String[]{"InOrden", "PreOrden", "PostOrden"});
        estilizar(comboRecorrido);

        JTextField txtNombrePlaylist = new JTextField(18);
        txtNombrePlaylist.setToolTipText("Nombre de la playlist");
        estilizar(txtNombrePlaylist);

        JButton btnEnc = crearBoton(" Encriptar y Exportar", new Color(180, 50, 220));
        JButton btnDec = crearBoton(" Importar y Desencriptar", COLOR_ACENTO2);

        btnEnc.addActionListener(e -> {
            String nombre = txtNombrePlaylist.getText().trim();
            Playlist p = gestor.buscarPlaylist(nombre);
            if (p == null) {
                areaEnc.setText("Playlist no encontrada: " + nombre);
                return;
            }
            EncriptadorPlaylist.TipoRecorrido rec = getTipoRecorrido(comboRecorrido.getSelectedIndex());
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                boolean ok = GestorArchivos.exportarPlaylistEncriptada(p, fc.getSelectedFile().getAbsolutePath(), rec);
                areaEnc.setText(ok ? " Playlist encriptada y exportada correctamente\nRecorrido: " + rec.name()
                        : "Error al exportar");
            }
        });

        btnDec.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String nombre = txtNombrePlaylist.getText().trim();
                Playlist ref = gestor.buscarPlaylist(nombre);
                if (ref == null) {
                    areaEnc.setText("Playlist de referencia no encontrada.");
                    return;
                }
                EncriptadorPlaylist.TipoRecorrido rec = getTipoRecorrido(comboRecorrido.getSelectedIndex());
                Playlist importada = GestorArchivos.importarPlaylistEncriptada(fc.getSelectedFile().getAbsolutePath(), ref, rec);
                if (importada != null) {
                    String nuevoNombre = importada.getNombre() + "_importada";

                    gestor.crearPlaylist(nuevoNombre);
                    if (importada.getCanciones() != null) {
                        for (Cancion cancion : importada.getCanciones()) {
                            gestor.agregarCancion(nuevoNombre, cancion);
                        }
                    }

                    areaEnc.setText(" Playlist desencriptada: " + importada.getNombre()
                            + "\n" + importada.getTotalCanciones() + " canciones recuperadas.");
                    refrescarListaPlaylists();
                } else {
                    areaEnc.setText(" Error al desencriptar.");
                }
            }
        });

        panelBotones.add(new JLabel("Playlist:") {
            {
                setForeground(COLOR_TEXTO);
            }
        });
        panelBotones.add(txtNombrePlaylist);
        panelBotones.add(new JLabel("Recorrido:") {
            {
                setForeground(COLOR_TEXTO);
            }
        });
        panelBotones.add(comboRecorrido);
        panelBotones.add(btnEnc);
        panelBotones.add(btnDec);

        panel.add(panelBotones, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaEnc), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelReproductor() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        panel.setPreferredSize(new Dimension(0, 115));

        JPanel panelInfo = new JPanel(new GridLayout(2, 1));
        panelInfo.setBackground(COLOR_PANEL);
        lblCancionActual = new JLabel("Sin reproduccion");
        lblCancionActual.setForeground(COLOR_TEXTO);
        lblCancionActual.setFont(FUENTE_TITULO);
        lblArtista = new JLabel("—");
        lblArtista.setForeground(COLOR_TEXTO_SEC);
        lblArtista.setFont(FUENTE_NORMAL);
        panelInfo.add(lblCancionActual);
        panelInfo.add(lblArtista);
        panelInfo.setPreferredSize(new Dimension(220, 0));

        JPanel panelControles = new JPanel(new BorderLayout(0, 4));
        panelControles.setBackground(COLOR_PANEL);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panelBotones.setBackground(COLOR_PANEL);
        btnPrev = crearBotonIcono("⏮");
        btnPlay = crearBotonIcono("▶");
        btnPause = crearBotonIcono("⏸");
        btnStop = crearBotonIcono("⏹");
        btnNext = crearBotonIcono("⏭");

        btnPrev.addActionListener(e -> {
            reproductor.anterior();
            mostrarCancionActual();
            areaColaMini.setText(reproductor.getColaString());
            iniciarTimerProgreso();
        });
        btnPlay.addActionListener(e -> {
            accionPlay();
            iniciarTimerProgreso();
        });
        btnPause.addActionListener(e -> {
            reproductor.pausar();
            detenerTimerProgreso();
        });
        btnStop.addActionListener(e -> {
            reproductor.detener();
            mostrarCancionActual();
            detenerTimerProgreso();
        });
        btnNext.addActionListener(e -> {
            reproductor.siguiente();
            mostrarCancionActual();
            areaColaMini.setText(reproductor.getColaString());
            iniciarTimerProgreso();
        });

        panelBotones.add(btnPrev);
        panelBotones.add(btnPlay);
        panelBotones.add(btnPause);
        panelBotones.add(btnStop);
        panelBotones.add(btnNext);

        barraProgreso = new JProgressBar(0, 100);
        barraProgreso.setForeground(COLOR_ACENTO);
        barraProgreso.setBackground(new Color(50, 50, 50));
        barraProgreso.setStringPainted(false);

        panelControles.add(panelBotones, BorderLayout.CENTER);
        panelControles.add(barraProgreso, BorderLayout.SOUTH);

        // Controles de modo
        JPanel panelModo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        panelModo.setBackground(COLOR_PANEL);
        comboModo = new JComboBox<>(new String[]{" Normal", " Aleatorio", " Circular"});
        estilizar(comboModo);
        comboModo.addActionListener(e -> {
            ReproductorMusical.ModoReproduccion[] modos = {
                ReproductorMusical.ModoReproduccion.NORMAL,
                ReproductorMusical.ModoReproduccion.ALEATORIO,
                ReproductorMusical.ModoReproduccion.CIRCULAR
            };
            reproductor.setModo(modos[comboModo.getSelectedIndex()]);
        });
        lblTiempo = new JLabel("00:00");
        lblTiempo.setForeground(COLOR_TEXTO_SEC);
        panelModo.add(new JLabel("Modo:") {
            {
                setForeground(COLOR_TEXTO);
            }
        });
        panelModo.add(comboModo);
        panelModo.add(lblTiempo);

        // —— 4. COLA DE REPRODUCCIÓN ——
        areaColaMini = new JTextArea();
        areaColaMini.setBackground(new Color(25, 25, 25));
        areaColaMini.setForeground(COLOR_ACENTO);
        areaColaMini.setFont(new Font("Consolas", Font.PLAIN, 11));
        areaColaMini.setEditable(false);
        areaColaMini.setText("Cola vacía");

        JScrollPane scrollColaMini = new JScrollPane(areaColaMini);
        scrollColaMini.setPreferredSize(new Dimension(220, 65));
        scrollColaMini.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50), 1));
        scrollColaMini.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel panelExtremoDerecho = new JPanel(new BorderLayout(0, 4));
        panelExtremoDerecho.setBackground(COLOR_PANEL);
        panelExtremoDerecho.add(panelModo, BorderLayout.NORTH);
        panelExtremoDerecho.add(scrollColaMini, BorderLayout.SOUTH);

        panel.add(panelInfo, BorderLayout.WEST);
        panel.add(panelControles, BorderLayout.CENTER);
        panel.add(panelExtremoDerecho, BorderLayout.EAST);
        return panel;
    }

    private void accionCargarBiblioteca() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogTitle("Selecciona la carpeta de musica");
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String ruta = fc.getSelectedFile().getAbsolutePath();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<List<Cancion>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Cancion> doInBackground() {
                return biblioteca.cargarDesde(ruta);
            }

            @Override
            protected void done() {
                try {
                    List<Cancion> canciones = get();
                    refrescarTablaBiblioteca(canciones);
                    JOptionPane.showMessageDialog(VentanaPrincipal.this,
                            " Cargadas " + canciones.size() + " canciones.\n"
                            + biblioteca.getResumenComparativaCarga(),
                            "Carga completada", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(VentanaPrincipal.this,
                            "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }

    private void accionBuscar() {

        String termino = txtBuscar.getText().trim();
        if (termino.isEmpty()) {
            refrescarTablaBiblioteca(null);
            return;
        }
        String criterio = (String) comboBuscarPor.getSelectedItem();
        List<Cancion> resultado;
        switch (criterio) {
            case "Artista":
                resultado = biblioteca.getAbb().buscarPorArtista(termino);
                break;
            case "Album":
                resultado = biblioteca.getAbb().buscarPorAlbum(termino);
                break;
            case "Genero":
                resultado = biblioteca.getAbb().buscarPorGenero(termino);
                break;
            default: {
                Cancion c = biblioteca.getAbb().buscar(termino);
                resultado = c != null ? List.of(c) : List.of();
            }
        }

        refrescarTablaBiblioteca(resultado);
        estadisticas.medirBusqueda(termino);
        lblEstABB.setText("ABB: " + biblioteca.getAbb().getTotalNodos() + " nodos | "
                + String.format("%.4f ms", estadisticas.getUltimaBusquedaABB() / 1_000_000.0));
        lblEstAVL.setText("AVL: " + biblioteca.getAvl().getTotalNodos() + " nodos | "
                + String.format("%.4f ms", estadisticas.getUltimaBusquedaAVL() / 1_000_000.0));
    }

    private void accionReproducirSeleccionada() {
        Cancion cancionAReproducir = null;
        if (!reproductor.getColaReproduccion().isEmpty()) {
            cancionAReproducir = reproductor.getColaReproduccion().dequeue();
        } else {
            int fila = tablaBiblioteca.getSelectedRow();
            if (fila < 0) {
                return;
            }
            
            String nombre = (String) modeloBiblioteca.getValueAt(fila, 0);
            cancionAReproducir = biblioteca.getAbb().buscar(nombre);

            if (cancionAReproducir == null) {
                cancionAReproducir = biblioteca.getAvl().buscar(nombre);
            }
        }

        if (cancionAReproducir != null) {
            reproductor.reproducir(cancionAReproducir);
            lblCancionActual.setText(cancionAReproducir.getNombre());
            lblArtista.setText(
                    cancionAReproducir.getArtista()
                    + " • "
                    + cancionAReproducir.getAlbum()
            );
        }
    }

    private void accionAgregarACola() {
        int fila = tablaBiblioteca.getSelectedRow();
        if (fila < 0) {
            mostrarMensaje("Selecciona una cancion primero");
            return;
        }
        String nombre = (String) modeloBiblioteca.getValueAt(fila, 0);
        Cancion c = biblioteca.getAbb().buscar(nombre);
        if (c != null) {
            reproductor.encolar(c);
            mostrarMensaje("Cancion agregada a la cola");
            areaColaMini.setText(reproductor.getColaString());
        }
    }

    private void accionAgregarAPlaylist() {
        int fila = tablaBiblioteca.getSelectedRow();
        if (fila < 0) {
            mostrarMensaje("Selecciona una canción primero.");
            return;
        }
        String nombre = (String) modeloBiblioteca.getValueAt(fila, 0);
        Cancion c = biblioteca.getAbb().buscar(nombre);
        if (c == null) {
            return;
        }
        List<Playlist> todas = gestor.getTodas();
        if (todas.isEmpty()) {
            mostrarMensaje("Primero crea una playlist");
            return;
        }
        String[] nombres = todas.stream().map(Playlist::getNombre).toArray(String[]::new);
        String elegida = (String) JOptionPane.showInputDialog(this, "Selecciona la playlist:", "Agregar a Playlist", JOptionPane.PLAIN_MESSAGE, null, nombres, nombres[0]);
        if (elegida != null) {
            gestor.agregarCancion(elegida, c);
            mostrarMensaje("Cancion agregada a: " + elegida);
        }
    }

    private void accionNuevaPlaylist() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre de la nueva playlist:");
        if (nombre != null && !nombre.trim().isEmpty()) {
            gestor.crearPlaylist(nombre.trim());
            refrescarListaPlaylists();
        }
    }

    private void accionEliminarPlaylist() {
        String sel = listaPlaylists.getSelectedValue();
        if (sel == null) {
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this, "Eliminar la playlist '" + sel + "'?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            gestor.eliminarPlaylist(sel);
            refrescarListaPlaylists();
            modeloPlaylist.setRowCount(0);
        }
    }

    private void accionExportarPlaylist() {
        String sel = listaPlaylists.getSelectedValue();
        if (sel == null) {
            mostrarMensaje("Selecciona una playlist primero.");
            return;
        }
        Playlist p = gestor.buscarPlaylist(sel);
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            boolean ok = GestorArchivos.exportarPlaylist(p, fc.getSelectedFile().getAbsolutePath());
            mostrarMensaje(ok ? "Playlist exportada" : "Error al exportar");
        }
    }

    private void accionImportarPlaylist() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            Playlist p = GestorArchivos.importarPlaylist(fc.getSelectedFile().getAbsolutePath());
            if (p != null) {
                gestor.crearPlaylist(p.getNombre());
                if (p.getCanciones() != null) {
                    for (Cancion cancion : p.getCanciones()) {
                        gestor.agregarCancion(p.getNombre(), cancion);
                    }
                }

                refrescarListaPlaylists();
                mostrarMensaje(" Playlist importada exitosamente con sus canciones: " + p.getNombre());
            } else {
                mostrarMensaje(" Error al importar playlist");
            }
        }
    }

    private void accionQuitarCancionPlaylist() {
        String playlistSeleccionada = listaPlaylists.getSelectedValue();
        int filaSeleccionada = tablaPlaylist.getSelectedRow();

        if (playlistSeleccionada == null || filaSeleccionada < 0) {
            return;
        }

        String nombreCancion = (String) modeloPlaylist.getValueAt(filaSeleccionada, 0);

        gestor.eliminarCancion(playlistSeleccionada, nombreCancion);
        mostrarCancionesPlaylist();
    }

    private void accionReproducirPlaylist() {

        String nombrePlaylist = listaPlaylists.getSelectedValue();

        if (nombrePlaylist == null) {
            return;
        }

        Playlist playlist = gestor.buscarPlaylist(nombrePlaylist);

        if (playlist == null) {
            return;
        }

        for (Cancion cancion : playlist.getCanciones()) {
            reproductor.encolar(cancion);
        }

        if (reproductor.getModo()
                != ReproductorMusical.ModoReproduccion.CIRCULAR) {
            reproductor.setModo(ReproductorMusical.ModoReproduccion.NORMAL);
        }

        Cancion primeraCancion = playlist.getCanciones().getPrimero();

        if (primeraCancion != null) {
            reproductor.reproducir(primeraCancion);
            lblCancionActual.setText(primeraCancion.getNombre());
            lblArtista.setText(primeraCancion.getArtista());
        }
    }

    private void accionPlay() {
        if (reproductor.isPausado()) {
            reproductor.continuar();
        } else {
            accionReproducirSeleccionada();
        }
    }

    private void mostrarRecorrido(String arbol, String tipo) {
        if (biblioteca.getAbb().isEmpty()) {
            areaLog.setText("Carga la biblioteca primero.");
            return;
        }
        List<Cancion> lista;
        if (arbol.equals("ABB")) {
            lista = tipo.equals("INORDEN") ? biblioteca.getAbb().inOrden()
                    : tipo.equals("PREORDEN") ? biblioteca.getAbb().preOrden()
                    : biblioteca.getAbb().postOrden();
        } else {
            lista = tipo.equals("INORDEN") ? biblioteca.getAvl().inOrden()
                    : tipo.equals("PREORDEN") ? biblioteca.getAvl().preOrden()
                    : biblioteca.getAvl().postOrden();
        }
        StringBuilder sb = new StringBuilder("=== " + arbol + " " + tipo + " ===\n");
        int i = 1;
        for (Cancion c : lista) {
            sb.append(i++).append(". ").append(c.getArtista()).append(" - ").append(c.getNombre()).append("\n");
            if (i > 500) {
                sb.append("... (truncado a 500 entradas)");
                break;
            }
        }
        areaLog.setText(sb.toString());
        areaLog.setCaretPosition(0);
    }

    private void mostrarComparativaCarga() {
        areaLog.setText(biblioteca.getResumenComparativaCarga());
    }

    private void generarGraphviz(String arbol) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        String dir = fc.getSelectedFile().getAbsolutePath();
        String dot = arbol.equals("ABB")
                ? VisualizadorArbol.generarDotABB(biblioteca.getAbb())
                : VisualizadorArbol.generarDotAVL(biblioteca.getAvl());
        String nombre = arbol.toLowerCase() + "_smartplayer";
        String png = VisualizadorArbol.renderizarComoPNG(dot, nombre, dir);
        if (png != null) {
            mostrarMensaje("Imagen generada: " + png);
        } else {
            mostrarMensaje(" Archivo .dot guardado. Instala Graphviz para generar PNG");
        }
    }

    private void mostrarDuplicados() {
        List<Cancion[]> dups = estadisticas.getDuplicados();
        if (dups.isEmpty()) {
            areaEstadisticas.setText("No se encontraron duplicados.");
            return;
        }
        StringBuilder sb = new StringBuilder("=== DUPLICADOS DETECTADOS ===\n");
        long totalBytes = 0;
        for (Cancion[] par : dups) {
            sb.append("• ").append(par[0].getNombre()).append("\n")
                    .append("  Original : ").append(par[0].getRuta()).append("\n")
                    .append("  Duplicado: ").append(par[1].getRuta()).append("\n")
                    .append("  Tamaño   : ").append(par[1].getTamanoFormateado()).append("\n\n");
            totalBytes += par[1].getTamano();
        }
        sb.append(String.format("Total duplicados: %d | Espacio desperdiciado: %.2f MB",
                dups.size(), totalBytes / (1024.0 * 1024.0)));
        areaEstadisticas.setText(sb.toString());
    }

    private void refrescarTablaBiblioteca(List<Cancion> canciones) {
        modeloBiblioteca.setRowCount(0);
        if (canciones == null) {
            for (Cancion c : biblioteca.getListaBiblioteca()) {
                modeloBiblioteca.addRow(new Object[]{
                    c.getNombre(), c.getArtista(), c.getAlbum(), c.getGenero(), c.getDuracionFormateada(), c.getTamanoFormateado(), c.getAnio(),
                    c.getRuta()});
            }
        } else {
            for (Cancion c : canciones) {
                modeloBiblioteca.addRow(new Object[]{
                    c.getNombre(), c.getArtista(), c.getAlbum(), c.getGenero(), c.getDuracionFormateada(), c.getTamanoFormateado(), c.getAnio(),
                    c.getRuta()});
            }
        }
    }

    private void refrescarListaPlaylists() {
        modeloListaPlaylists.clear();

        for (Playlist p : gestor.getTodas()) {
            modeloListaPlaylists.addElement(p.getNombre());
        }
    }

    private void refrescarCancionReproducida() {
        Cancion cancion = reproductor.getCancionActual();

        if (cancion != null) {
            String nombre = cancion.getNombre();
            String info = cancion.getArtista() + " - " + cancion.getAlbum();

            lblCancionActual.setText(nombre);
            lblArtista.setText(info);
        }
    }

    private void mostrarCancionesPlaylist() {
        modeloPlaylist.setRowCount(0);

        String nombrePlaylist = listaPlaylists.getSelectedValue();
        if (nombrePlaylist == null) {
            return;
        }

        Playlist playlist = gestor.buscarPlaylist(nombrePlaylist);

        if (playlist == null) {
            return;
        }

        for (Cancion cancion : playlist.getCanciones()) {
            modeloPlaylist.addRow(new Object[]{
                cancion.getNombre(),
                cancion.getArtista(),
                cancion.getAlbum(),
                cancion.getGenero(),
                cancion.getDuracionFormateada()
            });
        }
    }

    private void actualizarEstadosArboles() {
        lblEstABB.setText("ABB: " + biblioteca.getAbb().getTotalNodos()
                + " nodos | Altura: " + biblioteca.getAbb().altura());
        lblEstAVL.setText("AVL: " + biblioteca.getAvl().getTotalNodos()
                + " nodos | Altura: " + biblioteca.getAvl().altura());
    }

    private JTable crearTabla(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(isRowSelected(row) ? COLOR_ACENTO
                        : (row % 2 == 0 ? COLOR_FILA_PAR : COLOR_FILA_IMPAR));
                c.setForeground(isRowSelected(row) ? Color.WHITE : COLOR_TEXTO);
                return c;
            }
        };
        tabla.setBackground(COLOR_FONDO);
        tabla.setForeground(COLOR_TEXTO);
        tabla.setFont(FUENTE_NORMAL);
        tabla.setRowHeight(22);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.getTableHeader().setBackground(COLOR_PANEL);
        tabla.getTableHeader().setForeground(COLOR_ACENTO);
        tabla.getTableHeader().setFont(FUENTE_TITULO);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return tabla;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(FUENTE_NORMAL);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });
        return btn;
    }

    private JButton crearBotonIcono(String icono) {
        JButton btn = new JButton(icono);
        btn.setBackground(COLOR_PANEL);
        btn.setForeground(COLOR_TEXTO);
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(COLOR_ACENTO);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setForeground(COLOR_TEXTO);
            }
        });
        return btn;
    }

    private void estilizar(JComponent c) {
        c.setBackground(new Color(50, 50, 50));
        c.setForeground(COLOR_TEXTO);
        c.setFont(FUENTE_NORMAL);
        if (c instanceof JTextField) {
            ((JTextField) c).setCaretColor(COLOR_TEXTO);
        }
    }

    private void titularPanel(JComponent panel, String titulo) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_ACENTO, 1),
                titulo, TitledBorder.LEFT, TitledBorder.TOP,
                FUENTE_TITULO, COLOR_ACENTO);
        panel.setBorder(border);
    }

    private void mostrarMensaje(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Smart Player", JOptionPane.INFORMATION_MESSAGE);
    }

    private EncriptadorPlaylist.TipoRecorrido getTipoRecorrido(int idx) {
        switch (idx) {
            case 1:
                return EncriptadorPlaylist.TipoRecorrido.PRE_ORDEN;
            case 2:
                return EncriptadorPlaylist.TipoRecorrido.POST_ORDEN;
            default:
                return EncriptadorPlaylist.TipoRecorrido.IN_ORDEN;
        }
    }

    private void mostrarCancionActual() {
        Cancion c = reproductor.getCancionActual();
        if (c != null) {
            lblCancionActual.setText(c.getNombre());
            lblArtista.setText(c.getArtista() + " • " + c.getAlbum());
        } else {
            lblCancionActual.setText("Sin reproducción");
            lblArtista.setText("—");
        }
    }

    private void iniciarTimerProgreso() {

        inicioReproduccion = System.currentTimeMillis();

        if (timerProgreso != null) {
            timerProgreso.stop();
        }

        timerProgreso = new Timer(1000, e -> {

            Cancion c = reproductor.getCancionActual();

            if (c == null) {
                return;
            }

            long segundosActuales
                    = (System.currentTimeMillis() - inicioReproduccion) / 1000;

            long duracionTotal = (long) c.getDuracion();

            if (duracionTotal <= 0) {
                return;
            }

            int porcentaje = (int) ((segundosActuales * 100.0) / duracionTotal);

            barraProgreso.setValue(
                    Math.min(porcentaje, 100)
            );

            lblTiempo.setText(
                    String.format("%02d:%02d",
                            segundosActuales / 60,
                            segundosActuales % 60)
            );
        });

        timerProgreso.start();
    }

    private void detenerTimerProgreso() {

        if (timerProgreso != null) {
            timerProgreso.stop();
        }

        barraProgreso.setValue(0);
        lblTiempo.setText("00:00");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(VentanaPrincipal::new);
    }
}
