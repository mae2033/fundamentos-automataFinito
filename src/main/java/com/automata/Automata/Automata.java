package com.automata.Automata;

import java.util.ArrayList;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class Automata {
	private Estado estadoInicial;
	private ArrayList<Estado> estados;
	private Set<Transicion> transiciones;
	private char[] alfabeto;

	public Automata(char[] alfabeto, ArrayList<Estado> estados, Set<Transicion> transiciones) {
		super();
		this.alfabeto = alfabeto;
		this.estados = estados;
		this.transiciones = transiciones;
		this.estadoInicial = findEstadoInicial();
	}

	public char[] getAlfabeto() {
		return alfabeto;
	}

	public void setAlfabeto(char[] alfabeto) {
		this.alfabeto = alfabeto;
	}

	public Estado getEstadoInicial() {
		return estadoInicial;
	}

	public Estado findEstadoInicial() {
		for (Estado estado : estados) {
			if (estado.isInicial())
				return estado;
		}
		return estados.stream().findFirst().orElse(null);
	}

	public ArrayList<Estado> getEstados() {
		return estados;
	}

	public Set<Transicion> getTransiciones() {
		return transiciones;
	}

	public boolean aceptarCadena(String cadena) {
		if (!isDeterminista())
			return false;
		Estado estadoActual = estadoInicial;
		for (char simbolo : cadena.toCharArray()) {
			estadoActual = transicion(estadoActual, simbolo);
			if (estadoActual == null) {
				return false;
			}
		}
		return estadoActual.isAceptador();
	}

	// retorna el estado destino dado por un Estado y una entrada
	private Estado transicion(Estado estado, char simbolo) {
		for (Transicion transicion : transicionesDesde(estado)) {
			if (transicion.getSimbolo() == simbolo) {
				return transicion.getHasta();
			}
		}
		return null;
	}

	public boolean isDeterminista() {
		if (estadoInicial == null) {
			return false; // No hay estado inicial
		}

		Map<Estado, Set<Character>> mapaTransiciones = new HashMap<>();
		for (Transicion transicion : transiciones) {
			Estado estadoDesde = transicion.getDesde();
			char simbolo = transicion.getSimbolo();

			if (!mapaTransiciones.containsKey(estadoDesde)) {
				mapaTransiciones.put(estadoDesde, new HashSet<>());
			}
			Set<Character> simbolos = mapaTransiciones.get(estadoDesde);
			if (simbolos.contains(simbolo)) {
				return false; // Hay más de una transición para el mismo símbolo desde el mismo estado
			}
			// Agregar el símbolo al conjunto de transiciones del estado
			simbolos.add(simbolo);
		}
		return true;
	}

	public Automata convertirAFNDaAFD() {
		Set<Set<Estado>> nuevosEstados = new HashSet<>();
		Queue<Set<Estado>> estadosPorProcesar = new LinkedList<>();
		Set<Transicion> nuevasTransiciones = new HashSet<>();
		Set<Estado> estadosAFD = new HashSet<>();
		Map<Set<Estado>, Estado> nombreDeEstado = new HashMap<>();

		// Paso 1: Crear el estado inicial del AFD
		Set<Estado> estadoInicialAFD = new HashSet<>();
		estadoInicialAFD.add(estadoInicial); // Comienza con el estado inicial del AFN
		estadosPorProcesar.add(estadoInicialAFD);
		nuevosEstados.add(estadoInicialAFD);

		// Asignar nombre al estado inicial del AFD
		Estado estadoInicialAFDEquivalente = new Estado(estadoInicial.getNombre(), false, true); // Define si es un
																									// estado de
																									// aceptacion mas
																									// adelante
		nombreDeEstado.put(estadoInicialAFD, estadoInicialAFDEquivalente);
		estadosAFD.add(estadoInicialAFDEquivalente); // Agrega el estado inicial a los estados del AFD

		while (!estadosPorProcesar.isEmpty()) {
			Set<Estado> conjuntoActual = estadosPorProcesar.poll(); // Tomar el siguiente conjunto de estados para
																	// procesar
			Estado estadoActualAFD = nombreDeEstado.get(conjuntoActual);

			for (char simbolo : alfabeto) {
				Set<Estado> nuevoConjunto = new HashSet<>();

				// Para cada estado en el conjunto actual, obtener las transiciones con el
				// simbolo actual
				for (Estado estado : conjuntoActual) {
					Set<Transicion> transicionesConSimbolo = transicionesDesde(estado).stream()
							.filter(t -> t.getSimbolo() == simbolo).collect(Collectors.toSet());

					for (Transicion t : transicionesConSimbolo) {
						nuevoConjunto.add(t.getHasta());
					}
				}

				if (!nuevosEstados.contains(nuevoConjunto) && !nuevoConjunto.isEmpty()) { // Si el nuevo conjunto de
																							// estados no ha sido
																							// procesado
					nuevosEstados.add(nuevoConjunto);
					estadosPorProcesar.add(nuevoConjunto);

					// Asignar nombre unico al nuevo conjunto de estados
					Estado nuevoEstadoAFD = new Estado(
							nuevoConjunto.stream().map(Estado::getNombre).sorted().collect(Collectors.joining(",")),
							false);
					nombreDeEstado.put(nuevoConjunto, nuevoEstadoAFD);
					estadosAFD.add(nuevoEstadoAFD);
				}

				Estado estadoDestinoAFD = nombreDeEstado.get(nuevoConjunto);
				if (estadoDestinoAFD != null) {
					nuevasTransiciones.add(new Transicion(estadoActualAFD, simbolo, estadoDestinoAFD));
				}
			}
		}

		for (Set<Estado> conjunto : nuevosEstados) {
			boolean esAceptador = conjunto.stream().anyMatch(Estado::isAceptador);
			Estado estadoAFD = nombreDeEstado.get(conjunto);
			if (esAceptador) {
				estadoAFD.setAceptador(true);
			}
		}

		List<Estado> listaOrdenada = estadosAFD.stream().sorted(Comparator.comparing(Estado::getNombre))
				.collect(Collectors.toList());

		ArrayList<Estado> estadosAFDOrdenados = new ArrayList<Estado>(listaOrdenada);

		return new Automata(alfabeto, estadosAFDOrdenados, nuevasTransiciones);
	}

	public Set<Transicion> transicionesDesde(Estado desde) {
		return transiciones.stream().filter(transicion -> transicion.getDesde().equals(desde))
				.collect(Collectors.toSet());
	}

	public Set<Transicion> transicionesDesde(Estado desde, char entrada) {
		return transiciones.stream()
				.filter(transicion -> transicion.getDesde().equals(desde) && transicion.getSimbolo() == entrada)
				.collect(Collectors.toSet());
	}
}
