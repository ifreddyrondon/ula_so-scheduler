package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import model.Proceso;

/**
 * 
 * @author Freddy Rondon
 * 
 *         Un panel de proceso es un alto y delgado rectangulo (115x100) que
 *         consta de un medidor de la CPU y un indicador de prioridad. El burst
 *         panel muestra si un proceso es llegado o activo. También se muestra
 *         una barra de progreso relacionada al valor inicial del burst del
 *         proceso y su tiempo burst restante. La prioridad en la parte inferior
 *         muestra el peso de un proceso que se da en algunos algoritmos de
 *         planificación.
 * 
 */

@SuppressWarnings("serial")
class PanelProceso extends JPanel {

	Proceso proceso;

	/**
	 * Ancho del panel de proceso.
	 */
	static final int PPANCHO = 12;

	/**
	 * Alto del panel de proceso.
	 */
	static final int PPALTO = 150;

	/**
	 * La altura a la que desea los medidores dibujados. Hago una proporción de
	 * 1:1 con mi ráfaga máxima.
	 */
	static final int BARALTURA = 135;

	/**
	 * Algunos colores para dibujar.
	 */
	Color burstColor, initBurstColor = Color.darkGray, unarrivedColor,
			lblColor;

	/**
	 * EL label para mostrar el PID.
	 */
	JLabel pidLbl;
	
	

	/**
	 * Quieres ver los procesos unarrived?
	 */
	static boolean showHidden = true;

	/** Constructor por defecto. Genera su propio proceso. */
	PanelProceso() {
		proceso = new Proceso();
		initPanel();
	}

	/**
	 * Constructor parametrico.
	 * 
	 * @param p
	 *              p sobre el que se basa este panel.
	 */
	PanelProceso(Proceso p) {
		proceso = p;
		initPanel();
	}

	/**
	 * Construye el panel
	 */
	void initPanel() {
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setLayout(new BorderLayout());
		
		pidLbl = new JLabel("" + (int) proceso.getPID());
		pidLbl.setFont(new Font("Helvetica", Font.BOLD, 8));
		pidLbl.setToolTipText(proceso.printString());
		pidLbl.setHorizontalAlignment(SwingConstants.CENTER);

		pidLbl.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				JOptionPane.showMessageDialog(null, proceso.printString(), "Informacion del Proceso", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		
		setSize(PPANCHO, PPALTO);
		setBackground(Color.white);
		setOpaque(true);
		add(pidLbl, "South");
	}

	/**
	 * Si el proceso termino removerlo. De lo contrario actualizar su medidor de burst.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (proceso.isFinalizado() == true) {
			setVisible(false);
		} else {
			DrawBursts(g);
		}
	}

	/**
	 * Dibujar el panel burst. Dibujar el burst restante sobre el burst acabado.
	 * Dibujar el proceso activo en un color brillante.
	 */
	void DrawBursts(Graphics g) {
		int initBurstHeight = 0, burstHeight = 0;
		int width = 0;

		initBurstHeight = (int) proceso.getTiempoInitBurst();
		burstHeight = (int) proceso.getTiempoBurst();
		width = (int) PPANCHO - 2; // fuera por un error in swing?

		lblColor = (proceso.isLlegado() ? Color.black
				: (showHidden ? Color.lightGray : Color.white));

		initBurstColor = (proceso.isLlegado() ? Color.lightGray
				: Color.lightGray);

		burstColor = (proceso.isLlegado()) ? (proceso.isActivo() == true ? Color.red
				: Color.cyan)
				: (showHidden ? Color.darkGray : Color.white);

		pidLbl.setForeground(lblColor);
		pidLbl.setBackground(proceso.isActivo() ? Color.red : Color.white);

		if (proceso.isLlegado() && !proceso.bloqueado) {
			g.setColor(initBurstColor);
			g.drawRect(0, BARALTURA - initBurstHeight, width,
					initBurstHeight);
			g.setColor(burstColor);
			g.fillRect(1, BARALTURA - burstHeight + 1, width - 1,
					burstHeight - 1);
		} else if(proceso.bloqueado){
			g.setColor(Color.green);
			g.drawRect(0, BARALTURA - initBurstHeight, width,
					initBurstHeight);
			g.setColor(Color.green);
			g.fillRect(1, BARALTURA - burstHeight + 1, width - 1,
					burstHeight - 1);
		} else if (showHidden) {
			g.setColor(initBurstColor);
			g.drawRect(0, BARALTURA - initBurstHeight, width,
					initBurstHeight);
		}

	}

	/**
	 * Obtener el valor de proceso.
	 * 
	 * @return valor de proceso.
	 */
	public Proceso getProceso() {
		return proceso;
	}

	/**
	 * Ajustar el valor de proceso.
	 * 
	 * @param v
	 *              Valor a asignar a proceso.
	 */
	public void setProceso(Proceso v) {
		this.proceso = v;
	}

	public Dimension getPreferredSize() {
		return (new Dimension(PPANCHO, PPALTO));
	}

	/**
	 * Obtener el valor de showHidden.
	 * 
	 * @return valor de showHidden.
	 */
	public static boolean getShowHidden() {
		return showHidden;
	}

	/**
	 * Ajustar el valor de showHidden.
	 * 
	 * @param v
	 *              Valor a asignar a showHidden.
	 */
	public static void setShowHidden(boolean v) {
		showHidden = v;
	}

}
