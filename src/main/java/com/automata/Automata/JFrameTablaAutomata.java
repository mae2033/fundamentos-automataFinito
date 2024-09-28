package com.automata.Automata;

import java.awt.Dimension;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

public class JFrameTablaAutomata extends JFrame {
	private String[][] matrizNoDeterminista;
	private String[][] matrizDeterminista;
	boolean determinista;
	private Automata automata;
	private Automata automataDeterminista;

	private JTextField campoTexto;
	private JButton boton;

	public JFrameTablaAutomata(Automata automata) {
		this.automata = automata;

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		// Obtener el tamanio de la pantalla
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		setSize(new Dimension(screenSize.width, screenSize.height - 50));

		JPanel panel = new JPanel();

		JScrollPane scrollPane = new JScrollPane(panel);

		add(scrollPane);

		aDeterminista();

		if (!determinista) {
			int numFilas = matrizNoDeterminista.length - 1; // Numero de filas en la nueva matriz
			int numColumnas = matrizNoDeterminista[0].length; // Numero de columnas (asumimos que todas las filas tienen
																// el
																// mismo numero de columnas)
			String[][] matrizNoDeterministaCopia = new String[numFilas][numColumnas];

			// Copiar las filas de la matriz original a la nueva matriz, omitiendo la
			// primera fila
			for (int i = 0; i < numFilas; i++) {
				matrizNoDeterministaCopia[i] = matrizNoDeterminista[i + 1];
			}

			DefaultTableModel model1 = new DefaultTableModel(matrizNoDeterministaCopia, matrizNoDeterminista[0]) {
				@Override
				public boolean isCellEditable(int row, int column) {
					return false; // Hacer todas las celdas no editables
				}
			};

			JTable table1 = new JTable(model1);

			JScrollPane scrollPane1 = new JScrollPane(table1);

			panel.add(scrollPane1);
		}

		int numFilas = matrizDeterminista.length - 1; // Numero de filas en la nueva matriz
		int numColumnas = matrizDeterminista[0].length; // Numero de columnas (asumimos que todas las filas tienen
														// el
														// mismo numero de columnas)
		String[][] matrizDeterministaCopia = new String[numFilas][numColumnas];

		// Copiar las filas de la matriz original a la nueva matriz, omitiendo la
		// primera fila
		for (int i = 0; i < numFilas; i++) {
			matrizDeterministaCopia[i] = matrizDeterminista[i + 1];
		}

		DefaultTableModel model2 = new DefaultTableModel(matrizDeterministaCopia, matrizDeterminista[0]) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Hacer todas las celdas no editables
			}
		};

		JTable table2 = new JTable(model2);

		JScrollPane scrollPane2 = new JScrollPane(table2);

		panel.add(scrollPane2);

		campoTexto = new JTextField(15);
		panel.add(campoTexto);

		boton = new JButton("Evaluar cadena");
		panel.add(boton);

		ManejadorBoton manejador = new ManejadorBoton();
		boton.addActionListener(manejador);

		mxGraph mxGrafo = new mxGraph();

		mxGrafo.getModel().beginUpdate();
		try {
			mxCell[] elementosEstados = new mxCell[automataDeterminista.getEstados().size()];
			mxCell estadoInicial = null;

			for (int i = 0; i < automataDeterminista.getEstados().size(); i++) {
				String nombreEstado = automataDeterminista.getEstados().get(i).getNombre();
				// Calcular el tamaño del nodo basado en el tamaño del texto
				int ancho = Math.max(nombreEstado.length() * 5 + 10, 80); // Ancho mínimo de 80
				int altura = Math.max(nombreEstado.length() * 5 + 5, 30); // Altura basada en el tamaño del texto con
																			// mínimo de 30

				elementosEstados[i] = (mxCell) mxGrafo.insertVertex(null, null, nombreEstado, 100 + i * 200, 100, ancho,
						altura);

				if (automataDeterminista.getEstados().get(i).isAceptador()) {
					// Doble círculo para estados aceptadores
					elementosEstados[i].setStyle("shape=doubleEllipse;fillColor=lightgreen;strokeColor=black;");
				} else {
					// Círculo simple para estados no aceptadores
					elementosEstados[i].setStyle("shape=ellipse;fillColor=lightgrey;strokeColor=black;");
				}

				// Identificar el estado inicial
				if (automataDeterminista.getEstados().get(i).isInicial()) {
					estadoInicial = elementosEstados[i];
				}

			}

			// Mapa para almacenar transiciones únicas
			Map<String, StringBuilder> transicionesMap = new HashMap<>();

			for (Transicion transicion : automataDeterminista.getTransiciones()) {
				String delEstado = transicion.getDesde().getNombre();
				String haciaEstado = transicion.getHasta().getNombre();
				char entrada = transicion.getSimbolo();

				// Crear una clave única para cada par de estados
				String clave = delEstado + "->" + haciaEstado;

				// Si ya existe una entrada para esta clave, se agrega el símbolo, si no se crea
				// uno nuevo
				transicionesMap.putIfAbsent(clave, new StringBuilder());
				StringBuilder simbolos = transicionesMap.get(clave);
				if (simbolos.length() > 0) {
					simbolos.append(","); // Agregar coma si ya hay símbolos
				}
				simbolos.append(entrada);
			}

			for (Map.Entry<String, StringBuilder> entry : transicionesMap.entrySet()) {
				String[] estados = entry.getKey().split("->");
				String delEstado = estados[0];
				String haciaEstado = estados[1];
				String entradas = entry.getValue().toString();

				mxCell desdeElemento = buscarElementoPorEstado(delEstado, elementosEstados);
				mxCell hastaElemento = buscarElementoPorEstado(haciaEstado, elementosEstados);

				mxGrafo.insertEdge(null, null, entradas, desdeElemento, hastaElemento);

			}

			// Agregar una flecha desde un nodo ficticio hacia el estado
			// inicial
			if (estadoInicial != null) {

				mxCell nodoInicial = (mxCell) mxGrafo.insertVertex(null, null, "", 50, 100, 20, 20); // Nodo ficticio
																										// para el
																										// estado
																										// inicial
				nodoInicial.setStyle("shape=circle;fillColor=transparent;strokeColor=transparent;"); // Hacerlo
																										// invisible
				mxGrafo.insertEdge(null, null, "Inicio", nodoInicial, estadoInicial);
			}
		} finally {
			mxGrafo.getModel().endUpdate();
		}

		mxGraphComponent componenteGrafo = new mxGraphComponent(mxGrafo);

		mxCircleLayout layout = new mxCircleLayout(mxGrafo);
		layout.execute(mxGrafo.getDefaultParent());

		mxGrafo.setCellsResizable(false); // Deshabilitar el redimensionamiento de los nodos
		mxGrafo.setCellsEditable(false); // Deshabilitar la edición de las celdas
		mxGrafo.setConnectableEdges(false); // Deshabilitar la conexión de aristas
		mxGrafo.setEdgeLabelsMovable(false); // No permitir que las etiquetas de los edges se muevan

		panel.add(componenteGrafo);

	}

	private mxCell buscarElementoPorEstado(String estado, mxCell[] elementosEstados) {
		for (mxCell elemento : elementosEstados) {
			if (elemento.getValue().equals(estado)) {
				return elemento;
			}
		}
		return null;
	}

	private class ManejadorBoton implements ActionListener {

		public void actionPerformed(ActionEvent evento) {

			if (evento.getSource() == boton) {
				String cadena = campoTexto.getText();

				boolean cadenaAceptada = automataDeterminista.aceptarCadena(cadena);

				if (cadenaAceptada)
					JOptionPane.showMessageDialog(null, "Cadena aceptada", null, JOptionPane.INFORMATION_MESSAGE);
				else
					JOptionPane.showMessageDialog(null, "Cadena no aceptada", null, JOptionPane.ERROR_MESSAGE);
			}

		}
	}

	private void aDeterminista() {
		determinista = false;

		if (automata.isDeterminista()) {
			determinista = true;
			automataDeterminista = automata.convertirAFNDaAFD();
			AutomataUtil automataUtilDeterminista = new AutomataUtil(automataDeterminista);
			matrizDeterminista = automataUtilDeterminista.tabla();
			return;
		}

		AutomataUtil automataUtil = new AutomataUtil(automata);
		matrizNoDeterminista = automataUtil.tabla();

		automataDeterminista = automata.convertirAFNDaAFD();

		AutomataUtil automataUtilDeterminista = new AutomataUtil(automataDeterminista);
		matrizDeterminista = automataUtilDeterminista.tabla();

	}
}
