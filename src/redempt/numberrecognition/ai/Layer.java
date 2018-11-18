package redempt.numberrecognition.ai;

import java.util.ArrayList;
import java.util.List;

public class Layer {
	
	private List<Neuron> neurons = new ArrayList<>();
	
	public void setNextLayerSize(int size) {
		for (Neuron neuron : neurons) {
			neuron.generateWeights(size);
		}
	}
	
	public Layer(int count) {
		for (int i = 0; i < count; i++) {
			neurons.add(new Neuron());
		}
	}
	
	public List<Neuron> getNeurons() {
		return neurons;
	}
	
	public void addNeuron(Neuron neuron) {
		neurons.add(neuron);
	}
	
	public Layer clone() {
		List<Neuron> cloned = new ArrayList<>();
		neurons.forEach(n -> cloned.add(n.clone()));
		Layer layer = new Layer(0);
		layer.neurons = cloned;
		return layer;
	}
	
}
