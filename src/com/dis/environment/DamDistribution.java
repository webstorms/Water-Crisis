package com.dis.environment;

import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

public abstract class DamDistribution {

	public abstract IntegerDistribution getRainDistribution();

	public abstract IntegerDistribution getWaterConsumptionDistribution();

	public abstract RealDistribution getInitialStorage();
	
	
}