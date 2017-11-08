package com.dis.simulation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.math3.stat.Frequency;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

public class IOUtil {

	public static void save(Frequency[][] data, String filename) {
		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
			oos.writeObject(data);

		} 
		catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static Frequency[][] load(String filename) {
		Frequency[][] data = null;

		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
			data = (Frequency[][] ) ois.readObject();

		} 
		catch(Exception ex) {
			ex.printStackTrace();
			return null;

		}

		return data;

	}

	public static MultiLayerNetwork readModel(String filename) {
		try {
			return ModelSerializer.restoreMultiLayerNetwork(new File(filename));
		} 
		catch (IOException e) {
			return null;

		}

	}

	public static void writeModel(MultiLayerNetwork model, String filename) {
		boolean saveUpdater = true;
		try {
			ModelSerializer.writeModel(model, new File(filename), saveUpdater);
		} 
		catch (IOException e) {
			e.printStackTrace();

		}

	}

	public static void writeText(String name, String content) {
		try(Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(name), "utf-8"))) {
			writer.write(content);
		} catch (Exception e) {
			
		}

	}
	

}