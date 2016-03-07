package com.jcg.examples.entity;

public class ResumenTareas {
	private String[] retrasadas = new String[] {};
	private String[] proximas = new String[] {};
	private String[] realizadas = new String[] {};

	public String[] getRetrasadas() {
		return retrasadas;
	}

	public void setRetrasadas(String[] retrasadas) {
		this.retrasadas = retrasadas;
	}

	public String[] getProximas() {
		return proximas;
	}

	public void setProximas(String[] proximas) {
		this.proximas = proximas;
	}

	public String[] getRealizadas() {
		return realizadas;
	}

	public void setRealizadas(String[] realizadas) {
		this.realizadas = realizadas;
	}

}
