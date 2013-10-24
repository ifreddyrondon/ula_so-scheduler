package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * @author Freddy Rondon
 * 
 *         Un simple panel para mostrar tiempo/idle/burst para cualquier
 *         cuantificador. Similar a PanelEstadisticas
 */

@SuppressWarnings("serial")
class PanelReloj extends JPanel {

	final static int width = 162, height = 80;

	JLabel tiempoLabel, idleLabel, ocuapadoLabel, tiempo, idle, ocuapado;

	PanelReloj(String title) {
		Border bGreyLine = BorderFactory.createLineBorder(Color.WHITE,
				1);
		TitledBorder tBorder = BorderFactory.createTitledBorder(bGreyLine,
				title, TitledBorder.LEFT, TitledBorder.TOP, new Font(
						"Helvetica Neue", Font.BOLD, 14));
		setBorder(tBorder);
		setBackground(Color.WHITE);
		setLayout(new GridLayout(0, 2));

		tiempoLabel = new JLabel("Tiempo");
		tiempoLabel.setFont(new Font("Helvetica Neue", 12, 11));
		tiempo = new JLabel("" + 0);
		tiempo.setFont(new Font("Helvetica Neue", Font.BOLD, 13));
		ocuapadoLabel = new JLabel("Ocupado");
		ocuapadoLabel.setFont(new Font("Helvetica Neue", 12, 11));
		ocuapado = new JLabel("" + 0);
		ocuapado.setFont(new Font("Helvetica Neue", Font.BOLD, 13));
		idleLabel = new JLabel("Idle");
		idleLabel.setFont(new Font("Helvetica Neue", 12, 11));
		idle = new JLabel("" + 0);
		idle.setFont(new Font("Helvetica Neue", Font.BOLD, 13));

		add(tiempoLabel);
		add(tiempo);
		add(idleLabel);
		add(idle);
		add(ocuapadoLabel);
		add(ocuapado);

		setSize(width, height);
		setMinimumSize(new Dimension(width, height));
	}

	/**
	 * Actualizar numeros mostrados
	 */
	public void setEstadisticas(int _tiempo, int _idle, int _ocupado) {
		tiempo.setText(Integer.toString(_tiempo));
		idle.setText(Integer.toString(_idle));
		ocuapado.setText(Integer.toString(_ocupado));
	}

	public Dimension getMinimumSize() {
		return new Dimension(width, height);
	}

	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}

}
