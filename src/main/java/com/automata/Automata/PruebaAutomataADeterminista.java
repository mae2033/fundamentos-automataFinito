package com.automata.Automata;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PruebaAutomataADeterminista {

	private static final Logger logger = Logger.getLogger(PruebaAutomataADeterminista.class.getName());

	public static void main(String[] args) {
		try {
			ArrayList<String> entradas = leerEntradas("entradas.txt");
			char[] alfabeto = convertirEntradasAAlfabeto(entradas);
			Map<String, Estado> map = new HashMap<>();
			ArrayList<Estado> estados = leerEstados("estados.txt", map);
			Set<Transicion> transiciones = leerTransiciones("transiciones.txt", alfabeto, map);

			Automata automata = new Automata(alfabeto, estados, transiciones);
			JFrameTablaAutomata frame = new JFrameTablaAutomata(automata);
			frame.setVisible(true);
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "Error en el archivo: {0}", e.getMessage());
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error inesperado: {0}", e.getMessage());
		}
	}

	private static ArrayList<String> leerEntradas(String archivo) throws FileNotFoundException {
		ArrayList<String> entradas = new ArrayList<>();
		try (Scanner read = new Scanner(new File(archivo))) {
			read.useDelimiter("\\s*;\\s*");
			boolean archivoEntradasVacio = true;

			while (read.hasNext()) {
				archivoEntradasVacio = false;
				String entrada = read.next();

				validarEntrada(entrada, entradas);
				entradas.add(entrada);
			}

			if (archivoEntradasVacio) {
				logger.severe("Archivo de entradas vacío");
				throw new ArchivoEntradasEstaVacioException();
			}
		} catch (FileNotFoundException e) {
			throw e;
		}

		return entradas;
	}

	private static void validarEntrada(String entrada, ArrayList<String> entradas) {
		if (entrada.length() > 1) {
			throw new EntradaNoEsCharException("Entrada no es de longitud 1: " + entrada);
		}
		if (entradas.contains(entrada)) {
			throw new EntradaRepetidaException("Entrada repetida: " + entrada);
		}
	}

	private static char[] convertirEntradasAAlfabeto(ArrayList<String> entradas) {
		char[] alfabeto = new char[entradas.size()];
		int i = 0;
		for (String ent : entradas) {
			alfabeto[i++] = ent.charAt(0);
		}
		return alfabeto;
	}

	private static ArrayList<Estado> leerEstados(String archivo, Map<String, Estado> map) throws FileNotFoundException {
		ArrayList<Estado> estados = new ArrayList<>();
		try (Scanner read = new Scanner(new File(archivo))) {
			read.useDelimiter("\\s*;\\s*");
			boolean archivoEstadosVacio = true;

			while (read.hasNext()) {
				archivoEstadosVacio = false;
				String estadoNombre = read.next();
				String esAceptadorONo = read.next();

				Estado s = crearEstado(estadoNombre, esAceptadorONo);
				if (map.containsKey(estadoNombre)) {
					throw new EstadoYaExisteException("Estado repetido: " + estadoNombre);
				}
				estados.add(s);
				map.put(estadoNombre, s);
			}

			if (archivoEstadosVacio) {
				throw new ArchivoEstadosEstaVacioException();
			}
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "Archivo {0} no encontrado", archivo);
			throw e;
		}

		return estados;
	}

	private static Estado crearEstado(String nombre, String esAceptadorONo) {
		if ("0".equals(esAceptadorONo)) {
			return new Estado(nombre, false);
		} else if ("1".equals(esAceptadorONo)) {
			return new Estado(nombre, true);
		} else {
			throw new NoEsCeroOUnoException("No indica si es aceptador: " + nombre);
		}
	}

	private static Set<Transicion> leerTransiciones(String archivo, char[] alfabeto, Map<String, Estado> map)
			throws FileNotFoundException {
		Set<Transicion> transiciones = new HashSet<>();
		try (Scanner read = new Scanner(new File(archivo))) {
			read.useDelimiter("\\s*;\\s*");

			while (read.hasNext()) {
				String estadoInicio = read.next();
				String entrada = read.next();
				String estadoLlegada = read.next();

				validarEntrada(entrada, alfabeto, estadoInicio, estadoLlegada);
				Estado estadoInicio1 = obtenerEstado(map, estadoInicio);
				Estado estadoLlegada1 = obtenerEstado(map, estadoLlegada);

				Transicion transicion = new Transicion(estadoInicio1, entrada.charAt(0), estadoLlegada1);
				if (transicionYaExiste(transiciones, transicion)) {
					throw new TransicionYaExisteException(
							"Transición repetida: " + estadoInicio + ";" + entrada + ";" + estadoLlegada);
				}
				transiciones.add(transicion);
			}
		} catch (FileNotFoundException e) {

			throw e;
		}

		return transiciones;
	}

	private static void validarEntrada(String entrada, char[] alfabeto, String estadoInicio, String estadoLlegada) {
		if (entrada.length() > 1) {
			throw new EntradaNoEsCharException(
					"Entrada no es de longitud 1: " + estadoInicio + ";" + entrada + ";" + estadoLlegada);
		}
		boolean entradaExiste = false;
		for (char c : alfabeto) {
			if (c == entrada.charAt(0)) {
				entradaExiste = true;
				break;
			}
		}
		if (!entradaExiste) {
			throw new EntradaNoExisteException(
					"Entrada no válida:" + estadoInicio + ";" + entrada + ";" + estadoLlegada);
		}
	}

	private static Estado obtenerEstado(Map<String, Estado> map, String estado) {
		Estado estadoObtenido = map.get(estado);
		if (estadoObtenido == null) {
			throw new NullPointerException("Estado no existe: " + estado);
		}
		return estadoObtenido;
	}

	private static boolean transicionYaExiste(Set<Transicion> transiciones, Transicion transicion) {
		return transiciones.stream()
				.anyMatch(tran -> tran.getDesde().getNombre().equals(transicion.getDesde().getNombre())
						&& tran.getHasta().getNombre().equals(transicion.getHasta().getNombre())
						&& tran.getSimbolo() == transicion.getSimbolo());
	}
}
