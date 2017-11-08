package com.dis.agent.implementations.testing;
import com.dis.agent.Agent;
import com.dis.agent.implementations.training.DeepNet;
import com.dis.environment.Environment;
import com.dis.simulation.MathUtil;
import com.dis.simulation.Tuple;

public class NonStationaryAgent extends Agent {

	private DeepNet policy;
	
	public NonStationaryAgent(Environment environment, DeepNet policy) {
		super(environment);
		this.policy = policy;
		
	}

	@Override
	public int getAction(float[] state, int[] environmentActionVector) {
		return MathUtil.argMax(this.policy.getQValues(state));
		
	}
	
	@Override
	public void onActionExecuted(Tuple tuple) {
		super.onActionExecuted(tuple);
		
		float maxQ = MathUtil.getMaxValue(this.policy.getQValues(tuple.getState()));
		this.stats.addQVal(maxQ);

	}
	
	
}