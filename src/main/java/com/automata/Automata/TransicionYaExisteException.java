package com.automata.Automata;

public class TransicionYaExisteException extends RuntimeException {
	
	public TransicionYaExisteException() {
		super();
	}
	
	public TransicionYaExisteException(String mensaje) {
		super(mensaje);
	}

}
