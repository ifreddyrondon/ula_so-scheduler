package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import model.PlanificadorCPU;
import model.Proceso;

import utileria.BetterFileFilter;

/**
 * 
 *         PanelPlanificadorCPU es un JFrame que contiene y representa un objeto
 *         PlanificadorCPU. Se puede cargar conjuntos de datos aleatorios y
 *         predeterminados, ejecutar simulaciones desde una interfaz gráfica de
 *         usuario y ver una animación del proceso gracias a una señal de tiempo
 *         de respuesta a través de la interfaz ActionListener.
 */
@SuppressWarnings("serial")
public class PanelPlanificadorCPU extends JFrame implements ActionListener {

	PlanificadorCPU cpu;

	JCheckBox comenzarSimulacion;
	ImageIcon playPic, pausePic, pressPic;

	JMenuBar menuBar;
	JMenu fileMenu, algoritmoMenu, opcionesMenu, velocidadMenu;
	JMenuItem nuevoMenuItem, configurarMenuItem, cargarMenuItem,
			resetMenuItem, guardarMenuItem, salirMenuItem;
	JRadioButtonMenuItem fps1, fps10, fps20, fps30, fps40, fps50, fps60,
			fps70, fps80, fps90, fps100, fps200, fps500, fps1000;
	JRadioButtonMenuItem fcfsMenuItem, srtMenuItem, rrMenuItem, psjfMenuItem;
	JCheckBoxMenuItem mostrarOcultosMenuItem;

	JLabel statusBar, algolLbl;
	PanelEstadisticas waitSP, turnSP, responseSP;
	PanelReloj cpuTimePanel;

	JFileChooser openFileChooser;
	BetterFileFilter openFilter, saveFilter;

	public Vector<Double> vectorBurst;
	public Vector<Double> vectorLlegada;
	public Vector<Double> vectorBloqueado;

	int frameNumber = -1;
	int fps = 30;
	Timer temporizador;
	boolean pausa = true;

	JPanel contentPane, panelColaProcesos;

	String fileName = "";

