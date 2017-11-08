package com.dis.environment;

import java.util.List;

import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

import com.dis.simulation.MathUtil;

public class Dynamics extends DamDistribution {

	public static final Dynamics increaseDynamics = new Dynamics(8, 2);
	public static final Dynamics nonchangingDynamics = new Dynamics(3, 3);
	public static final Dynamics decreasingDynamics = new Dynamics(1, 3);
	public static final Dynamics doubledecreasingDynamics = new Dynamics(1, 5);
	
	private int lamdaRain;
	private int lamdaWaterConsumption;
	private int meanInitialStorage = 50;
	private int sdInitialStorage = 5;
	
	public Dynamics(int lamdaRain, int lamdaWaterConsumption) {
		this.lamdaRain = lamdaRain;
		this.lamdaWaterConsumption = lamdaWaterConsumption;
		
	}
	
	@Override
	public IntegerDistribution getRainDistribution() {
		return new PoissonDistribution(this.lamdaRain);
		
	}

	@Override
	public IntegerDistribution getWaterConsumptionDistribution() {
		return new PoissonDistribution(this.lamdaWaterConsumption);
		
	}

	@Override
	public RealDistribution getInitialStorage() {
		return new NormalDistribution(this.meanInitialStorage, this.sdInitialStorage);
		
	}
	
	public static float[] getConvolution(Dynamics dynamics, List<Integer> domain) {
		float[] data = new float[domain.size()];
		for(int i = 0; i < data.length; i++) {
			double propability = 0;
			int k = domain.get(i);
			
			for(int n = Math.max(0, -k); n < 6; n++) {
				long denom1 = MathUtil.factorial(n);
				long denom2 =  MathUtil.factorial(k+n);
				if(denom1 > 0 && denom2 > 0 && denom1*denom2 > 0) propability += (Math.pow(dynamics.lamdaRain, k + n) * Math.pow(dynamics.lamdaWaterConsumption, n)) / (denom1*denom2);
				
				
			}
			data[i] = (float) (Math.exp(-dynamics.lamdaRain-dynamics.lamdaWaterConsumption) * propability);
			
		}
		
		return data;
		
	}
	
	
}