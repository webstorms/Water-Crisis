package com.dis.environment;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;

import com.dis.agent.Agent;
import com.esotericsoftware.minlog.Log;

public class Environment {

	public static final int STATIONARY_PERIOD = 100;

	public static final int STATIONARY_ENVIRONMENT_AMOUNT = 4;
	public static final int DAMS_AMOUNT = 8;

	private static final int DAM_THRESHOLD = 10;

	private boolean stationary;
	private int environmentType;

	private Agent agent;
	private Dam[] dams;
	private int[] systemActionVector;
	private int tick;

	public Environment() {
		
		// Set up the dams and their respective dynamics
		this.dams = new Dam[DAMS_AMOUNT];
		for(int i = 0; i < DAMS_AMOUNT; i++) dams[i] = new Dam(getDamDistribution(i));
		this.systemActionVector = new int[DAMS_AMOUNT];
		
	}
	
	public void setEnvironmentType(Integer environmentType) {
		this.environmentType = environmentType;
		
	}
	
	public int getStationaryEnvironmentType() {
		return this.environmentType;

	}

	/**
	 * Get the dynamics associated with the various stationary environments for a single dam.
	 * The dam will have declining dynamics for every stationary environment except for the 
	 * stationary environment associated with the supplied index.
	 * @param index
	 * @return DamDistribution[] containing the various distributions for each stationary environment
	 */

	private DamDistribution[] getDamDistribution(int shift) {
		DamDistribution[] paramters = new DamDistribution[STATIONARY_ENVIRONMENT_AMOUNT];
		paramters[0] = Dynamics.increaseDynamics;
		paramters[1] = Dynamics.nonchangingDynamics;
		paramters[2] = Dynamics.decreasingDynamics;
		paramters[3] = Dynamics.doubledecreasingDynamics;
		
		DamDistribution[] shiftedParamters = new DamDistribution[STATIONARY_ENVIRONMENT_AMOUNT]; 
		for(int i = 0; i < STATIONARY_ENVIRONMENT_AMOUNT; i++) {                
			shiftedParamters[i] = paramters[(i + shift) % STATIONARY_ENVIRONMENT_AMOUNT];
			
		}
		
		return shiftedParamters;
		
	}

	public void setAgent(Agent agent) {
		this.agent = agent;

	}

	public void setStationary(boolean stationary) {
		this.stationary = stationary;

	}

	/**
	 * Put the environemt into an initial state.
	 */

	public void sampleInitialState() {
		this.agent.sampleInitialState();
		for(int i = 0; i < DAMS_AMOUNT; i++) this.dams[i].sampleInitialState(this.environmentType);

	}

	public int agentAct() {
		return this.agent.act();

	}

	/**
	 * Advance the simulation by one time step.
	 */

	public void step() {

		for(int i = 0; i < DAMS_AMOUNT; i++) {
			// Gather the rain and water consumption of evey dam
			int rain = this.dams[i].getRain(environmentType);
			int waterConsumption = this.dams[i].getWaterConsumption(environmentType);
			// Encode both variables into a single row in the system action vector
			this.systemActionVector[i] = rain - waterConsumption;

		}

		// Check weather to alter the environmental dynamics
		if(!this.stationary) {
			this.tick++;
			if(this.tick == Environment.STATIONARY_PERIOD) {
				String log = "Switching from dynamics " + this.environmentType + " to ";
				UniformIntegerDistribution uniform = new UniformIntegerDistribution(0, Environment.STATIONARY_ENVIRONMENT_AMOUNT - 1);
				this.environmentType = uniform.sample();
				this.tick = 0;
				log += this.environmentType;
				Log.info(this.getClass().getName(), log);

			}

		}

	}

	/**
	 * Get the reward which is the count of all dams with water levels above the dam threshold.
	 * @return reward
	 */

	public float getReward() {
		int reward = 0;
		for(Dam dam : this.dams) reward += dam.getStorage() >= Environment.DAM_THRESHOLD ? 1 : -1;
		
		// Normalize
		reward /= 1f;
		
		return reward;

	}

	/**
	 * Get the current state of the environment.
	 * @return State of the environment
	 */

	public float[] getState() {
		float[] state = new float[Environment.DAMS_AMOUNT + 2];
		for(int i = 0; i < DAMS_AMOUNT; i++) state[i] = this.dams[i].getStorage() / 100f;
		state[DAMS_AMOUNT] = this.agent.getStorage() / 100f;
		state[DAMS_AMOUNT + 1] = this.agent.getDamLocation() / ((float) DAMS_AMOUNT - 1);

		return state;

	}

	/**
	 * Get the action vector of the system which is of size DAMS_AMOUNT.
	 * @return
	 */

	public int[] getSystemActionVector() {
		return this.systemActionVector;

	}

	/**
	 * Get the dam located at location damLocation.
	 * @param damLocation location of the dam
	 * @return Dam
	 */

	public Dam getDam(int damLocation) {
		return this.dams[damLocation];

	}

	@Override
	public String toString() {
		String output = "State: ";
		output += "System Vector: ";
		for(int a : this.getSystemActionVector()) output += " " + a;
		output += "\n";
		for(float a : this.getState()) output += " " + a;
		output += "Reward: " + this.getReward();
		return output;

	}


}