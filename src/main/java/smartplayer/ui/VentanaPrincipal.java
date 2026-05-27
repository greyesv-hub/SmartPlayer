package smartplayer.ui;

import smartplayer.model.Cancion;
import smartplayer.player.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import smartplayer.model.Playlist;

/**
 * Interfaz Grafica Principal de Smart Player.
 *
 */
public class VentanaPrincipal extends JFrame {

     //  Colores del tema 
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

    //  Componentes UI 
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


    public VentanaPrincipal() {
        super("🎵 Smart Player - Sistema de Gestión Musical");
        configurarVentana();
        inicializarUI();
        setVisible(true);
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

        // Panel superior con logo y controles globales
        add(crearPanelSuperior(), BorderLayout.NORTH);

        // Panel central con pestañas
        pestanas = new JTabbedPane(JTabbedPane.TOP);
        pestanas.setBackground(COLOR_PANEL);
        pestanas.setForeground(COLOR_TEXTO);
        pestanas.setFont(FUENTE_TITULO);
        pestanas.addTab("📚 Biblioteca", crearPestanaBiblioteca());
        pestanas.addTab("🎵 Playlists", crearPestanaPlaylists());
        pestanas.addTab("🌲 Árboles", crearPestanaArboles());
        pestanas.addTab("📊 Estadísticas", crearPestanaEstadisticas());
        pestanas.addTab("🔐 Encriptación", crearPestanaEncriptacion());
        add(pestanas, BorderLayout.CENTER);

        // Panel inferior con reproductor
        add(crearPanelReproductor(), BorderLayout.SOUTH);
    }

    //  Panel Superior 
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        JLabel logo = new JLabel("🎵 SMART PLAYER");
        logo.setFont(FUENTE_GRANDE);
        logo.setForeground(COLOR_ACENTO);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.setBackground(COLOR_PANEL);
        JButton btnCargar = crearBoton("📂 Cargar Biblioteca", COLOR_ACENTO2);
        btnCargar.addActionListener(e -> accionCargarBiblioteca());
        panelBotones.add(btnCargar);

