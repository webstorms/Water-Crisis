package com.dis.agent.implementations.training;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import com.dis.environment.Environment;
import com.dis.simulation.IOUtil;
import com.dis.simulation.MathUtil;
import com.dis.simulation.Tuple;

public class DeepNet {

	private static final int MINIBATCH_SIZE = 32;
	
	private MultiLayerNetwork network;
	private float gamma;
	
	public DeepNet(MultiLayerNetwork network) throws Exception {
		if(network == null) throw new Exception("FACK");
		this.network = network;
		
	}
	
	public DeepNet(MultiLayerNetwork network, float gamma) {
		this.network = (network != null) ? network : this.getNetwork(Environment.DAMS_AMOUNT + 2, Environment.DAMS_AMOUNT + 2);
		this.gamma = gamma;
		
	}
	
	public void train(Queue<Tuple> memoryReplay) {
		if(memoryReplay.size() < DeepNet.MINIBATCH_SIZE) return;
		DataSetIterator iterator = getMinibatch((List<Tuple>) memoryReplay);
		this.network.fit(iterator);
		
	}
	
	public INDArray getQValues(float[] newState) {
		INDArray in = Nd4j.create(newState);
		INDArray out = Nd4j.create(1, 1).addi(1);
		DataSet next = new DataSet(in, out);
		INDArray output = this.network.output(next.getFeatureMatrix());
		
		return output;
		
	}
	
	public void save(String name) {
		IOUtil.writeModel(this.network, name);
		
	}
	
	private DataSetIterator getMinibatch(List<Tuple> tuples) {
		// Uniformly generate unique MINIBATCH_SIZE indices
		Set<Integer> set = new HashSet<Integer>();
		UniformIntegerDistribution uniform = new UniformIntegerDistribution(0, tuples.size() - 1);
		while(set.size() != DeepNet.MINIBATCH_SIZE) set.add(uniform.sample());
		
		// Create DataSet
		List<DataSet> dataset = new ArrayList<DataSet>();
		for(int index : set) {
			
			// Input
			INDArray in = Nd4j.create(tuples.get(index).getState());
			
			INDArray qValues = getQValues(tuples.get(index).getNewState());
			float reward = tuples.get(index).getReward() + gamma * MathUtil.getMaxValue(qValues);
			
			// Output
			INDArray out = getQValues(tuples.get(index).getState());
			out = out.putScalar(tuples.get(index).getAction(), reward);
			
			dataset.add(new DataSet(in, out));
			
		}

		return new ListDataSetIterator(dataset, DeepNet.MINIBATCH_SIZE);
		
	}
	
	private MultiLayerNetwork getNetwork(int inSize, int outSize) {
		MultiLayerNetwork foo = new MultiLayerNetwork(new NeuralNetConfiguration.Builder()
				.seed(123)
				.iterations(1)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.learningRate(0.01)
				.weightInit(WeightInit.XAVIER)
				.updater(new Nesterovs(0.9))
			//	.regularization(true).l2(0.0015 * 0.005) 
				.list()
				.layer(0, new DenseLayer.Builder().nIn(inSize).nOut(20)
						.activation(Activation.RELU)
						.build())
				.layer(1, new DenseLayer.Builder().nOut(20)
						.activation(Activation.RELU)
						.build())
				.layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
						.activation(Activation.IDENTITY)
						.nOut(outSize).build())
				.pretrain(false).backprop(true).build());
		foo.init();
		return foo;
		
	}
	
	
}