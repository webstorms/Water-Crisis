package com.dis.simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.Frequency;

import com.dis.agent.Agent;
import com.dis.agent.implementations.testing.BayesianAgent;
import com.dis.agent.implementations.testing.NonStationaryAgent;
import com.dis.agent.implementations.testing.RandomAgent;
import com.dis.agent.implementations.training.ANNAgent;
import com.dis.agent.implementations.training.ActionVectorAgent;
import com.dis.agent.implementations.training.DeepNet;
import com.dis.environment.Dynamics;
import com.dis.environment.Environment;
import com.esotericsoftware.minlog.Log;

public class Simulator {
	
	// Simulation paramters
	private static final int TRAINING_EPISODES = 500;
	private static final int TESTING_EPISODES = 100;
	private static final int EPISODE_PERIOD = Environment.STATIONARY_PERIOD;
	
	// File name paramters
	public static final String DISTRIBUTIONS_FILE = "distributions_file";
	public static final String STATIONARY_NETWORK_FILE = "stationary_network_file";
	public static final String NONSTATIONARY_NETWORK_FILE = "nonstationary_network_file";
	
	private Environment environment;
	
	public Simulator() {
		//Log.NONE(); // Turn logging off
		this.environment = new Environment();
		
	}

	public static void main(String[] args) throws Exception {
		Simulator simulator = new Simulator();
		
		// Training
		// Approximate the distributions
		// simulator.approximateDistributions();
	//	simulator.trainPolicy("Stationary_Training", true, Simulator.STATIONARY_NETWORK_FILE);
	//	simulator.trainPolicy("NonStationary_Training", false, Simulator.NONSTATIONARY_NETWORK_FILE);
		
		// Testing
		Agent agent = new RandomAgent(simulator.environment);
		simulator.deploy("RANDOM", agent);
		
		DeepNet deepNet = new DeepNet(IOUtil.readModel(NONSTATIONARY_NETWORK_FILE));
		agent = new NonStationaryAgent(simulator.environment, deepNet);
		simulator.deploy("NONSTATIONARY", agent);
		
		deepNet = new DeepNet(IOUtil.readModel(STATIONARY_NETWORK_FILE));
		agent = new BayesianAgent(simulator.environment, IOUtil.load(Simulator.DISTRIBUTIONS_FILE), deepNet);
		simulator.deploy("STATIONARY", agent);
		
//		DeepNet deepNet = new DeepNet(IOUtil.readModel(STATIONARY_NETWORK_FILE));
//		Agent agent = new NonStationaryAgent(simulator.environment, deepNet);
//		simulator.deploy("TEST", agent);
		
	//	statistics = simulator.deploy(true);
	//	simulator.deploy(true);
	//	System.out.println(statistics.getRewardData().get(0).getMean());
//		RandomAgent agent = new RandomAgent(simulator.environment);
//		simulator.simulate(TESTING_EPISODES, true, 0, agent);
//		System.out.println(statistics.getActionData());
		
	//	DeepNet deepNet = new DeepNet(IOUtil.readModel(STATIONARY_NETWORK_FILE));
	//	BayesianAgent agent2 = new BayesianAgent(simulator.environment, IOUtil.load(Simulator.DISTRIBUTIONS_FILE), deepNet);
	//	simulator.deploy("Bayesian", agent2);
	//	simulator.simulate(TESTING_EPISODES, true, 0, agent2);
		// System.out.println(agent2.getStats().getRewardData());
		
	}
	
