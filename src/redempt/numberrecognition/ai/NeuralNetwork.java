package redempt.numberrecognition.ai;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import redempt.numberrecognition.Main;

public class NeuralNetwork {
	
	private List<Layer> layers = new ArrayList<>();
	public int score = -1;
	private int[] layerStructure;
	
	public NeuralNetwork(int... layerStructure) {
		if (layerStructure.length <= 1) {
			throw new IllegalArgumentException("Neural network must have at least 2 layers!");
		}
		this.layerStructure = layerStructure;
		for (int num : layerStructure) {
			layers.add(new Layer(num));
		}
		for (int i = 0; i < layers.size() - 1; i++) {
			layers.get(i).setNextLayerSize(layers.get(i + 1).getNeurons().size());
		}
		for (Neuron neuron : layers.get(layers.size() - 1).getNeurons()) {
			neuron.weights = new double[] {1};
			neuron.bias = 0;
		}
		layers.get(layers.size() - 1).setNextLayerSize(1);
	}
	
	public double[] feed(double[]... inputs) {
		int pos = 0;
		for (double[] array : inputs) {
			for (double num : array) {
				layers.get(0).getNeurons().get(pos).feed(num);
				pos++;
			}
		}
		for (Neuron neuron : layers.get(0).getNeurons()) {
			double[] output = neuron.output();
			neuron.reset();
			for (int i = 0; i < output.length; i++) {
				layers.get(1).getNeurons().get(i).feed(output[i]);
			}
		}
		for (int i = 1; i < layers.size() - 1; i++) {
			for (Neuron neuron : layers.get(i).getNeurons()) {
				double[] output = neuron.output();
				neuron.reset();
				for (int x = 0; x < output.length; x++) {
					layers.get(i + 1).getNeurons().get(x).feed(output[x]);
				}
			}
		}
		double[] outputs = new double[layers.get(layers.size() - 1).getNeurons().size()];
		for (int i = 0; i < outputs.length; i++) {
			Neuron neuron = layers.get(layers.size() - 1).getNeurons().get(i);
			outputs[i] = neuron.output()[0];
			neuron.reset();
		}
		return outputs;
	}
	
	public double[] dryFeed(double[]... inputs) {
		int pos = 0;
		for (double[] array : inputs) {
			for (double num : array) {
				layers.get(0).getNeurons().get(pos).feed(num);
				pos++;
			}
		}
		for (Neuron neuron : layers.get(0).getNeurons()) {
			double[] output = neuron.output();
			for (int i = 0; i < output.length; i++) {
				layers.get(1).getNeurons().get(i).feed(output[i]);
			}
		}
		for (int i = 1; i < layers.size() - 1; i++) {
			for (Neuron neuron : layers.get(i).getNeurons()) {
				double[] output = neuron.output();
				for (int x = 0; x < output.length; x++) {
					layers.get(i + 1).getNeurons().get(x).feed(output[x]);
				}
			}
		}
		double[] outputs = new double[layers.get(layers.size() - 1).getNeurons().size()];
		for (int i = 0; i < outputs.length; i++) {
			Neuron neuron = layers.get(layers.size() - 1).getNeurons().get(i);
			outputs[i] = neuron.output()[0];
		}
		return outputs;
	}
	
