package com.dis.environment;

public class Dam {

	private static final int STORAGE_CAPACITY = 100;

	private int storage;
	private DamDistribution[] distributions;

	public Dam(DamDistribution...distributions) {
		this.distributions = distributions;
		
		
	}
	
	public DamDistribution getD(int index) {
		return this.distributions[index];
		
	}

	public void sampleInitialState(int index) {
		this.storage = (int) Math.round(this.distributions[index].getInitialStorage().sample());
		// Ensure the storage is within the bounds [0, STORAGE_CAPACITY]
		this.storage = Math.min(this.storage, Dam.STORAGE_CAPACITY);
		this.storage = Math.max(this.storage, 0);
		
	}
	
	public int decrease(int amount) {
		int decrease;
		if(storage >= amount) decrease = amount;
		else decrease = storage;
		storage -= decrease;
		return decrease;

	}

	public int increase(int amount) {
		int increase;
		if(storage + amount <= STORAGE_CAPACITY) increase = amount;
		else increase = STORAGE_CAPACITY - storage;
		storage += increase;
		return increase;

	}

	public int getStorage() {
		return this.storage;

	}

	public int getRain(int index) {
		int rain = this.distributions[index].getRainDistribution().sample();
		increase(rain);
		return rain;

	}

	public int getWaterConsumption(int index) {
		int consumption = this.distributions[index].getWaterConsumptionDistribution().sample();
		decrease(consumption);
		return consumption;

	}


}