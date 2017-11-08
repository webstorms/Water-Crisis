package com.dis.agent.implementations.training;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;

import com.dis.agent.Agent;
import com.dis.environment.Environment;
import com.dis.simulation.MathUtil;
import com.dis.simulation.Tuple;

public class ANNAgent extends Agent {
	
	private static final int REPLAY_MEMORY_SIZE = 1000;
	
	private Queue<Tuple> memoryReplay;
	private float eps = 1f;
	private float epsMin = 0.1f;
	private float epsDecay = 0.99f;
	private float gamma = 0.9f;
	private DeepNet net;
	
	private String name;
	
	private UniformIntegerDistribution uniformOverActions;
	private UniformRealDistribution uniformOverOne;
	
	public ANNAgent(Environment environment, String name, MultiLayerNetwork ann) {
		super(environment);
		this.name = name;
		this.memoryReplay = new LinkedList<Tuple>();
		this.net = new DeepNet(ann, this.gamma);
		
		this.uniformOverActions = new UniformIntegerDistribution(0, this.getActionSize() - 1);
		this.uniformOverOne = new UniformRealDistribution();
		
		
	}

	@Override
	public void onActionExecuted(Tuple tuple) {
		super.onActionExecuted(tuple);
		float maxQ = MathUtil.getMaxValue(this.net.getQValues(tuple.getState()));
		this.stats.addQVal(maxQ);
		
		// Preprocess tuple and store it in the memory replay
		if(this.memoryReplay.size() == REPLAY_MEMORY_SIZE) this.memoryReplay.poll();
		this.memoryReplay.add(tuple);
		
		// Train net
		this.net.train(this.memoryReplay);
		
	}
	
	@Override
	public int getAction(float[] state, int[] environmentActionVector) {
		// With random probability eps select random action
		if(uniformOverOne.sample() <= this.eps) {
			return this.uniformOverActions.sample();
			
		}
		// Otherwise select action = maxQ(state, action)
		else {
			INDArray qValues = this.net.getQValues(state);
			return MathUtil.argMax(qValues);
			
		}		
		
	}

	@Override
	public void onEpisodeCompleted() {
		super.onEpisodeCompleted();
		this.eps *= this.epsDecay;
		if(this.eps < this.epsMin) this.eps = this.epsMin;
		this.net.save(name);
		System.out.println("New Eps: " + this.eps);
		System.out.println("STATE: " + Arrays.toString(this.environment.getState()));
		System.out.println("QVALS: " + this.net.getQValues(this.environment.getState()));
		
	}
	
	
}