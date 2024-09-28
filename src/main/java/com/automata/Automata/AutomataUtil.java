package com.automata.Automata;

import java.util.ArrayList;
import java.util.Set;

import java.util.stream.Collectors;

// herramientas
public class AutomataUtil {
	private Automata automata;

	public AutomataUtil(Automata automata) {
		this.automata = automata;
	}

	public String[][] tabla() {
		ArrayList<Estado> estados = automata.getEstados();
		char[] alfabeto = automata.getAlfabeto();

		String[][] tabla = new String[estados.size() + 1][alfabeto.length + 2];

		// Encabezado de la tabla
		tabla[0][0] = "d"; // nombres de estados
		int columna = 1;
		for (char simbolo : alfabeto) {
			tabla[0][columna++] = String.valueOf(simbolo); // simbolos del alfabeto
		}
		tabla[0][columna] = "f";
		int fila = 1;
		for (Estado estado : estados) {
			tabla[fila][0] = estado.getNombre(); // Nombre del estado

			columna = 1;
			for (char simbolo : alfabeto) {
				// Obtener transiciones desde el estado actual con simbolo especifico
				Set<Transicion> transicionesConSimbolo = automata.transicionesDesde(estado, simbolo);
				if (transicionesConSimbolo.isEmpty()) {
					tabla[fila][columna] = "-";
				} else {
					String destinos = transicionesConSimbolo.stream().map(t -> t.getHasta().getNombre()).sorted()
							.collect(Collectors.joining(","));
					tabla[fila][columna] = destinos;
				}
				columna++;
			}
			tabla[fila][columna] = estado.isAceptador() ? "1" : "0";

			fila++;
		}

		return tabla;
	}
}
