package redempt.numberrecognition;

import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import redempt.numberrecognition.ai.NeuralNetwork;
import redempt.numberrecognition.ai.SampleSet;

public class Main extends Application {
	
	private static SampleSet[] samples = new SampleSet[10];
	private static NeuralNetwork network = new NeuralNetwork(400, 10, 10, 10);
	
	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			samples[i] = new SampleSet();
		}
		launch();
	}
	
	public void start(Stage stage) throws Exception {
		Board board = new Board(20, 20);
		StackPane pane = new StackPane();
		pane.getChildren().add(board);
		Scene scene = new Scene(pane);
		stage.setScene(scene);
		stage.setTitle("Number Recognition");
		stage.show();
		stage.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
			if (e.getCode() == KeyCode.C) {
				board.clear();
			}
			if (e.getText().matches("[0-9]")) {
				int index = Integer.parseInt(e.getText());
				samples[index].getSamples().add(board.getState());
				board.clear();
				System.out.println("Sample added for " + index);
			}
			if (e.getCode() == KeyCode.T) {
				System.out.println("Training network, please wait...");
				int score = 0;
				int total = 0;
				for (int i = 0; i < samples.length; i++) {
					for (double[][] sample : samples[i].getSamples()) {
						total++;
						if (getHighestIndex(network.feed(sample)) == i) {
							score++;
						}
					}
				}
				System.out.println("Initial score: " + score + "/" + total);
				for (int i = 0; i < samples.length; i++) {
					if (samples[i].getSamples().size() > 0) {
						List<double[][]> sampleSet = samples[i].getSamples();
//						for (int x = 0; x < 2; x++) {
							double[][] sample = sampleSet.get((int) Math.round(Math.random() * (sampleSet.size() - 1)));
							for (double n = 1; n <= 1; n++) {
								network.dryFeed(sample);
								network.train(getExpected(i));
								network.resetNeurons();
							}
//						}
					}
				}
				score = 0;
				for (int i = 0; i < samples.length; i++) {
					for (double[][] sample : samples[i].getSamples()) {
						if (getHighestIndex(network.feed(sample)) == i) {
							score++;
						}
					}
				}
				System.out.println("Network training complete!");
				System.out.println("Final score: " + score + "/" + total);
				board.clear();
			}
			if (e.getCode() == KeyCode.SPACE) {
				double[] output = network.feed(board.getState());
				System.out.println(getHighestIndex(output));
			}
		});
	}
	
	private static double[] getExpected(int num) {
		double[] out = new double[10];
		for (int i = 0; i < 10; i++) {
			if (i == num) {
				out[i] = 1;
			} else {
				out[i] = 0;
			}
		}
		return out;
	}
	
	public static int getHighestIndex(double[] array) {
		int index = -1;
		double highest = Double.MIN_VALUE;
		for (int i = 0; i < array.length; i++) {
			double val = array[i];
			if (val > highest) {
				highest = val;
				index = i;
			}
		}
		return index;
	}
	
}
