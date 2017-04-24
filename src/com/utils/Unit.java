package com.utils;

public class Unit {
	int latency;
	String name;
	String type;
	boolean availablity;
	public boolean isAvailablity() {
		return availablity;
	}
	public void setAvailablity(boolean availablity) {
		this.availablity = availablity;
	}
	public int getLatency() {
		return latency;
	}
	public void setLatency(int latency) {
		this.latency = latency;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Unit(int latency, String name, String type,boolean availability) {
		super();
		this.latency = latency;
		this.name = name;
		this.type = type;
		this.availablity = availability;
	}
}
