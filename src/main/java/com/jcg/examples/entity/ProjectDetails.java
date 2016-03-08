package com.jcg.examples.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by lpimentel on 02-03-2016.
 */
public class ProjectDetails implements Serializable {
	private String jefeProyecto;
	private String resumen;
	private String sponsor;
	private String lider;
	private String desc;
	private String name;
	private Long porciento;
	private long id;
	private Date startDate;
	private Date endDate;

	public String getResumen() {
		return resumen;
	}

	public void setResumen(String resumen) {
		this.resumen = resumen;
	}

	public Long getPorciento() {
		return porciento;
	}

	public void setPorciento(Long porciento) {
		this.porciento = porciento;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public ProjectDetails() {
	}

	public ProjectDetails(String jefeProyecto, String sponsor, String lider, String desc, String name, long id) {
		this.jefeProyecto = jefeProyecto;
		this.sponsor = sponsor;
		this.lider = lider;
		this.desc = desc;
		this.name = name;
		this.id = id;
	}

	public String getJefeProyecto() {
		return jefeProyecto;
	}

	public void setJefeProyecto(String jefeProyecto) {
		this.jefeProyecto = jefeProyecto;
	}

	public String getSponsor() {
		return sponsor;
	}

	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}

	public String getLider() {
		return lider;
	}

	public void setLider(String lider) {
		this.lider = lider;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ProjectDetails [jefeProyecto=" + jefeProyecto + ", sponsor=" + sponsor + ", lider=" + lider + ", desc="
				+ desc + ", name=" + name + ", id=" + id + "]";
	}

}
