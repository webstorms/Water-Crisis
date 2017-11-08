package com.dis.agent.implementations.training;

import org.apache.commons.math3.stat.Frequency;

import com.dis.agent.Agent;
import com.dis.environment.Environment;
import com.dis.simulation.Tuple;

public class ActionVectorAgent extends Agent {
	
	private Frequency[] aproxDist;
	
	public ActionVectorAgent(Environment environment, Frequency[] aproxDist) {
		super(environment);
		this.aproxDist = aproxDist;
		
	}

	public Frequency[] getActionVector() {
		return this.aproxDist;
		
	}
	
	@Override
	public void onActionExecuted(Tuple tuple) {
		for(int i = 0; i < Environment.DAMS_AMOUNT; i++) this.aproxDist[i].addValue(tuple.getEnvironmentAction()[i]);
		
	}
	
	@Override
	public int getAction(float[] state, int[] environmentActionVector) {
		return 0;
		
	}

	@Override
	public void onEpisodeCompleted() {
	}
	
	
}