	public void train(double[] expected) {
		if (expected.length != layers.get(layers.size() - 1).getNeurons().size()) {
			throw new IllegalArgumentException("Wrong number of outputs provided!");
		}
		double[] outputs = getOutputs();
		double[] offsets = new double[expected.length];
		for (int i = 0; i < offsets.length; i++) {
			offsets[i] = Math.abs(expected[i] - outputs[i]);
		}
		int n = Main.getHighestIndex(offsets);
		if (n == -1) {
			return;
		}
//		for (int n = 0; n < offsets.length; n++) {
			//List of the most significant neuron to this output from each layer
			double offset = expected[n] - outputs[n];
			if (Math.abs(offset) < 0.05) {
				System.out.println(n + " No change");
				return;
			}
			Set<NeuronLocation> significant = getSignificantNeurons(new NeuronLocation(layers.size() - 1, n));
			double most = Double.MIN_VALUE;
			NeuronLocation best = null;
			for (NeuronLocation loc : significant) {
				double weightOutput = Math.tanh(Math.abs((expected[n] - (tweakAndCheck(loc, loc.getFocus())[n])) - offset));
//				double biasOutput = Math.abs((expected[n] - (tweakAndCheck(loc, -1)[n])) - offset);
				double biasOutput = -1;
				if (weightOutput > most || biasOutput > most || most == Double.MIN_VALUE) {
					if (biasOutput > weightOutput) {
						loc.setFocus(-1);
					}
					most = Math.max(biasOutput, weightOutput);
					best = loc;
				}
			}
//			System.out.println("Selecting neuron " + best.getLayer() + ", " + best.getNumber());
			double offsetChange = (expected[n] - tweakAndCheck(best, best.getFocus())[n]) - offset;
//			System.out.println(n + " Effectiveness: " + most);
//			if (offsetChange / offset < 0) {
				offsetChange = -offset / offsetChange;
//			}
			if (Math.abs(offsetChange) > 1) {
				offsetChange = Math.signum(offsetChange) * 1;
			}
			System.out.println(n + " Change: " + offsetChange);
			if (best.getFocus() != -1) {
				getNeuron(best).weights[best.getFocus()] += offsetChange;
				System.out.println("w");
			} else {
				System.out.println("b");
				getNeuron(best).bias += offsetChange;
			}
			NeuralNetwork clone = clone();
			clone.dryFeed(getValues(0));
			System.out.println(n + " Offset: " + offset);
			System.out.println(n + " Old: " + getOutputs()[n]);
			System.out.println(n + " New: " + clone.getOutputs()[n]);
//		}
	}
	
	private Set<NeuronLocation> getSignificantNeurons(NeuronLocation loc) {
		Set<NeuronLocation> significant = new HashSet<>();
		for (int x = 1; x < loc.getLayer() - 1; x++) {
			for (int y = 0; y < layers.get(x).getNeurons().size(); y++) {
				significant.add(new NeuronLocation(x, y));
			}
		}
		return significant;
	}
	
	private double[] tweakAndCheck(NeuronLocation location, int weight) {
		if (weight > -1) {
			NeuralNetwork clone = clone();
			Neuron neuron = clone.getNeuron(location);
			neuron.weights[weight] += 0.01;
			return clone.dryFeed(getValues(0));
		} else {
			NeuralNetwork clone = clone();
			Neuron neuron = clone.getNeuron(location);
			neuron.bias += 0.01;
			return clone.dryFeed(getValues(0));
		}
	}
	
	private double[] getValues(int layer) {
		double[] values = new double[layers.get(layer).getNeurons().size()];
		for (int i = 0; i < values.length; i++) {
			values[i] = layers.get(layer).getNeurons().get(i).value;
		}
		return values;
	}
	
	private double[] getOutputs() {
		double[] values = new double[layers.get(layers.size() - 1).getNeurons().size()];
		for (int i = 0; i < values.length; i++) {
			values[i] = layers.get(layers.size() - 1).getNeurons().get(i).output()[0];
		}
		return values;
	}
	
	public Neuron getNeuron(NeuronLocation location) {
		return layers.get(location.getLayer()).getNeurons().get(location.getNumber());
	}
	
	public void resetNeurons() {
		layers.stream().forEach(c -> c.getNeurons().stream().forEach(Neuron::reset));
	}
	
	public void mutate(int iterations) {
		for (int i = 0; i < iterations; i++) {
			Layer layer = layers.get((int) Math.round(Math.random() * (layers.size() - 2)));
			Neuron neuron = layer.getNeurons().get((int) Math.round(Math.random() * (layer.getNeurons().size() - 1)));
			neuron.mutate();
		}
	}
	
	public List<Layer> getLayers() {
		return layers;
	}
	
	public NeuralNetwork clone() {
		List<Layer> cloned = new ArrayList<>();
		layers.stream().forEach(l -> cloned.add(l.clone()));
		NeuralNetwork network = new NeuralNetwork(layerStructure);
		network.layers = cloned;
		return network;
	}
	
}
