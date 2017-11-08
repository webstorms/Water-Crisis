package com.dis.agent;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;

import com.dis.environment.Environment;
import com.dis.simulation.Statistics;
import com.dis.simulation.Tuple;

public abstract class Agent implements EnvironmentListener {

	public static final int ACTION_COLLECT = Environment.DAMS_AMOUNT;
	public static final int ACTION_DROP_OFF = Environment.DAMS_AMOUNT + 1;
	private static final int STORAGE_CAPACITY = 20;
	
	protected Statistics stats;
	protected Environment environment;
	
	private int damLocation;
	private int storage;
	private int meanInitial = 10;
	private int sdInitial = 3;
	
	public Agent(Environment environment) {
		this.environment = environment;
		this.stats = new Statistics();
		
	}

	public void sampleInitialState() {
		UniformIntegerDistribution uniform = new UniformIntegerDistribution(0, Environment.DAMS_AMOUNT - 1);
		this.damLocation = uniform.sample();

		RealDistribution normal =  new NormalDistribution(this.meanInitial, this.sdInitial);
		this.storage = (int) Math.round(normal.sample());
		// Ensure the storage is within the bounds [0, STORAGE_CAPACITY]
		this.storage = Math.min(this.storage, Agent.STORAGE_CAPACITY);
		this.storage = Math.max(this.storage, 0);
		
	}

	public int getActionSize() {
		return Environment.DAMS_AMOUNT + 2;
		
	}
	
	/**
	 * This method is to be overriden and supply the necessary decision making in regards to action
	 * that is to be taken
	 * @param state The current state
	 * @param environmentActionVector The environment action vector emitted during the previous environment tick
	 * @return
	 */
	
	public abstract int getAction(float[] state, int[] environmentActionVector);

	public int act() {
		int action = getAction(this.environment.getState(), this.environment.getSystemActionVector());
		if(action == Agent.ACTION_COLLECT) storage += this.environment.getDam(this.damLocation).decrease(STORAGE_CAPACITY - storage);
		else if(action == Agent.ACTION_DROP_OFF) storage -= this.environment.getDam(this.damLocation).increase(storage);
		else damLocation = action;
		
		return action;

	}
	
	@Override
	public void onActionExecuted(Tuple tuple) {
		this.stats.addReward(tuple.getReward());
		
	}

	@Override
	public void onEpisodeCompleted() {
		stats.episodeComplete();
		
	}

	public int getDamLocation() {
		return this.damLocation;

	}

	public int getStorage() {
		return this.storage;
	}

	public Statistics getStats() {
		return this.stats;
		
	}


}