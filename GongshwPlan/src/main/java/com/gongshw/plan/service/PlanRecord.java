package com.gongshw.plan.service;

/**
 * Author       : gongshw
 * Created At   : 15/2/9.
 */
public class PlanRecord {
	private String id;
	private double sort;
	private String text;
	private String color;
	private boolean repeat;
	private boolean finished;
	private Unit unit;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getSort() {
		return sort;
	}

	public void setSort(double sort) {
		this.sort = sort;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isRepeat() {
		return repeat;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public boolean getFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
