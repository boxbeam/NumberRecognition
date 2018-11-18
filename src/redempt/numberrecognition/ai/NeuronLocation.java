package redempt.numberrecognition.ai;

import java.util.Objects;

public class NeuronLocation {
	
	private int layer;
	private int number;
	private int focus;
	
	public NeuronLocation(int layer, int number) {
		this.layer = layer;
		this.number = number;
	}
	
	public NeuronLocation(int layer, int number, int focus) {
		this.layer = layer;
		this.number = number;
		this.focus = focus;
	}
	
	public int getFocus() {
		return focus;
	}
	
	public void setFocus(int focus) {
		this.focus = focus;
	}
	
	public int getLayer() {
		return layer;
	}
	
	public int getNumber() {
		return number;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof NeuronLocation) {
			NeuronLocation other = (NeuronLocation) o;
			return other.layer == this.layer && other.number == this.number;
		}
		return super.equals(o);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(number, layer);
	}
	
}