	private void approximateDistributions() {
		// Gather approximated distributions through simuluations
		Frequency[][] aproxDist = null;//IOUtil.load(Simulator.DISTRIBUTIONS_FILE);
		if(aproxDist == null) {
			aproxDist = new Frequency[Environment.STATIONARY_ENVIRONMENT_AMOUNT][Environment.DAMS_AMOUNT];
			for(int i = 0; i < Environment.STATIONARY_ENVIRONMENT_AMOUNT; i++) {
				for(int j = 0; j < Environment.DAMS_AMOUNT; j++) aproxDist[i][j] = new Frequency();
				
			}
			
		}
		for(int i = 0; i < Environment.STATIONARY_ENVIRONMENT_AMOUNT; i++) {
			ActionVectorAgent agent = new ActionVectorAgent(this.environment, aproxDist[i]);
			this.simulate(TRAINING_EPISODES, true, i, agent);
			aproxDist[i] = agent.getActionVector();
			
		}
		
		// Save frequency tables such that it can be used by the bayesian agent
		IOUtil.save(aproxDist, DISTRIBUTIONS_FILE);
		
		// Print out stats regarding the captured distribution 
		// (only for first stationary environment, others are just permutations of it)
		float[][] aprox = new float[Environment.DAMS_AMOUNT][];
		float[][] real = new float[Environment.DAMS_AMOUNT][];
		for(int i = 0; i < Environment.DAMS_AMOUNT; i++) {
			List<Integer> domain = MathUtil.getDomain(aproxDist[0][i]);
			// The real and approximated distrbution for dam i
			Dynamics dynamics = null;
			if(i == 0) dynamics = Dynamics.increaseDynamics;
			else if(i == 1) dynamics = Dynamics.doubledecreasingDynamics;
			else if(i == 2) dynamics = Dynamics.decreasingDynamics;
			else if(i == 3) dynamics = Dynamics.doubledecreasingDynamics;
			real[i] = Dynamics.getConvolution(dynamics, domain);
			aprox[i] = MathUtil.frequencyToFloatArray(aproxDist[0][i]);
			System.out.println(Arrays.toString(real[i]));
			System.out.println(Arrays.toString(aprox[i]));
			
		}
		this.writeOutDistribution("distributions_stats", real, aprox);
		
	}
	
	private void trainPolicy(String experimentName, boolean stationary, String filename) {
		ANNAgent agent = new ANNAgent(this.environment, filename, IOUtil.readModel(filename));
		this.simulate(TRAINING_EPISODES, stationary, 0, agent);
		writeOutStats(experimentName, agent.getStats());
		
	}

	private void deploy(String experimentName, Agent agent) {
		this.simulate(TESTING_EPISODES, false, 0, agent);
		writeOutStats(experimentName, agent.getStats());
		
	}
	
	public void writeOutDistribution(String name, float[][] real, float[][] aprox) {
		String output = "action, real, aprox\n";
			
		for(int i = 0; i < aprox.length; i++) {
			output += MathUtil.getKL(real[i], aprox[i]) + "\n";
			for(int j = 0; j < aprox[i].length; j++) output += j + ", " + MathUtil.round(real[i][j]) + ", " + MathUtil.round(aprox[i][j]) + "\n";
			
		}
		
		IOUtil.writeText(name + "_distribution.csv", output);
		
	}
	
	public void writeOutStats(String name, Statistics stats) {
		
		// Create summary statistics
		String output = "Reward: " + stats.getReward().getMean() + " " + stats.getReward().getStandardDeviation() + "\n";
		output += "Switch: " + stats.getSwitch().getMean() + " " + stats.getSwitch().getStandardDeviation() + "\n";
		output += "QValue: " + stats.getQValue().getMean() + " " + stats.getQValue().getStandardDeviation() + "\n";
		
		IOUtil.writeText(name + "_summary.csv", output);
		
		// Create tabular statistics
		output = "index, rewardMean, rewardSD, qValueMean, qValueSD\n";
		for(int i = 0; i < stats.getRewardData().size(); i++) {
			StatsData reward = stats.getRewardData().get(i);
			StatsData qValue = stats.getQValueData().get(i);
			output += i + ", " + MathUtil.round(reward.getMean()) + "," + MathUtil.round(reward.getSD()) + ", " + MathUtil.round(qValue.getMean()) + ", " + MathUtil.round(qValue.getSD()) + "\n";
			
		}
		
		IOUtil.writeText(name + "_tabular.csv", output);
		
	}
	
	public void simulate(int episodes, boolean stationary, int environmentType, Agent agent) {

		// Set environmental paramters before the simulation
		this.environment.setStationary(stationary);
		this.environment.setEnvironmentType(environmentType);
		this.environment.setAgent(agent);

		// Iterate over the supplied amount of episodes
		for(int i = 0; i < episodes; i++) {
			Log.info(this.getClass().getName(), "New Episode " + i);

			// Sample the initial state from the environment
			environment.sampleInitialState();
			
			// Simulate a single "game" i.e. the dynamics over a STATIONARY_PERIOD amount of time
			for(int j = 0; j < EPISODE_PERIOD; j++) {
				float[] state = this.environment.getState();
				int action = agent.act();
				this.environment.step();
				int[] environmentAction = this.environment.getSystemActionVector();
				float[] newState = this.environment.getState();
				float reward = this.environment.getReward();
				Tuple tuple = new Tuple(state, action, environmentAction, newState, reward, this.environment.getStationaryEnvironmentType());
				agent.onActionExecuted(tuple);
				
			}
			
			agent.onEpisodeCompleted();
			
		}

	}


}