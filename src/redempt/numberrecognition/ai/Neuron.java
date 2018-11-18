package redempt.numberrecognition.ai;

import java.util.Random;

public class Neuron {
	
	protected double[] weights = null;
	protected double bias = 0;
	protected double value = 0;
	
	private Neuron(double[] weights, double bias) {
		this.weights = weights;
		this.bias = bias;
	}
	
	public Neuron() {
		bias = Math.random() - 0.5;
	}
	
	public void generateWeights(int size) {
		weights = new double[size];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = (Math.random() * 3) - 1.5;
		}
	}
	
	public void reset() {
		value = 0;
	}
	
	public void feed(double value) {
		this.value += value;
	}
	
	public double[] output() {
//		if (weights == null) {
//			return new double[] {process(value + bias)};
//		}
		double[] outputs = new double[weights.length];
		for (int i = 0; i < outputs.length; i++) {
			outputs[i] = process((weights[i] * value) + bias);
		}
		return outputs;
	}
	
	public void mutate() {
		if (Math.random() > 0.9) {
			bias = Math.random() - 0.5;
		} else {
			Random random = new Random();
			weights[random.nextInt(weights.length - 1)] = (Math.random() * 3) - 1.5;
		}
	}
	
	public Neuron clone() {
		return new Neuron(weights.clone(), bias);
	}
	
	protected static double process(double num) {
//		return 1d / (1d + Math.exp(-num));
//		return num;
		return Math.tanh(num);
	}
	
}
