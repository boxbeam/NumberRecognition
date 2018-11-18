package redempt.numberrecognition;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Board extends Canvas {
	
	private int width;
	private int height;
	private double[][] values;
	private double drag = 0;
	
	public Board(int width, int height) {
		this.setFocusTraversable(true);
		this.width = width;
		this.height = height;
		values = new double[width][height];
		this.setWidth(width * 20);
		this.setHeight(height * 20);
		this.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
			int x = (int) e.getX() / 20;
			int y = (int) e.getY() / 20;
			if (y < 0 || y >= height || x < 0 || x >= width) {
				return;
			}
			drag = 1 - values[x][y];
			values[x][y] = 1 - values[x][y];
			update();
		});
		this.addEventHandler(MouseEvent.MOUSE_DRAGGED, (e) -> {
			int x = (int) e.getX() / 20;
			int y = (int) e.getY() / 20;
			if (y < 0 || y >= height || x < 0 || x >= width) {
				return;
			}
			values[x][y] = drag;
			update();
		});
	}
	
	public void update() {
		GraphicsContext g = this.getGraphicsContext2D();
		g.setFill(Color.WHITE);
		g.fillRect(0, 0, width * 20, height * 20);
		g.setFill(Color.BLACK);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (values[x][y] > 0) {
					g.fillRect(x * 20, y * 20, 20, 20);
				}
			}
		}
	}
	
	public double[][] getState() {
		double[][] cloned = new double[width][];
		for (int i = 0; i < width; i++) {
			cloned[i] = values[i].clone();
		}
		return cloned;
	}
	
	public void clear() {
		values = new double[width][height];
		update();
	}
	
}
