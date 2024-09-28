package com.automata.Automata;


import java.util.Objects;

public class Estado {
	private String nombre;
	private boolean aceptador;
	private boolean inicial;

	public Estado(String nombre, boolean aceptador, boolean inicial) {
		this.nombre = nombre;
		this.aceptador = aceptador;
		this.inicial = inicial;
	}

	public Estado(String nombre, boolean aceptador) {
		this.nombre = nombre;
		this.aceptador = aceptador;
		this.inicial = false;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public boolean isAceptador() {
		return aceptador;
	}

	public void setAceptador(boolean aceptador) {
		this.aceptador = aceptador;
	}

	public boolean isInicial() {
		return inicial;
	}

	public void setInicial(boolean inicial) {
		this.inicial = inicial;
	}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Estado estado = (Estado) obj;
        return nombre.equals(estado.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
	@Override
	public String toString() {
		return nombre + (aceptador ? " (Aceptador) " : " ") + (inicial ? " (inicial) " : "");
	}
}
