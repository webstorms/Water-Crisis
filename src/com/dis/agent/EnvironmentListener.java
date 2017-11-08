package com.dis.agent;

import com.dis.simulation.Tuple;

public interface EnvironmentListener {

	public void onActionExecuted(Tuple tuple);
	public void onEpisodeCompleted();
	
}
