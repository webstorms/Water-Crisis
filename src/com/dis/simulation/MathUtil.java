package com.dis.simulation;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.Frequency;
import org.nd4j.linalg.api.ndarray.INDArray;

public class MathUtil {

	public static int argMax(INDArray array) {
		float maxVal = Integer.MIN_VALUE;
		int maxIndex = 0;
		for(int i = 0; i < array.length(); i++) {
			if(array.getFloat(0, i) > maxVal) {
				maxVal = array.getFloat(0, i);
				maxIndex = i;

			}

		}
		return maxIndex;

	}

	public static float getMaxValue(INDArray array) {
		float maxVal = Integer.MIN_VALUE;
		for(int i = 0; i < array.length(); i++) {
			if(array.getFloat(0, i) > maxVal) maxVal = array.getFloat(0, i);

		}

		return maxVal;

	}

	public static double getKL(float[] p1, float[] p2) {
		double klDiv = 0.0;
		for(int i = 0; i < p1.length; ++i) {
			if(p1[i] == 0) { continue; }
			if(p2[i] == 0.0) { continue; }
			klDiv += p1[i] * Math.log(p1[i] / p2[i]);
			
		}
		return (double) (klDiv / Math.log(2));

	}

	public static List<Integer> getDomain(Frequency frequency) {
		List<Integer> domain = new ArrayList<Integer>();
		Iterator<Entry<Comparable<?>, Long>> iterator = frequency.entrySetIterator();
		while(iterator.hasNext()) domain.add(((Long) iterator.next().getKey()).intValue());
		return domain;

	}

	public static float[] frequencyToFloatArray(Frequency frequency) {
		List<Integer> domain = MathUtil.getDomain(frequency);
		float[] data = new float[domain.size()];
		for(int i = 0; i < domain.size(); i++) data[i] = (float) frequency.getPct(domain.get(i));
		
		return data;

	}
	
	public static long factorial(int k) {
		long a = 1;
		for(int i = 1; i <= k; i++) a *= i;
		return a;
		
	}
	
	public static String round(float num) {
		DecimalFormat df = new DecimalFormat("#.####");
		df.setRoundingMode(RoundingMode.CEILING);
		return df.format(num);
		
	}


}
