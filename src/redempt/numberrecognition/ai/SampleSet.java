package redempt.numberrecognition.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SampleSet {
	
	private List<double[][]> samples = new ArrayList<>();
	
	public SampleSet(double[][]... samples) {
		this.samples.addAll(Arrays.asList(samples));
	}
	
	public List<double[][]> getSamples() {
		return samples;
	}
	
}
