package com.automata.Automata;


public class Transicion {
    private Estado desde;
    private char simbolo;
    private Estado hasta;

    public Transicion(Estado desde, char simbolo, Estado hasta) {
        this.desde = desde;
        this.simbolo = simbolo;
        this.hasta = hasta;
    }

	public Estado getDesde() {
		return desde;
	}

	public void setDesde(Estado desde) {
		this.desde = desde;
	}

	public Estado getHasta() {
		return hasta;
	}

	public void setHasta(Estado hasta) {
		this.hasta = hasta;
	}

	public char getSimbolo() {
		return simbolo;
	}

	public void setSimbolo(char simbolo) {
		this.simbolo = simbolo;
	}

	@Override
	public String toString() {
		return "Transicion:[ -" + desde.getNombre() + " -" + simbolo + "-> " + hasta.getNombre() + "] ";
	}


}
