package smartplayer.ui;

import smartplayer.model.Cancion;
import smartplayer.player.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Interfaz Grafica Principal de Smart Player.
 * Implementada con Java Swing / NetBeans.
 */
public class VentanaPrincipal extends JFrame {

    // ── Colores del tema ───────────────────────────────────────────────────
    private static final Color COLOR_FONDO      = new Color(18, 18, 18);
    private static final Color COLOR_PANEL      = new Color(30, 30, 30);
    private static final Color COLOR_ACENTO     = new Color(29, 185, 84);   // verde Spotify
    private static final Color COLOR_ACENTO2    = new Color(0, 120, 215);   // azul
    private static final Color COLOR_TEXTO      = new Color(255, 255, 255);
    private static final Color COLOR_TEXTO_SEC  = new Color(180, 180, 180);
    private static final Color COLOR_FILA_PAR   = new Color(40, 40, 40);
    private static final Color COLOR_FILA_IMPAR = new Color(35, 35, 35);
    private static final Font  FUENTE_TITULO    = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font  FUENTE_NORMAL    = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font  FUENTE_GRANDE    = new Font("Segoe UI", Font.BOLD, 22);

    // ── Lógica de negocio ──────────────────────────────────────────────────
    private final BibliotecaMusical  biblioteca  = new BibliotecaMusical();

    // ── Componentes UI ─────────────────────────────────────────────────────
    private JTabbedPane pestanas;
    private JTable      tablaBiblioteca;
    private DefaultTableModel modeloBiblioteca;
    private JTable      tablaPlaylist;
    private DefaultTableModel modeloPlaylist;
    private JLabel      lblCancionActual;
    private JLabel      lblArtista;
    private JLabel      lblTiempo;
    private JProgressBar barraProgreso;
    private JButton     btnPlay, btnPause, btnStop, btnNext, btnPrev;
    private JComboBox<String> comboModo;
    private JTextField  txtBuscar;
    private JComboBox<String> comboBuscarPor;
    private JTextArea   areaLog;
    private JList<String> listaPlaylists;
    private DefaultListModel<String> modeloListaPlaylists;
    private JLabel      lblEstABB, lblEstAVL;
    private JTextArea   areaEstadisticas;

    // ── Constructor ────────────────────────────────────────────────────────

    public VentanaPrincipal() {
        super("🎵 Smart Player - Sistema de Gestion Musical");
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
        UIManager.put("TabbedPane.background",      COLOR_PANEL);
        UIManager.put("TabbedPane.foreground",      COLOR_TEXTO);
        UIManager.put("TabbedPane.selected",        COLOR_ACENTO);
        UIManager.put("TabbedPane.contentAreaColor",COLOR_PANEL);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  CONSTRUCCIÓN DE UI
    // ══════════════════════════════════════════════════════════════════════

    private void inicializarUI() {
        setLayout(new BorderLayout(0, 0));

        // Panel superior con logo y controles globales
        add(crearPanelSuperior(),  BorderLayout.NORTH);

        // Panel central con pestañas
        pestanas = new JTabbedPane(JTabbedPane.TOP);
        pestanas.setBackground(COLOR_PANEL);
        pestanas.setForeground(COLOR_TEXTO);
        pestanas.setFont(FUENTE_TITULO);
        pestanas.addTab("📚 Biblioteca",    crearPestanaBiblioteca());
        pestanas.addTab("🎵 Playlists",     crearPestanaPlaylists());
        pestanas.addTab("🌲 Árboles",       crearPestanaArboles());
        pestanas.addTab("📊 Estadísticas",  crearPestanaEstadisticas());
        pestanas.addTab("🔐 Encriptación",  crearPestanaEncriptacion());
        add(pestanas, BorderLayout.CENTER);

        // Panel inferior con reproductor
        add(crearPanelReproductor(), BorderLayout.SOUTH);
    }

    // ── Panel Superior ─────────────────────────────────────────────────────

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

        panel.add(logo,          BorderLayout.WEST);
        panel.add(panelBotones,  BorderLayout.EAST);
        panel.add(new JSeparator(), BorderLayout.SOUTH);
        return panel;
    }