        panel.add(logo, BorderLayout.WEST);
        panel.add(panelBotones, BorderLayout.EAST);
        panel.add(new JSeparator(), BorderLayout.SOUTH);
        return panel;
    }

    //  Pestaña Biblioteca 
    private JPanel crearPestanaBiblioteca() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Barra de busqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelBusqueda.setBackground(COLOR_FONDO);

        comboBuscarPor = new JComboBox<>(new String[]{"Nombre", "Artista", "Album", "Genero"});
        estilizar(comboBuscarPor);

        txtBuscar = new JTextField(25);
        estilizar(txtBuscar);

        JButton btnBuscar = crearBoton("🔍 Buscar", COLOR_ACENTO);
        btnBuscar.addActionListener(e -> accionBuscar());

        JButton btnLimpiar = crearBoton("✖ Limpiar", new Color(180, 50, 50));
        btnLimpiar.addActionListener(e -> refrescarTablaBiblioteca(null));

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
        String[] columnas = {"Nombre", "Artista", "Album", "Genero", "DuracioSn", "Tamaño", "Año", "Ruta"};
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

        // Barra inferior con acciones rapidas
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        panelAcciones.setBackground(COLOR_FONDO);
        JButton btnAgregarCola = crearBoton("➕ Cola", new Color(100, 100, 200));
        JButton btnAgregarPlaylist = crearBoton("📋 Agregar a Playlist", new Color(100, 160, 100));
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

        // Lista de playlists (izquierda)
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
        JButton btnNueva = crearBoton("➕ Nueva", COLOR_ACENTO);
        JButton btnEliminar = crearBoton("🗑 Eliminar", new Color(180, 50, 50));
        JButton btnExportar = crearBoton("💾 Exportar", COLOR_ACENTO2);
        JButton btnImportar = crearBoton("📂 Importar", new Color(150, 100, 200));
        btnNueva.addActionListener(e -> accionNuevaPlaylist());
        btnEliminar.addActionListener(e -> accionEliminarPlaylist());
        panelBotonesP.add(btnNueva);
        panelBotonesP.add(btnEliminar);
        panelBotonesP.add(btnExportar);
        panelBotonesP.add(btnImportar);

        JPanel panelIzq = new JPanel(new BorderLayout(4, 4));
        panelIzq.setBackground(COLOR_FONDO);
        panelIzq.add(scrollPlaylists, BorderLayout.CENTER);
        panelIzq.add(panelBotonesP, BorderLayout.SOUTH);

        // Tabla de canciones de la playlist (derecha)
        String[] columnas = {"Nombre", "Artista", "Álbum", "Género", "Duración"};
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

        JButton btnEliminarCancion = crearBoton("🗑 Quitar canción", new Color(180, 50, 50));
        JButton btnReproducir = crearBoton("▶ Reproducir Playlist", COLOR_ACENTO);
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

        // Area para recorridos
        areaLog = new JTextArea();
        areaLog.setBackground(COLOR_PANEL);
        areaLog.setForeground(new Color(0, 255, 100));
        areaLog.setFont(new Font("Consolas", Font.PLAIN, 11));
        areaLog.setEditable(false);
        JScrollPane scrollLog = new JScrollPane(areaLog);
        titularPanel(scrollLog, "Recorridos / Comparativas");

        // Botones de recorridos
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
            crearBoton("📊 Comparar Carga", new Color(200, 100, 0)),
            crearBoton("🌲 Graphviz ABB", new Color(100, 0, 200)),
            crearBoton("🌲 Graphviz AVL", new Color(100, 0, 200)),};

        botones[6].addActionListener(e -> mostrarComparativaCarga());

        for (JButton b : botones) {
            panelBotones.add(b);
        }

        // Etiquetas de estado
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
        JButton btnActualizar = crearBoton("🔄 Actualizar", COLOR_ACENTO);
        JButton btnDuplicados = crearBoton("🔍 Ver Duplicados", new Color(200, 100, 0));

        String[] tipos = {"Buscar (nombre):", "Buscar (artista):", "Buscar (álbum):"};
        JTextField txtMedir = new JTextField(20);
        estilizar(txtMedir);
        JButton btnMedir = crearBoton("⏱ Medir Búsqueda", COLOR_ACENTO2);

        panelBotones.add(btnActualizar);
        panelBotones.add(btnDuplicados);
        panelBotones.add(new JLabel("Término:") {
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

    //  Pestaña Encriptacion
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

        JButton btnEnc = crearBoton("🔐 Encriptar y Exportar", new Color(180, 50, 220));
        JButton btnDec = crearBoton("🔓 Importar y Desencriptar", COLOR_ACENTO2);

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

    // Panel Reproductor 
    private JPanel crearPanelReproductor() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        panel.setPreferredSize(new Dimension(0, 100));

        // Info de la cancion
        JPanel panelInfo = new JPanel(new GridLayout(2, 1));
        panelInfo.setBackground(COLOR_PANEL);
        lblCancionActual = new JLabel("Sin reproducción");
        lblCancionActual.setForeground(COLOR_TEXTO);
        lblCancionActual.setFont(FUENTE_TITULO);
        lblArtista = new JLabel("—");
        lblArtista.setForeground(COLOR_TEXTO_SEC);
        lblArtista.setFont(FUENTE_NORMAL);
        panelInfo.add(lblCancionActual);
        panelInfo.add(lblArtista);
        panelInfo.setPreferredSize(new Dimension(250, 0));

        // Controles centrales
        JPanel panelControles = new JPanel(new BorderLayout(0, 4));
        panelControles.setBackground(COLOR_PANEL);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panelBotones.setBackground(COLOR_PANEL);
        btnPrev = crearBotonIcono("⏮");
        btnPlay = crearBotonIcono("▶");
        btnPause = crearBotonIcono("⏸");
        btnStop = crearBotonIcono("⏹");
        btnNext = crearBotonIcono("⏭");
        btnPrev.addActionListener(e -> { reproductor.anterior(); refrescarCancionReproducida(); });
        btnPlay.addActionListener(e -> accionPlay());
        btnPause.addActionListener(e -> reproductor.pausar());
        btnStop.addActionListener(e -> { reproductor.detener(); refrescarCancionReproducida(); });
        btnNext.addActionListener(e -> { reproductor.siguiente(); refrescarCancionReproducida();});
        panelBotones.add(btnPrev);
        panelBotones.add(btnPlay);
        panelBotones.add(btnPause);
        panelBotones.add(btnStop);
        panelBotones.add(btnNext);

        barraProgreso = new JProgressBar(0, 100);
        barraProgreso.setForeground(COLOR_ACENTO);
        barraProgreso.setBackground(new Color(50, 50, 50));
        barraProgreso.setStringPainted(false);

        panelControles.add(panelBotones, BorderLayout.NORTH);
        panelControles.add(barraProgreso, BorderLayout.SOUTH);

        // Controles de modo
        JPanel panelModo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        panelModo.setBackground(COLOR_PANEL);
        comboModo = new JComboBox<>(new String[]{"▶ Normal", "🔀 Aleatorio", "🔁 Circular"});
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

        panel.add(panelInfo, BorderLayout.WEST);
        panel.add(panelControles, BorderLayout.CENTER);
        panel.add(panelModo, BorderLayout.EAST);
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
                            "✅ Cargadas " + canciones.size() + " canciones.\n"
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

                resultado = biblioteca.listaBiblioteca.buscarTodos(
                        c -> c.getArtista().toLowerCase()
                                .contains(termino.toLowerCase())
                ).toList();
                break;

            case "Album":

                resultado = biblioteca.listaBiblioteca.buscarTodos(
                        c -> c.getAlbum().toLowerCase()
                                .contains(termino.toLowerCase())).toList();
                break;

            case "Genero":

                resultado = biblioteca.listaBiblioteca.buscarTodos(
                        c -> c.getGenero().toLowerCase()
                                .contains(termino.toLowerCase())).toList();
                break;

            default: {

                Cancion c = biblioteca.listaBiblioteca.buscar(
                        cancion -> cancion.getNombre().toLowerCase()
                                .contains(termino.toLowerCase())
                );

                resultado = c != null ? List.of(c) : List.of();
            }
        }

        refrescarTablaBiblioteca(resultado);
    }

    private void accionReproducirSeleccionada() {
        int fila = tablaBiblioteca.getSelectedRow();
        if (fila < 0) {
            return;
        }
        String nombre = (String) modeloBiblioteca.getValueAt(fila, 0);
        Cancion c = biblioteca.listaBiblioteca.buscar(
                cancion -> cancion.getNombre().toLowerCase()
                        .contains(nombre.toLowerCase())
        );
        if (c != null) {
            reproductor.reproducir(c);
            refrescarCancionReproducida();
        }
    }

    private void accionAgregarACola() {
        int fila = tablaBiblioteca.getSelectedRow();
        if (fila < 0) {
            mostrarMensaje("Selecciona una cancion");
            return;
        }
        String nombre = (String) modeloBiblioteca.getValueAt(fila, 0);
        Cancion c = biblioteca.listaBiblioteca.buscar(
                cancion -> cancion.getNombre().toLowerCase()
                        .contains(nombre.toLowerCase())
        );
        if (c != null) {
            reproductor.encolar(c);
            mostrarMensaje("Cancion agregada a la cola");
        }
    }

    private void accionAgregarAPlaylist() {
        int fila = tablaBiblioteca.getSelectedRow();
        if (fila < 0) {
            mostrarMensaje("Selecciona una cancion");
            return;
        }
        String nombre = (String) modeloBiblioteca.getValueAt(fila, 0);
        Cancion c = biblioteca.listaBiblioteca.buscar(
                cancion -> cancion.getNombre().toLowerCase()
                        .contains(nombre.toLowerCase())
        );
        if (c == null) {
            return;
        }
        List<Playlist> todas = gestor.getTodas();
        if (todas.isEmpty()) {
            mostrarMensaje("Crea una playlist");
            return;
        }
        String[] nombres = todas.stream().map(Playlist::getNombre).toArray(String[]::new);
        String elegida = (String) JOptionPane.showInputDialog(this,"Selecciona la playlist:", "Agregar a Playlist",
                JOptionPane.PLAIN_MESSAGE, null, nombres, nombres[0]);
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
        int conf = JOptionPane.showConfirmDialog(this,
                "Eliminar la playlist '" + sel + "'?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            gestor.eliminarPlaylist(sel);
            refrescarListaPlaylists();
            modeloPlaylist.setRowCount(0);
        }
    }

    private void accionQuitarCancionPlaylist() {
        String selP = listaPlaylists.getSelectedValue();
        int filaC = tablaPlaylist.getSelectedRow();
        if (selP == null || filaC < 0) {
            return;
        }
        String nombreC = (String) modeloPlaylist.getValueAt(filaC, 0);
        gestor.eliminarCancion(selP, nombreC);
        mostrarCancionesPlaylist();
    }

    private void accionReproducirPlaylist() {
        String sel = listaPlaylists.getSelectedValue();
        if (sel == null) {
            return;
        }
        Playlist p = gestor.buscarPlaylist(sel);
        for (Cancion c : p.getCanciones()) {
            reproductor.encolar(c);
        }
        reproductor.setModo(
                reproductor.getModo() == ReproductorMusical.ModoReproduccion.CIRCULAR
                ? ReproductorMusical.ModoReproduccion.CIRCULAR
                : ReproductorMusical.ModoReproduccion.NORMAL);
        Cancion primera = p.getCanciones().getPrimero();
        if (primera != null) {
            reproductor.reproducir(primera);
            lblCancionActual.setText(primera.getNombre());
            lblArtista.setText(primera.getArtista());
        }
    }

    private void accionPlay() {
        if (reproductor.isPausado()) {
            reproductor.continuar();
        } else {
            accionReproducirSeleccionada();
        }
    }

    private void mostrarComparativaCarga() {
        areaLog.setText(biblioteca.getResumenComparativaCarga());
    }

    //  HELPERS DE UI
    private void refrescarTablaBiblioteca(List<Cancion> canciones) {
        modeloBiblioteca.setRowCount(0);
        Iterable<Cancion> fuente = (canciones != null)
                ? canciones
                : biblioteca.getListaBiblioteca();
        for (Cancion c : fuente) {
            modeloBiblioteca.addRow(new Object[]{
                c.getNombre(), c.getArtista(), c.getAlbum(), c.getGenero(),
                c.getDuracionFormateada(), c.getTamanoFormateado(), c.getAnio(), c.getRuta()
            });
        }
    }

    private void refrescarListaPlaylists() {
        modeloListaPlaylists.clear();
        for (Playlist p : gestor.getTodas()) {
            modeloListaPlaylists.addElement(p.getNombre());
        }
    }

    private void refrescarCancionReproducida() {
        Cancion actual = reproductor.getCancionActual();

        if (actual != null) {
            lblCancionActual.setText(actual.getNombre());
            lblArtista.setText(actual.getArtista() + " • " + actual.getAlbum());
        }
    }

    private void mostrarCancionesPlaylist() {
        modeloPlaylist.setRowCount(0);
        String sel = listaPlaylists.getSelectedValue();
        if (sel == null) {
            return;
        }
        Playlist p = gestor.buscarPlaylist(sel);
        if (p == null) {
            return;
        }
        for (Cancion c : p.getCanciones()) {
            modeloPlaylist.addRow(new Object[]{
                c.getNombre(), c.getArtista(), c.getAlbum(), c.getGenero(), c.getDuracionFormateada()
            });
        }
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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(VentanaPrincipal::new);
    }
}