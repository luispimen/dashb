package com.jcg.examples.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lpimentel on 26-02-2016.
 */
public class HistoryPoint implements Comparable {
    private String date;
    private Long real;
    private Long plan;
    private Map<String, Long> lineasBaseMap = new HashMap<String, Long>();
    private Long base = null;

    public HistoryPoint() {
    }

    public HistoryPoint(String date, Long real, Long plan, Long base) {
        this.date = date;
        this.real = real;
        this.plan = plan;
        this.base = base;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getReal() {
        return real;
    }

    public void setReal(Long real) {
        this.real = real;
    }

    public Long getPlan() {
        return plan;
    }

    public void setPlan(Long plan) {
        this.plan = plan;
    }

    public Map<String, Long> getLineasBaseMap() {
        return lineasBaseMap;
    }

    public void setLineasBaseMap(Map<String, Long> lineasBaseMap) {
        this.lineasBaseMap = lineasBaseMap;
    }

    public Long getBase() {
        return base;
    }

    public void setBase(Long base) {
        this.base = base;
    }

    @Override
    public int compareTo(Object o) {
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        int comparationResult = 0;
        try {
            Date date = dt.parse(getDate());
            Date date1 = dt.parse(((HistoryPoint) o).getDate());
            comparationResult = date.compareTo(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return comparationResult;
    }
}