    // ── Pestaña Biblioteca ─────────────────────────────────────────────────

    private JPanel crearPestanaBiblioteca() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Barra de busqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelBusqueda.setBackground(COLOR_FONDO);

        comboBuscarPor = new JComboBox<>(new String[]{"Nombre","Artista","Album","Genero"});
        estilizar(comboBuscarPor);

        txtBuscar = new JTextField(25);
        estilizar(txtBuscar);

        JButton btnBuscar = crearBoton("🔍 Buscar", COLOR_ACENTO);

        JButton btnLimpiar = crearBoton("✖ Limpiar", new Color(180, 50, 50));

        panelBusqueda.add(new JLabel("Buscar por:") {{ setForeground(COLOR_TEXTO); }});
        panelBusqueda.add(comboBuscarPor);
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnLimpiar);

        // Tabla
        String[] columnas = {"Nombre","Artista","Album","Genero","Duracion","Tamaño","Año","Ruta"};
        modeloBiblioteca = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaBiblioteca = crearTabla(modeloBiblioteca);

        JScrollPane scroll = new JScrollPane(tablaBiblioteca);
        scroll.getViewport().setBackground(COLOR_FONDO);

        // Barra inferior con acciones rapidas
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        panelAcciones.setBackground(COLOR_FONDO);
        JButton btnAgregarCola = crearBoton("➕ Cola", new Color(100, 100, 200));
        JButton btnAgregarPlaylist = crearBoton("📋 Agregar a Playlist", new Color(100, 160, 100));
        panelAcciones.add(btnAgregarCola);
        panelAcciones.add(btnAgregarPlaylist);

        panel.add(panelBusqueda, BorderLayout.NORTH);
        panel.add(scroll,        BorderLayout.CENTER);
        panel.add(panelAcciones, BorderLayout.SOUTH);
        return panel;
    }

    // ── Pestaña Playlists ──────────────────────────────────────────────────

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

        JScrollPane scrollPlaylists = new JScrollPane(listaPlaylists);
        scrollPlaylists.setPreferredSize(new Dimension(220, 0));
        titularPanel(scrollPlaylists, "Playlists");

        // Botones CRUD playlists
        JPanel panelBotonesP = new JPanel(new GridLayout(4, 1, 4, 4));
        panelBotonesP.setBackground(COLOR_FONDO);
        JButton btnNueva   = crearBoton("➕ Nueva",    COLOR_ACENTO);
        JButton btnEliminar= crearBoton("🗑 Eliminar", new Color(180,50,50));
        JButton btnExportar= crearBoton("💾 Exportar", COLOR_ACENTO2);
        JButton btnImportar= crearBoton("📂 Importar", new Color(150,100,200));
        panelBotonesP.add(btnNueva); panelBotonesP.add(btnEliminar);
        panelBotonesP.add(btnExportar); panelBotonesP.add(btnImportar);

        JPanel panelIzq = new JPanel(new BorderLayout(4, 4));
        panelIzq.setBackground(COLOR_FONDO);
        panelIzq.add(scrollPlaylists, BorderLayout.CENTER);
        panelIzq.add(panelBotonesP,   BorderLayout.SOUTH);

        // Tabla de canciones de la playlist (derecha)
        String[] columnas = {"Nombre","Artista","Album","Genero","Duracion"};
        modeloPlaylist = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaPlaylist = crearTabla(modeloPlaylist);
        JScrollPane scrollCanciones = new JScrollPane(tablaPlaylist);
        scrollCanciones.getViewport().setBackground(COLOR_FONDO);
        titularPanel(scrollCanciones, "Canciones de la Playlist");

        JButton btnEliminarCancion = crearBoton("🗑 Quitar cancion", new Color(180,50,50));
        JButton btnReproducir      = crearBoton("▶ Reproducir Playlist", COLOR_ACENTO);
        JPanel panelSurP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        panelSurP.setBackground(COLOR_FONDO);
        panelSurP.add(btnEliminarCancion); panelSurP.add(btnReproducir);

        JPanel panelDer = new JPanel(new BorderLayout(4, 4));
        panelDer.setBackground(COLOR_FONDO);
        panelDer.add(scrollCanciones, BorderLayout.CENTER);
        panelDer.add(panelSurP,        BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzq, panelDer);
        split.setDividerLocation(240);
        split.setBackground(COLOR_FONDO);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    // ── Pestaña Arboles ────────────────────────────────────────────────────

    private JPanel crearPestanaArboles() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Area de texto para recorridos
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
            crearBoton("ABB InOrden",      COLOR_ACENTO2),
            crearBoton("ABB PreOrden",     COLOR_ACENTO2),
            crearBoton("ABB PostOrden",    COLOR_ACENTO2),
            crearBoton("AVL InOrden",      COLOR_ACENTO),
            crearBoton("AVL PreOrden",     COLOR_ACENTO),
            crearBoton("AVL PostOrden",    COLOR_ACENTO),
            crearBoton("📊 Comparar Carga",new Color(200,100,0)),
            crearBoton("🌲 Graphviz ABB",  new Color(100,0,200)),
            crearBoton("🌲 Graphviz AVL",  new Color(100,0,200)),
        };

        for (JButton b : botones) panelBotones.add(b);

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
        panelSur.add(panelEst,     BorderLayout.NORTH);
        panelSur.add(panelBotones, BorderLayout.CENTER);

        panel.add(scrollLog, BorderLayout.CENTER);
        panel.add(panelSur,  BorderLayout.SOUTH);
        return panel;
    }

    // ── Pestaña Estadísticas ───────────────────────────────────────────────

    private JPanel crearPestanaEstadisticas() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        areaEstadisticas = new JTextArea();
        areaEstadisticas.setBackground(COLOR_PANEL);
        areaEstadisticas.setForeground(COLOR_TEXTO);
        areaEstadisticas.setFont(new Font("Consolas", Font.PLAIN, 12));
        areaEstadisticas.setEditable(false);
        areaEstadisticas.setText("Carga una biblioteca para ver estadisticas...");
        JScrollPane scroll = new JScrollPane(areaEstadisticas);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelBotones.setBackground(COLOR_FONDO);
        JButton btnActualizar = crearBoton("🔄 Actualizar", COLOR_ACENTO);
        JButton btnDuplicados = crearBoton("🔍 Ver Duplicados", new Color(200,100,0));

        String[] tipos = {"Buscar (nombre):","Buscar (artista):","Buscar (album):"};
        JTextField txtMedir = new JTextField(20);
        estilizar(txtMedir);
        JButton btnMedir = crearBoton("⏱ Medir Busqueda", COLOR_ACENTO2);

        panelBotones.add(btnActualizar); panelBotones.add(btnDuplicados);
        panelBotones.add(new JLabel("Termino:") {{ setForeground(COLOR_TEXTO); }});
        panelBotones.add(txtMedir); panelBotones.add(btnMedir);

        panel.add(panelBotones, BorderLayout.NORTH);
        panel.add(scroll,       BorderLayout.CENTER);
        return panel;
    }

    // ── Pestaña Encriptación ───────────────────────────────────────────────

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
                new String[]{"InOrden","PreOrden","PostOrden"});
        estilizar(comboRecorrido);

        JTextField txtNombrePlaylist = new JTextField(18);
        txtNombrePlaylist.setToolTipText("Nombre de la playlist");
        estilizar(txtNombrePlaylist);

        JButton btnEnc = crearBoton("🔐 Encriptar y Exportar", new Color(180, 50, 220));
        JButton btnDec = crearBoton("🔓 Importar y Desencriptar", COLOR_ACENTO2);

        panelBotones.add(new JLabel("Playlist:") {{ setForeground(COLOR_TEXTO); }});
        panelBotones.add(txtNombrePlaylist);
        panelBotones.add(new JLabel("Recorrido:") {{ setForeground(COLOR_TEXTO); }});
        panelBotones.add(comboRecorrido);
        panelBotones.add(btnEnc);
        panelBotones.add(btnDec);

        panel.add(panelBotones,         BorderLayout.NORTH);
        panel.add(new JScrollPane(areaEnc), BorderLayout.CENTER);
        return panel;
    }

    // ── Panel Reproductor ─────────────────────────────────────────────────

    private JPanel crearPanelReproductor() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        panel.setPreferredSize(new Dimension(0, 100));

        // Info de la canción
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
        btnPrev  = crearBotonIcono("⏮");
        btnPlay  = crearBotonIcono("▶");
        btnPause = crearBotonIcono("⏸");
        btnStop  = crearBotonIcono("⏹");
        btnNext  = crearBotonIcono("⏭");
        
        panelBotones.add(btnPrev); panelBotones.add(btnPlay);
        panelBotones.add(btnPause); panelBotones.add(btnStop); panelBotones.add(btnNext);

        barraProgreso = new JProgressBar(0, 100);
        barraProgreso.setForeground(COLOR_ACENTO);
        barraProgreso.setBackground(new Color(50, 50, 50));
        barraProgreso.setStringPainted(false);

        panelControles.add(panelBotones, BorderLayout.NORTH);
        panelControles.add(barraProgreso, BorderLayout.SOUTH);

        // Controles de modo
        JPanel panelModo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        panelModo.setBackground(COLOR_PANEL);
        comboModo = new JComboBox<>(new String[]{"▶ Normal","🔀 Aleatorio","🔁 Circular"});
        estilizar(comboModo);
        lblTiempo = new JLabel("00:00");
        lblTiempo.setForeground(COLOR_TEXTO_SEC);
        panelModo.add(new JLabel("Modo:") {{ setForeground(COLOR_TEXTO); }});
        panelModo.add(comboModo);
        panelModo.add(lblTiempo);

        panel.add(panelInfo,     BorderLayout.WEST);
        panel.add(panelControles,BorderLayout.CENTER);
        panel.add(panelModo,     BorderLayout.EAST);
        return panel;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  ACCIONES
    // ══════════════════════════════════════════════════════════════════════

    private void accionCargarBiblioteca() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogTitle("Selecciona la carpeta de música");
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        String ruta = fc.getSelectedFile().getAbsolutePath();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<List<Cancion>, Void> worker = new SwingWorker<>() {
            @Override protected List<Cancion> doInBackground() {
                return biblioteca.cargarDesde(ruta);
            }
            @Override protected void done() {
                try {
                    List<Cancion> canciones = get();
                    refrescarTablaBiblioteca(canciones);
                    JOptionPane.showMessageDialog(VentanaPrincipal.this,
                        "✅ Cargadas " + canciones.size() + " canciones.\n",
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

    // ══════════════════════════════════════════════════════════════════════
    //  HELPERS DE UI
    // ══════════════════════════════════════════════════════════════════════

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

    private JTable crearTabla(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
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
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(color.brighter()); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(color); }
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
            @Override public void mouseEntered(MouseEvent e) { btn.setForeground(COLOR_ACENTO); }
            @Override public void mouseExited(MouseEvent e)  { btn.setForeground(COLOR_TEXTO); }
        });
        return btn;
    }

    private void estilizar(JComponent c) {
        c.setBackground(new Color(50, 50, 50));
        c.setForeground(COLOR_TEXTO);
        c.setFont(FUENTE_NORMAL);
        if (c instanceof JTextField) ((JTextField)c).setCaretColor(COLOR_TEXTO);
    }

    private void titularPanel(JComponent panel, String titulo) {
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_ACENTO, 1),
            titulo, TitledBorder.LEFT, TitledBorder.TOP,
            FUENTE_TITULO, COLOR_ACENTO);
        panel.setBorder(border);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  MAIN
    // ══════════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(VentanaPrincipal::new);
    }
}