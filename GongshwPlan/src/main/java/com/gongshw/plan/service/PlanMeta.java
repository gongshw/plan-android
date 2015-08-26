package com.gongshw.plan.service;

import java.util.UUID;

/**
 * Author       : gongshw
 * Created At   : 15/2/9.
 */
public class PlanMeta {
	private String id;
	private String unit;
	private long index;
	private boolean repeat;
	private String text;
	private double sort;
	private String color;

	public PlanMeta() {
		id = UUID.randomUUID().toString().replaceAll("-", "");
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public long getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isRepeat() {
		return repeat;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public double getSort() {
		return sort;
	}

	public void setSort(double sort) {
		this.sort = sort;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
