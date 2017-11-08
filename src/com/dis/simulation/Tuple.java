package com.dis.simulation;

import java.util.Arrays;

public class Tuple {

	private float[] state;
	private int action;
	private int[] environmentActionVector;
	private float[] nextState;
	private float reward;
	private int stationaryEnvironmentID;
	
	public Tuple(float[] state, int action, int[] environmentActionVector, float[] nextState, float reward, int stationaryEnvironmentID) {
		this.state = state;
		this.action = action;
		this.environmentActionVector = environmentActionVector;
		this.nextState = nextState;
		this.reward = reward;
		this.stationaryEnvironmentID = stationaryEnvironmentID;
		
	}
	
	public float[] getState() {
		return this.state;
		
	}
	
	public int getAction() {
		return this.action;
		
	}
	
	public int[] getEnvironmentAction() {
		return this.environmentActionVector;
		
	}
	
	public float[] getNewState() {
		return this.nextState;
		
	}
	
	public float getReward() {
		return this.reward;
		
	}
	
	public int getStationaryEnvironmentID() {
		return this.stationaryEnvironmentID;
		
	}

	@Override
	public String toString() {
		String output = "State: " + Arrays.toString(this.state) + "\n";
		output += "Action: " + this.action + "\n";
		output += "Environment Action: " + Arrays.toString(this.environmentActionVector) + "\n";
		output += "New State: " + Arrays.toString(this.nextState) + "\n";
		output += "Reward: " + this.reward + "\n";
		return output;
		
	}
	
	
}