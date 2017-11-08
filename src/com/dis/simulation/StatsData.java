package com.dis.simulation;

public class StatsData {

	private float mean;
	private float sd;
	
	public StatsData(float mean, float sd) {
		this.mean = mean;
		this.sd = sd;
		
	}
	
	public float getMean() {
		return this.mean;
		
	}
	
	public float getSD() {
		return this.sd;
		
	}

	@Override
	public String toString() {
		return mean + "+" + sd;
		
	}
	
	
	
	
}