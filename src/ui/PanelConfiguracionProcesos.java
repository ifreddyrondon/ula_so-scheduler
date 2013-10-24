package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;

import utileria.StdRandom;

@SuppressWarnings("serial")
public class PanelConfiguracionProcesos extends JDialog implements
		ActionListener {

	private JPanel contentPane;
	private JTextField txtFieldNumProcesos;
	private JTextField txtFieldSeed;
	private JLabel lblTiempoBurst;
	private JComboBox comboBurst;
	private JComboBox comboBloqueado;
	private JComboBox comboLlegada;
	private JTextField txtFieldBurstCamp1;
	private JTextField txtFieldBurstCamp2;
	private JTextField txtFieldBloqueadoCamp1;
	private JTextField txtFieldBloqueadoCamp2;
	private JTextField txtFieldLlegadaCamp1;
	private JTextField txtFieldLlegadaCamp2;
	private JButton btnConfigurar;

	private int numeroProcesos;
	private long seed;
	private PanelPlanificadorCPU panelPlanificadorCPU;

	public PanelConfiguracionProcesos(
			PanelPlanificadorCPU _panelPlanificadorCPU) {

		super(_panelPlanificadorCPU, true);
		panelPlanificadorCPU = _panelPlanificadorCPU;

		setBounds(100, 100, 450, 295);
		setBackground(Color.white);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNumeroDeProcesos = new JLabel("Numero de procesos");
		lblNumeroDeProcesos.setFont(new Font("Helvetica Neue", 12, 12));
		lblNumeroDeProcesos.setBounds(26, 17, 136, 16);
		contentPane.add(lblNumeroDeProcesos);

		txtFieldNumProcesos = new JTextField();
		txtFieldNumProcesos.setBounds(174, 14, 57, 22);
		contentPane.add(txtFieldNumProcesos);
		txtFieldNumProcesos.setColumns(10);

		JLabel lblSemilla = new JLabel("Semilla");
		lblSemilla.setFont(new Font("Helvetica Neue", 12, 12));
		lblSemilla.setBounds(282, 17, 61, 16);
		contentPane.add(lblSemilla);

		txtFieldSeed = new JTextField();
		txtFieldSeed.setBounds(344, 14, 57, 22);
		contentPane.add(txtFieldSeed);
		txtFieldSeed.setColumns(10);

		lblTiempoBurst = new JLabel("Tiempo Burst");
		lblTiempoBurst.setFont(new Font("Helvetica Neue", 12, 12));
		lblTiempoBurst.setBounds(36, 56, 91, 16);
		contentPane.add(lblTiempoBurst);

		comboBurst = new JComboBox();
		comboBurst.setFont(new Font("Helvetica Neue", Font.BOLD, 12));
		comboBurst.setBounds(171, 52, 136, 27);
		comboBurst.addItem("Constante");
		comboBurst.addItem("Uniforme");
		comboBurst.addItem("Exponencial");
		comboBurst.addItem("Normal");
		comboBurst.addActionListener(this);
		contentPane.add(comboBurst);

		JLabel lblTiempoBloqueado = new JLabel("Tiempo Bloqueado");
		lblTiempoBloqueado.setFont(new Font("Helvetica Neue", 12, 12));
		lblTiempoBloqueado.setBounds(36, 106, 126, 16);
		contentPane.add(lblTiempoBloqueado);

		comboBloqueado = new JComboBox();
		comboBloqueado.setFont(new Font("Helvetica Neue", Font.BOLD, 12));
		comboBloqueado.setBounds(171, 102, 136, 27);
		comboBloqueado.addItem("Constante");
		comboBloqueado.addItem("Uniforme");
		comboBloqueado.addItem("Exponencial");
		comboBloqueado.addItem("Normal");
		comboBloqueado.addActionListener(this);
		contentPane.add(comboBloqueado);

		JLabel lblTiempoDeLlegada = new JLabel(
				"Tiempo de llegada entre procesos");
		lblTiempoDeLlegada.setFont(new Font("Helvetica Neue", 12, 12));
		lblTiempoDeLlegada.setBounds(36, 153, 212, 16);
		contentPane.add(lblTiempoDeLlegada);

		comboLlegada = new JComboBox();
		comboLlegada.setFont(new Font("Helvetica Neue", Font.BOLD, 12));
		comboLlegada.setBounds(174, 181, 133, 27);
		comboLlegada.addItem("Constante");
		comboLlegada.addItem("Uniforme");
		comboLlegada.addItem("Exponencial");
		comboLlegada.addItem("Normal");
		comboLlegada.addActionListener(this);
		contentPane.add(comboLlegada);

		txtFieldBurstCamp1 = new JTextField();
		txtFieldBurstCamp1.setBounds(310, 50, 57, 22);
		contentPane.add(txtFieldBurstCamp1);
		txtFieldBurstCamp1.setColumns(10);

		txtFieldBurstCamp2 = new JTextField();
		txtFieldBurstCamp2.setColumns(10);
		txtFieldBurstCamp2.setBounds(379, 50, 57, 22);
		txtFieldBurstCamp2.setVisible(false);
		contentPane.add(txtFieldBurstCamp2);

		txtFieldBloqueadoCamp1 = new JTextField();
		txtFieldBloqueadoCamp1.setColumns(10);
		txtFieldBloqueadoCamp1.setBounds(310, 100, 57, 22);
		contentPane.add(txtFieldBloqueadoCamp1);

		txtFieldBloqueadoCamp2 = new JTextField();
		txtFieldBloqueadoCamp2.setColumns(10);
		txtFieldBloqueadoCamp2.setBounds(379, 100, 57, 22);
		txtFieldBloqueadoCamp2.setVisible(false);
		contentPane.add(txtFieldBloqueadoCamp2);

		txtFieldLlegadaCamp1 = new JTextField();
		txtFieldLlegadaCamp1.setColumns(10);
		txtFieldLlegadaCamp1.setBounds(310, 179, 57, 22);
		contentPane.add(txtFieldLlegadaCamp1);

		txtFieldLlegadaCamp2 = new JTextField();
		txtFieldLlegadaCamp2.setColumns(10);
		txtFieldLlegadaCamp2.setBounds(379, 179, 57, 22);
		txtFieldLlegadaCamp2.setVisible(false);
		contentPane.add(txtFieldLlegadaCamp2);

		btnConfigurar = new JButton("Configurar");
		btnConfigurar.setFont(new Font("Helvetica Neue", Font.BOLD, 14));
		btnConfigurar.setBounds(6, 234, 430, 29);
		contentPane.add(btnConfigurar);

		btnConfigurar.addMouseListener(new MouseListener() {

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
				if (txtFieldBloqueadoCamp1.getText().equals("")
						|| txtFieldBurstCamp1.getText().equals("")
						|| txtFieldLlegadaCamp1.getText()
								.equals("")
						|| txtFieldNumProcesos.getText().equals("")) {
					JOptionPane.showMessageDialog(null,
							"Debe llenar los datos requeridos!!");
				} else if (((comboBurst.getSelectedItem().equals(
						"Uniforme") || comboBurst.getSelectedItem()
						.equals("Normal")) && txtFieldBurstCamp2
						.getText().equals(""))
						|| ((comboLlegada.getSelectedItem().equals(
								"Uniforme") || comboLlegada
								.getSelectedItem().equals(
										"Normal")) && txtFieldLlegadaCamp2
								.getText().equals(""))
						|| ((comboBloqueado.getSelectedItem()
								.equals("Uniforme") || comboBloqueado
								.getSelectedItem().equals(
										"Normal")) && txtFieldBloqueadoCamp2
								.getText().equals(""))) {
					JOptionPane.showMessageDialog(null,
							"Debe llenar los datos requeridos!!");
				} else {
					numeroProcesos = Integer
							.parseInt(txtFieldNumProcesos
									.getText());
					try {
						seed = Long.parseLong(txtFieldSeed
								.getText());
					} catch (Exception e) {
						seed = 0;
					}

					Vector<Double> vectorBurst = null;
					Vector<Double> vectorLlegada = null;
					Vector<Double> vectorBloqueado = null;

					Double campo1, campo2;

					String seleccionado = (String) comboBurst
							.getSelectedItem();
					if (seleccionado.equals("Uniforme")
							|| seleccionado.equals("Normal")) {
						campo1 = Double
								.parseDouble(txtFieldBurstCamp1
										.getText());
						campo2 = Double
								.parseDouble(txtFieldBurstCamp2
										.getText());
						vectorBurst = construirVector(seleccionado,
								campo1, campo2);
					} else {
						campo1 = Double
								.parseDouble(txtFieldBurstCamp1
										.getText());
						vectorBurst = construirVector(seleccionado,
								campo1, (double) 0);
					}
					seleccionado = (String) comboBloqueado
							.getSelectedItem();
					if (seleccionado.equals("Uniforme")
							|| seleccionado.equals("Normal")) {
						campo1 = Double
								.parseDouble(txtFieldBloqueadoCamp1
										.getText());
						campo2 = Double
								.parseDouble(txtFieldBloqueadoCamp2
										.getText());
						vectorBloqueado = construirVector(
								seleccionado, campo1, campo2);
					} else {
						campo1 = Double
								.parseDouble(txtFieldBloqueadoCamp1
										.getText());
						vectorBloqueado = construirVector(
								seleccionado, campo1,
								(double) 0);
					}
					seleccionado = (String) comboLlegada
							.getSelectedItem();
					if (seleccionado.equals("Uniforme")
							|| seleccionado.equals("Normal")) {
						campo1 = Double
								.parseDouble(txtFieldLlegadaCamp1
										.getText());
						campo2 = Double
								.parseDouble(txtFieldLlegadaCamp2
										.getText());
						vectorLlegada = construirVector(
								seleccionado, campo1, campo2);
					} else {
						campo1 = Double
								.parseDouble(txtFieldLlegadaCamp1
										.getText());
						vectorLlegada = construirVector(
								seleccionado, campo1,
								(double) 0);
					}

					panelPlanificadorCPU.vectorBurst = vectorBurst;
					panelPlanificadorCPU.vectorLlegada = vectorLlegada;
					panelPlanificadorCPU.vectorBloqueado = vectorBloqueado;

					setVisible(false);
					dispose();
				}
			}
		});

		this.setVisible(true);
		this.requestFocus();
		this.toFront();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == comboBurst) {
			String seleccionado = (String) comboBurst.getSelectedItem();
			if (seleccionado.equals("Uniforme")
					|| seleccionado.equals("Normal")) {
				txtFieldBurstCamp2.setVisible(true);
			} else {
				txtFieldBurstCamp2.setVisible(false);
			}

		} else if (e.getSource() == comboBloqueado) {
			String seleccionado = (String) comboBloqueado
					.getSelectedItem();
			if (seleccionado.equals("Uniforme")
					|| seleccionado.equals("Normal")) {
				txtFieldBloqueadoCamp2.setVisible(true);
			} else {
				txtFieldBloqueadoCamp2.setVisible(false);
			}
		} else if (e.getSource() == comboLlegada) {
			String seleccionado = (String) comboLlegada.getSelectedItem();
			if (seleccionado.equals("Uniforme")
					|| seleccionado.equals("Normal")) {
				txtFieldLlegadaCamp2.setVisible(true);
			} else {
				txtFieldLlegadaCamp2.setVisible(false);
			}
		}
	}

	public Vector<Double> construirVector(String _distribucion,
			Double _inicio, Double _final) {
		Vector<Double> aux = new Vector<Double>();

		if (seed != 0) {
			StdRandom.setSeed(seed);
		}

		if (_distribucion.equals("Constante")) {
			for (int i = 0; i < numeroProcesos; i++) {
				aux.add(_inicio);
			}
		} else if (_distribucion.equals("Uniforme")) {
			for (int i = 0; i < numeroProcesos; i++) {
				aux.add(StdRandom.uniform(_inicio, _final));
			}
		} else if (_distribucion.equals("Exponencial")) {
			for (int i = 0; i < numeroProcesos; i++) {
				aux.add(StdRandom.exp(_inicio));
			}
		} else if (_distribucion.equals("Normal")) {
			for (int i = 0; i < numeroProcesos; i++) {
				aux.add(StdRandom.gaussian(_inicio, _final));
			}
		}
		return aux;
	}

	/**
	 * Launch the application.
	 */
	/*
	 * public static void main(String[] args) { EventQueue.invokeLater(new
	 * Runnable() { public void run() { try { new
	 * PanelConfiguracionProcesos(null); } catch (Exception e) {
	 * e.printStackTrace(); } } }); }
	 */
}
