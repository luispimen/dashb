package com.jcg.examples.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lpimentel on 26-02-2016.
 */
public class HistoryPoint implements Comparable {
	private String date;
	private long real;
	private long plan;

	public HistoryPoint(String date, long plan, long real) {
		this.date = date;
		this.real = real;
		this.plan = plan;
	}

	public HistoryPoint() {
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public long getReal() {
		return real;
	}

	public void setReal(long real) {
		this.real = real;
	}

	public long getPlan() {
		return plan;
	}

	public void setPlan(long plan) {
		this.plan = plan;
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
