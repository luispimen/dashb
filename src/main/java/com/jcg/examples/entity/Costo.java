package com.jcg.examples.entity;

public class Costo {
	private Long presupuesto;
	private Long planificadoFecha;
	private Long gastado;
	private Long disponible;
	private String moneda;

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public Long getPresupuesto() {
		return presupuesto;
	}

	public void setPresupuesto(Long presupuesto) {
		this.presupuesto = (presupuesto != null) ? presupuesto : 0;
	}

	public Long getPlanificadoFecha() {
		return planificadoFecha;
	}

	public void setPlanificadoFecha(Long planificadoFecha) {
		this.planificadoFecha = (planificadoFecha != null) ? planificadoFecha : 0;
	}

	public Long getGastado() {
		return gastado;
	}

	public void setGastado(Long gastado) {
		this.gastado = (gastado != null) ? gastado : 0;
	}

	public Long getDisponible() {
		return disponible;
	}

	public void setDisponible(Long disponible) {
		this.disponible = disponible;
	}

}
