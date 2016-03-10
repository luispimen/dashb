package com.jcg.examples.entity;

public class Plan {
    private String fecha;
    private Long plan;
    private Long real;
    private Long base = null;

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Long getPlan() {
        return plan;
    }

    public void setPlan(Long plan) {
        this.plan = plan;
    }

    public Long getReal() {
        return real;
    }

    public void setReal(Long real) {
        this.real = real;
    }

    public Long getBase() {
        return base;
    }

    public void setBase(Long base) {
        this.base = base;
    }
}
