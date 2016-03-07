package com.jcg.examples.entity;

public class Riesgo {

	private String nombre;
	private String impacto;
	private String mitigacion;
	private String variableAfectada;
	private String factorDeRiesgo;

	public String getFactorDeRiesgo() {
		return factorDeRiesgo;
	}

	public void setFactorDeRiesgo(String factorDeRiesgo) {
		this.factorDeRiesgo = factorDeRiesgo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getImpacto() {
		return impacto;
	}

	public void setImpacto(String impacto) {
		this.impacto = impacto;
	}

	public String getMitigacion() {
		return mitigacion;
	}

	public void setMitigacion(String mitigacion) {
		this.mitigacion = mitigacion;
	}

	public String getVariableAfectada() {
		return variableAfectada;
	}

	public void setVariableAfectada(String variableAfectada) {
		this.variableAfectada = variableAfectada;
	}

}
