package com.dis.agent.implementations.testing;

import java.util.Arrays;

import org.apache.commons.math3.stat.Frequency;

import com.dis.agent.Agent;
import com.dis.agent.implementations.training.DeepNet;
import com.dis.environment.Environment;
import com.dis.simulation.MathUtil;
import com.dis.simulation.Tuple;

public class BayesianAgent extends Agent {

	private Frequency[][] distributions;
	private int estimatedEnvironmentType;
	private DeepNet policy;

	public BayesianAgent(Environment environment, Frequency[][] distributions, DeepNet policy) {
		super(environment);
		this.distributions = distributions;
		this.policy = policy;

	}

	@Override
	public void onActionExecuted(Tuple tuple) {
		super.onActionExecuted(tuple);

		// Switch the policy to the most suitable stationary environment
		int[] environmentActionVector = tuple.getEnvironmentAction();
		int maxIndex = 0;
		double maxProbability = 0;
		for(int i = 0; i < Environment.STATIONARY_ENVIRONMENT_AMOUNT; i++) {
			double probability = this.getProbability(i, environmentActionVector);
			if(probability > maxProbability) {
				maxProbability = probability;
				maxIndex = i;

			}

		}
		this.stats.addSwitch(maxIndex == environment.getStationaryEnvironmentType() ? 1 : 0);
		this.estimatedEnvironmentType = maxIndex;

	}

	@Override
	public int getAction(float[] state, int[] environmentActionVector) {
		// Greedely choose the action associated with the largest Q-Value
		int action = MathUtil.argMax(this.policy.getQValues(transformState(this.estimatedEnvironmentType, state)));
		System.out.println("BEFORE: " + Arrays.toString(state));
		System.out.println("AFTER: " + Arrays.toString(transformState(this.estimatedEnvironmentType, state)));
		System.out.println("ACTION B: " + action);
		// "Rotate" the action depending on the current estimated environment
		if(action != Agent.ACTION_COLLECT && action != Agent.ACTION_DROP_OFF) {
			action -= this.estimatedEnvironmentType;
			if(action < 0) action += Environment.DAMS_AMOUNT;
			// if(action < 0) action += Environment.DAMS_AMOUNT;
			
		}
		System.out.println("ENV: " + estimatedEnvironmentType);
		System.out.println("ACTION A: " + action);

		float maxQ = MathUtil.getMaxValue(this.policy.getQValues(transformState(this.estimatedEnvironmentType, state)));
		this.stats.addQVal(maxQ);
		
		return action;

	}

	private float[] transformState(int env, float[] state) {
		float[] transformedState = new float[state.length];
		for(int i = 0; i < state.length - 2; i++) {   
			int index = i - env;
			if(index < 0) index += state.length - 2;
			transformedState[i] = state[index];
			
		}
		transformedState[state.length - 2] = state[state.length - 2];
		transformedState[state.length - 1] = ((state[state.length - 1] * (Environment.DAMS_AMOUNT - 1) + env) % Environment.DAMS_AMOUNT) / (Environment.DAMS_AMOUNT - 1);
		return transformedState;
		
	}

	private double getProbability(int environmentType, int[] environmentActionVector) {
		// No need to compute denominator as it is the same for all comparisons
		
		double probability = 1;
		for(int i = 0; i < this.getActionSize() - 2; i++) {
			//	System.out.println(this.distributions[environmentType][i].getPct(environmentActionVector[i]));
			probability *= this.distributions[environmentType][i].getPct(environmentActionVector[i]);

		}

		return probability;

	}

	@Override
	public void onEpisodeCompleted() {
		super.onEpisodeCompleted();

	}


}