package com.dis.simulation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class Statistics {

	private SummaryStatistics rewardStatsGlobal;
	private SummaryStatistics switchStatsGlobal;
	private SummaryStatistics qValueStatsGlobal;
	
	private SummaryStatistics rewardStats;
	private SummaryStatistics switchStats;
	private SummaryStatistics qValueStats;

	private List<StatsData> rewardData;
	private List<StatsData> switchData;
	private List<StatsData> qValueData;
	
	public Statistics() {
		this.rewardStatsGlobal = new SummaryStatistics();
		this.switchStatsGlobal = new SummaryStatistics();
		this.qValueStatsGlobal = new SummaryStatistics();
		
		this.rewardStats = new SummaryStatistics();
		this.switchStats = new SummaryStatistics();
		this.qValueStats = new SummaryStatistics();
		
		this.rewardData = new ArrayList<StatsData>();
		this.switchData = new ArrayList<StatsData>();
		this.qValueData = new ArrayList<StatsData>();
		
	}
	
	public void addReward(double value) {
		this.rewardStatsGlobal.addValue(value);
		this.rewardStats.addValue(value);
		
	}
	
	public void addSwitch(double value) {
		this.switchStatsGlobal.addValue(value);
		this.switchStats.addValue(value);
		
	}
	
	public void addQVal(double value) {
		this.qValueStatsGlobal.addValue(value);
		this.qValueStats.addValue(value);
		
	}
	
	public SummaryStatistics getReward() {
		return this.rewardStatsGlobal;
		
	}
	
	public SummaryStatistics getSwitch() {
		return this.switchStatsGlobal;
		
	}
	
	public SummaryStatistics getQValue() {
		return this.qValueStatsGlobal;
		
	}
	
	public List<StatsData> getRewardData() {
		return this.rewardData;
		
	}
	
	public List<StatsData> getSwtichData() {
		return this.switchData;
		
	}
	
	public List<StatsData> getQValueData() {
		return this.qValueData;
		
	}
	
	public void episodeComplete() {
		// Add the necessary data from the episode
		this.rewardData.add(new StatsData((float) this.rewardStats.getMean(), (float) this.rewardStats.getStandardDeviation()));
		this.switchData.add(new StatsData((float) this.switchStats.getMean(), (float) this.switchStats.getStandardDeviation()));
		this.qValueData.add(new StatsData((float) this.qValueStats.getMean(), (float) this.qValueStats.getStandardDeviation()));
		// Clear the statistics loggers
		this.rewardStats.clear();
		this.switchStats.clear();
		this.qValueStats.clear();
		
	}
	
	
}