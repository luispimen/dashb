package com.jcg.examples.entity;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DashboardData implements Serializable {
	private ProjectDetails datosGenerales;
	private List<Plan> plan;
	private Costo costo;
	private ResumenTareas resumenTareas;
	private List<Riesgo> riesgos;
	private SignosVitales signosVitales;

	public SignosVitales getSignosVitales() {
		return signosVitales;
	}

	public void setSignosVitales(SignosVitales signosVitales) {
		this.signosVitales = signosVitales;
	}

	public List<Riesgo> getRiesgos() {
		return riesgos;
	}

	public void setRiesgos(List<Riesgo> riesgos) {
		this.riesgos = riesgos;
	}

	public ResumenTareas getResumenTareas() {
		return resumenTareas;
	}

	public void setResumenTareas(ResumenTareas resumenTareas) {
		this.resumenTareas = resumenTareas;
	}

	public Costo getCosto() {
		return costo;
	}

	public void setCosto(Costo costo) {
		this.costo = costo;
	}

	public List<Plan> getPlan() {
		return plan;
	}

	public void setPlan(List<Plan> plan) {
		this.plan = plan;
	}

	public ProjectDetails getDatosGenerales() {
		return datosGenerales;
	}

	public void setDatosGenerales(ProjectDetails datosGenerales) {
		this.datosGenerales = datosGenerales;
	}

	public String toJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

}
