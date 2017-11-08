package com.dis.agent.implementations.testing;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;

import com.dis.agent.Agent;
import com.dis.environment.Environment;
import com.dis.simulation.Tuple;
import com.esotericsoftware.minlog.Log;

public class RandomAgent extends Agent {

	private UniformIntegerDistribution uniform;
	
	public RandomAgent(Environment environment) {
		super(environment);
		this.uniform = new UniformIntegerDistribution(0, this.getActionSize() - 1);
		
	}

	@Override
	public void onActionExecuted(Tuple tuple) {
		super.onActionExecuted(tuple);
		Log.info(this.getClass().getName(), tuple.toString());
		
	}

	@Override
	public int getAction(float[] state, int[] environmentActionVector) {
		return this.uniform.sample();
		
	}
	
	
}