	/**
	 * Constructor por defecto, construye y muestra un objeto aleatoria de
	 * PlanificadorCPU.
	 */
	public PanelPlanificadorCPU() {

		setBackground(Color.WHITE);

		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		setSize(screenWidth / 2, screenHeight / 2);
		setLocation(screenWidth / 4, screenHeight / 4);
		setLocationRelativeTo(null);

		// configurando animacion y simulacion
		cpu = new PlanificadorCPU();
		int retrasar = (fps > 0) ? (1000 / fps) : 100;
		temporizador = new Timer(retrasar, this);
		temporizador.setCoalesce(false); // no combine eventos en cola
		temporizador.setInitialDelay(0);
		cpu.setFps(retrasar);

		// configuracion de panel
		setTitle("Simulador de Planificacion de la CPU");
		setSize(869, 390);

		construirBotonesDeSimulacion();
		panelColaProcesos = new JPanel();
		JScrollPane scrollerPanelColaProcesos = new JScrollPane(panelColaProcesos);  
		scrollerPanelColaProcesos.setBorder(null);
		
		construirMenus();
		buildStatusPanels();
		llenarPanelColaProcesos();
		construirFileChooser();
		actualizaSalidaDeDatosPorInterfaz();

		Container masterPanel = getContentPane();
		masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.Y_AXIS));

		JPanel topRow = new JPanel();
		topRow.setLayout(new BorderLayout());
		topRow.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel middleRow = new JPanel();
		middleRow.setLayout(new FlowLayout(FlowLayout.CENTER));
		middleRow.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));

		JPanel bottomRow = new JPanel();
		bottomRow.setLayout(new BoxLayout(bottomRow, BoxLayout.Y_AXIS));
		bottomRow.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));

		topRow.add(scrollerPanelColaProcesos, "North");

		middleRow.add(cpuTimePanel);
		middleRow.add(responseSP);
		middleRow.add(turnSP);
		middleRow.add(waitSP);

		bottomRow.add(middleRow, "Center");
		bottomRow.add(comenzarSimulacion, "South");
		
		// Leyendas
		JPanel panelLeyenda = new JPanel();
		panelLeyenda.setLayout(new FlowLayout(FlowLayout.LEADING, 10,0));
		
		JLabel leyenda = new JLabel("Leyenda: ");
		leyenda.setFont(new Font("Helvetica", Font.BOLD, 13));
		JLabel leyendaBloqueado = new JLabel("Bloqueado");
		leyendaBloqueado.setFont(new Font("Helvetica", 12, 12));
		leyendaBloqueado.setForeground(Color.GREEN);
		JLabel leyendaColaListos = new JLabel("Cola listos");
		leyendaColaListos.setFont(new Font("Helvetica", 12, 12));
		leyendaColaListos.setForeground(Color.cyan);
		JLabel leyendaEjecutando = new JLabel("Ejecutando");
		leyendaEjecutando.setFont(new Font("Helvetica", 12, 12));
		leyendaEjecutando.setForeground(Color.RED);
		
		panelLeyenda.add(leyenda);
		panelLeyenda.add(leyendaBloqueado);
		panelLeyenda.add(leyendaColaListos);
		panelLeyenda.add(leyendaEjecutando);
		
		bottomRow.add(panelLeyenda, "South");
		
		masterPanel.add(topRow);
		masterPanel.add(bottomRow);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		setVisible(true);
	}

	/**
	 * Remueve todos los PanelProceso que contiene la representacion grafica
	 * de la cola de listos
	 */
	public void vaciarPanelColaProcesos() {
		panelColaProcesos.removeAll();
	}

	/**
	 * Redisplay all hidden ProcessPanels
	 */
	public void resetPanelColaProcesos() {
		PanelProceso p;

		int num = panelColaProcesos.getComponentCount();
		for (int i = 0; i < num; i++) {
			p = (PanelProceso) panelColaProcesos.getComponent(i);
			p.setVisible(true);
		}
	}

	/**
	 * Muestra los procesos del planificador en una cola
	 */
	public void llenarPanelColaProcesos() {
		Vector<Proceso> v = cpu.getTodosProcesos();
		panelColaProcesos.setBackground(Color.white);
		panelColaProcesos.setOpaque(true);
		FlowLayout flay = new FlowLayout(FlowLayout.LEFT);
		panelColaProcesos.setLayout(flay);
		for (int i = 0; i < v.size(); i++) {
			PanelProceso p = new PanelProceso((Proceso) v.get(i));
			panelColaProcesos.add(p, "Left");
		}
		panelColaProcesos.revalidate();
	}

	/**
	 * Setup the panels used to display status. CPU time, wait time, response
	 * time and turnaround time
	 */
	void buildStatusPanels() {
		statusBar = new JLabel("");
		statusBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		statusBar.setAlignmentX(Component.LEFT_ALIGNMENT);

		cpuTimePanel = new PanelReloj("CPU");
		cpuTimePanel.setEstadisticas(0, 0, 0);
		cpuTimePanel.setToolTipText("El tiempo \"real\" en el reloj del CPU");

		waitSP = new PanelEstadisticas("Tiempo de espera");
		waitSP.setEstadisticas(0, 0, 0, 0);
		waitSP.setToolTipText("El tiempo de espera es la cantidad total de tiempo que "
				+ "un proceso en la cola de listos espera para ser ejecutado");

		turnSP = new PanelEstadisticas("Tiempo turnaround");
		turnSP.setEstadisticas(0, 0, 0, 0);
		turnSP.setToolTipText("Tiempo transcurrido entre que se lanza un proceso y termina");

		responseSP = new PanelEstadisticas("Tiempo de respuesta");
		responseSP.setEstadisticas(0, 0, 0, 0);
		responseSP.setToolTipText("Es el intervalo de tiempo desde que un proceso"
				+ " es cargado en la cola de listos hasta que brinda su primera respuesta");

		algolLbl = new JLabel("FCFS", JLabel.CENTER);
		algolLbl.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 20));

	}

	/**
	 * Metodo implementado de la interface ActionListener Comprueba el origen
	 * del evento y responde
	 */
	public void actionPerformed(ActionEvent e) {

		// Comenzar la Simulacion boton Start
		if (e.getSource() == comenzarSimulacion) {
			if (pausa == false) {
				pausa = true;
				pararAnimacion();
				cpu.setPausada(true);
			} else {
				cpu.setPausada(false);
				pausa = false;
				comenzarAnimacion();
			}

			// Temporizador
		} else if (e.getSource() == temporizador) {
			if (cpu.proximoCiclo() == true) {
				actualizaSalidaDeDatosPorInterfaz();
			} else {
				pararAnimacion();
				comenzarSimulacion.setSelected(false);
			}
			repaint();

			// Algoritmos a escojer
		} else if (e.getSource() == fcfsMenuItem) {
			cpu.setAlgoritmo(PlanificadorCPU.FCFS);
			algolLbl.setText("FCFS");
		} else if (e.getSource() == rrMenuItem) {
			cpu.setAlgoritmo(PlanificadorCPU.ROUNDROBIN);
			algolLbl.setText("RR");
		} else if (e.getSource() == srtMenuItem) {
			cpu.setAlgoritmo(PlanificadorCPU.SRT);
			algolLbl.setText("SRT");
		} else if (e.getSource() == psjfMenuItem) {
			cpu.setAlgoritmo(PlanificadorCPU.PSJF);
			algolLbl.setText("PSJF");

			// Mostrar o ocultar panel de procesos
		} else if (e.getSource() == mostrarOcultosMenuItem) {
			PanelProceso.setShowHidden(mostrarOcultosMenuItem.getState());
			repaint();

			// Crear nuevos procesos Aleatorios
		} else if (e.getSource() == nuevoMenuItem) {
			cpu.restaurar();
			resetPanelColaProcesos();

			cpu.construirColaRamdon();
			vaciarPanelColaProcesos();
			llenarPanelColaProcesos();
			actualizaSalidaDeDatosPorInterfaz();
			repaint();

			// Configurar procesos
		} else if (e.getSource() == configurarMenuItem) {
			new PanelConfiguracionProcesos(PanelPlanificadorCPU.this);

			try {
				Vector<Proceso> vecProcesos = new Vector<Proceso>();
				for (int i = 0; i < vectorBurst.size(); i++) {
					vecProcesos.add(new Proceso(vectorBurst.get(i).longValue(),
							vectorLlegada.get(i).longValue(), vectorBloqueado
									.get(i).longValue()));
				}
				cpu.restaurar();
				resetPanelColaProcesos();

				cpu = new PlanificadorCPU(vecProcesos);
				vaciarPanelColaProcesos();
				llenarPanelColaProcesos();
				actualizaSalidaDeDatosPorInterfaz();
				repaint();

			} catch (Exception e2) {
				// TODO: handle exception
			}
			
			// Cargar procesos de un archivo .dat
		} else if (e.getSource() == cargarMenuItem) {
			openFileChooser.resetChoosableFileFilters();
			openFileChooser.setFileFilter(openFilter);
			int returnVal = openFileChooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File fileName = openFileChooser.getSelectedFile();
				cpu = new PlanificadorCPU(fileName);
				vaciarPanelColaProcesos();
				llenarPanelColaProcesos();
				actualizaSalidaDeDatosPorInterfaz();
				repaint();
			}

			// Guardar Estadisticas en un archivo .csv
		} else if (e.getSource() == guardarMenuItem) {
			openFileChooser.resetChoosableFileFilters();
			openFileChooser.setFileFilter(saveFilter);
			int returnVal = openFileChooser.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {

					String fileName = openFileChooser
							.getSelectedFile().toString();
					int fnLen = fileName.length();
					if (!((fileName.substring(fnLen - 4))
							.equals(".csv")))
						fileName += ".csv";

					File file = new File(fileName);

					PrintWriter ostream = new PrintWriter(
							new BufferedWriter(new FileWriter(
									file)));
					cpu.printCSV(ostream);
					ostream.flush();
					ostream.close();
				} catch (IOException saveIOE) {
				}
			}

		}

		// Reiniciar la Simulacions
		else if (e.getSource() == resetMenuItem) {
			cpu.restaurar();
			resetPanelColaProcesos();
			actualizaSalidaDeDatosPorInterfaz();
			repaint();

			// Salir del programa
		} else if (e.getSource() == salirMenuItem) {
			pararAnimacion();
			dispose();
			System.exit(0);

			// Ajustar la velocidad de simulacion
		} else if (e.getSource() == fps1) {
			ajustarFPS(1);
		} else if (e.getSource() == fps10) {
			ajustarFPS(10);
		} else if (e.getSource() == fps20) {
			ajustarFPS(20);
		} else if (e.getSource() == fps30) {
			ajustarFPS(30);
		} else if (e.getSource() == fps40) {
			ajustarFPS(40);
		} else if (e.getSource() == fps50) {
			ajustarFPS(50);
		} else if (e.getSource() == fps60) {
			ajustarFPS(60);
		} else if (e.getSource() == fps70) {
			ajustarFPS(70);
		} else if (e.getSource() == fps80) {
			ajustarFPS(80);
		} else if (e.getSource() == fps90) {
			ajustarFPS(90);
		} else if (e.getSource() == fps100) {
			ajustarFPS(100);
		} else if (e.getSource() == fps200) {
			ajustarFPS(200);
		} else if (e.getSource() == fps500) {
			ajustarFPS(500);
		} else if (e.getSource() == fps1000) {
			ajustarFPS(1000);
		}

	}

	/**
	 * Puede ser invocado desde cualquier hilo
	 * */
	public synchronized void comenzarAnimacion() {
		if (pausa) {
			// No hacer nada.
		} else {
			if (!temporizador.isRunning()) {
				temporizador.start();
			}
		}
	}

	/**
	 * Puede ser invocado desde cualquier hilo
	 * */
	public synchronized void pararAnimacion() {
		// Detiene el hilo de animacion
		if (temporizador.isRunning()) {
			temporizador.stop();
		}
	}

	/**
	 * Actualiza los estados y estadisticas del planificador
	 * */
	void actualizaSalidaDeDatosPorInterfaz() {
		cpuTimePanel.setEstadisticas((int) cpu.getTiempoActual(),
				(int) cpu.getTiempoIdle(), (int) cpu.getTiempoOcupado());
		waitSP.setEstadisticas(cpu.getMinWait(), cpu.getMeanWait(),
				cpu.getMaxWait(), cpu.getStdDevWait());
		responseSP.setEstadisticas(cpu.getMinResponse(),
				cpu.getMeanResponse(), cpu.getMaxResponse(),
				cpu.getStdDevResponse());
		turnSP.setEstadisticas(cpu.getMinTurn(), cpu.getMeanTurn(),
				cpu.getMaxTurn(), cpu.getStdDevTurn());
	}

	/**
	 * Configura el FileChooser
	 * */
	void construirFileChooser() {
		openFileChooser = new JFileChooser(".");

		openFilter = new BetterFileFilter("");
		openFilter.addExtension("dat");
		openFilter.setDescription("Datos de procesos");

		saveFilter = new BetterFileFilter("");
		saveFilter.addExtension("csv");
		saveFilter.setDescription("cvs (separados por ,)");

	}

	/** Construye todos los Menus para la aplicacions */
	void construirMenus() {

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		fileMenu = new JMenu("Archivo");

		nuevoMenuItem = new JMenuItem("Nuevos procesos (aleatorios)");
		nuevoMenuItem.addActionListener(this);
		fileMenu.add(nuevoMenuItem);

		configurarMenuItem = new JMenuItem("Configuracion de los procesos");
		configurarMenuItem.addActionListener(this);
		fileMenu.add(configurarMenuItem);

		cargarMenuItem = new JMenuItem("Cargar archivo de procesos...");
		cargarMenuItem.addActionListener(this);
		fileMenu.add(cargarMenuItem);

		guardarMenuItem = new JMenuItem("Guardar estadisticas ...");
		guardarMenuItem.addActionListener(this);
		fileMenu.add(guardarMenuItem);

		resetMenuItem = new JMenuItem("Resetear");
		resetMenuItem.addActionListener(this);
		fileMenu.add(resetMenuItem);

		salirMenuItem = new JMenuItem("Salir");
		salirMenuItem.addActionListener(this);
		fileMenu.add(salirMenuItem);

		menuBar.add(fileMenu);

		// Menu de opciones
		opcionesMenu = new JMenu("Opciones");
		// SubMenu de Algoritmos
		algoritmoMenu = new JMenu("Algoritmos");

		ButtonGroup grupoAlgoritmos = new ButtonGroup();

		fcfsMenuItem = new JRadioButtonMenuItem("FIFO (No expropiable)");
		fcfsMenuItem.setSelected(true);
		fcfsMenuItem.setToolTipText("Algoritmo Primero en llegar Primero en Salir");
		grupoAlgoritmos.add(fcfsMenuItem);
		fcfsMenuItem.addActionListener(this);
		algoritmoMenu.add(fcfsMenuItem);

		srtMenuItem = new JRadioButtonMenuItem(
				"Menos tiempo restante primero (No expropiable)");
		srtMenuItem.setToolTipText("Algoritmo SRT");
		grupoAlgoritmos.add(srtMenuItem);
		srtMenuItem.addActionListener(this);
		algoritmoMenu.add(srtMenuItem);

		rrMenuItem = new JRadioButtonMenuItem("Round Robin (Expropiable)");
		rrMenuItem.setToolTipText("Algortimo Round Robin");
		grupoAlgoritmos.add(rrMenuItem);
		rrMenuItem.addActionListener(this);
		algoritmoMenu.add(rrMenuItem);

		psjfMenuItem = new JRadioButtonMenuItem(
				"Preemptive Shortest Job First (PSJF)");
		psjfMenuItem.setToolTipText("Algoritmo Preemptive Shortest Job First (PSJF Expropiable)");
		psjfMenuItem.addActionListener(this);
		grupoAlgoritmos.add(psjfMenuItem);
		algoritmoMenu.add(psjfMenuItem);

		opcionesMenu.add(algoritmoMenu);

		// SubMenu de Velocidad de simulacions
		velocidadMenu = new JMenu("Velocidad de simulacion");

		velocidadMenu
				.setToolTipText("Ajuste la velocidad de cpu reloj animación");
		ButtonGroup bg = new ButtonGroup();

		fps1 = new JRadioButtonMenuItem("1 fps");
		fps1.setToolTipText("Ajuste la velocidad de cpu reloj animación");
		bg.add(fps1);
		fps1.addActionListener(this);
		velocidadMenu.add(fps1);

		fps10 = new JRadioButtonMenuItem("10 fps");
		fps10.setToolTipText("Ajuste la velocidad de cpu reloj animación");
		bg.add(fps10);
		fps10.addActionListener(this);
		velocidadMenu.add(fps10);

		fps20 = new JRadioButtonMenuItem("20 fps");
		fps20.setToolTipText("Ajuste la velocidad de cpu reloj animación");
		bg.add(fps20);
		fps20.addActionListener(this);
		velocidadMenu.add(fps20);

		fps30 = new JRadioButtonMenuItem("30 fps", true);
		fps30.setToolTipText("Ajuste la velocidad de cpu reloj animación");
		bg.add(fps30);
		fps30.addActionListener(this);
		velocidadMenu.add(fps30);

		fps40 = new JRadioButtonMenuItem("40 fps");
		fps40.setToolTipText("Ajuste la velocidad de cpu reloj animación");
		bg.add(fps40);
		fps40.addActionListener(this);
		velocidadMenu.add(fps40);

		fps50 = new JRadioButtonMenuItem("50 fps");
		fps50.setToolTipText("Ajuste la velocidad de cpu reloj animación");
		bg.add(fps50);
		fps50.addActionListener(this);
		velocidadMenu.add(fps50);

		fps60 = new JRadioButtonMenuItem("60 fps");
		fps60.setToolTipText("Ajuste la velocidad de cpu reloj animación");
		bg.add(fps60);
		fps60.addActionListener(this);
		velocidadMenu.add(fps60);

		fps70 = new JRadioButtonMenuItem("70 fps");
		fps70.setToolTipText("Ajuste la velocidad de cpu reloj animación");
		bg.add(fps70);
		fps70.addActionListener(this);
		velocidadMenu.add(fps70);

		fps80 = new JRadioButtonMenuItem("80 fps");
		fps80.setToolTipText("Ajuste la velocidad de cpu reloj animación");
		bg.add(fps80);
		fps80.addActionListener(this);

		fps90 = new JRadioButtonMenuItem("90 fps");
		fps90.setToolTipText("Ajuste la velocidad de cpu reloj animación");
		bg.add(fps90);
		fps90.addActionListener(this);
		velocidadMenu.add(fps90);

		fps100 = new JRadioButtonMenuItem("100 fps");
		fps100.setToolTipText("Ajuste la velocidad de cpu reloj animación");
		bg.add(fps100);
		fps100.addActionListener(this);
		velocidadMenu.add(fps100);

		fps200 = new JRadioButtonMenuItem("200 fps");
		fps200.setToolTipText("Ajuste la velocidad de cpu reloj animación");
		bg.add(fps200);
		fps200.addActionListener(this);
		velocidadMenu.add(fps200);

		fps500 = new JRadioButtonMenuItem("500 fps");
		fps500.setToolTipText("Ajuste la velocidad de cpu reloj animación");
		bg.add(fps500);
		fps500.addActionListener(this);
		velocidadMenu.add(fps500);

		fps1000 = new JRadioButtonMenuItem("1000 fps");
		fps1000.setToolTipText("Ajuste la velocidad de cpu reloj animación");
		bg.add(fps1000);
		fps1000.addActionListener(this);
		velocidadMenu.add(fps1000);

		opcionesMenu.add(velocidadMenu);
		opcionesMenu.addSeparator();

		// Opcion para mostrar y ocultar los procesos
		mostrarOcultosMenuItem = new JCheckBoxMenuItem(
				"Mostrar/Ocultar Procesos", false);
		mostrarOcultosMenuItem.addActionListener(this);
		opcionesMenu.add(mostrarOcultosMenuItem);

		menuBar.add(opcionesMenu);
	}

	/**
	 * Ajuste de FPS para la animación. Es limitado por la velocidad del
	 * hardware.
	 */
	void ajustarFPS(int delay) {
		boolean state = pausa;
		pararAnimacion();
		delay = (delay > 0) ? (1000 / delay) : 100;
		temporizador.setDelay(delay);
		if (!state)
			comenzarAnimacion();
		cpu.setFps(delay);
	}

	/**
	 * Construir los botones para la simulacion
	 */
	void construirBotonesDeSimulacion() {
		// Imagen Play
		URL iconURL = ClassLoader.getSystemResource("imagenes/play.png");
		if (iconURL != null) {
			playPic = new ImageIcon(iconURL, "play");
		}
		// Imagen Pause
		iconURL = ClassLoader.getSystemResource("imagenes/pause.png");
		if (iconURL != null) {
			pausePic = new ImageIcon(iconURL, "pause");
		}
		// Imagen cuando el boton esta precionado
		iconURL = ClassLoader.getSystemResource("imagenes/playing.png");
		if (iconURL != null) {
			pressPic = new ImageIcon(iconURL, "playing");
		}

		comenzarSimulacion = new JCheckBox(playPic, false);
		comenzarSimulacion.addActionListener(this);
		comenzarSimulacion.setSelectedIcon(pausePic);
		comenzarSimulacion.setPressedIcon(pressPic);
		comenzarSimulacion.setBorder(new EmptyBorder(0, 0, 0, 0));
		comenzarSimulacion.setToolTipText("Play/Pause");
		comenzarSimulacion.setAlignmentX(Component.LEFT_ALIGNMENT);
	}

}
