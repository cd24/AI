package handwriting.learners.som;

import handwriting.core.Drawing;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SelfOrgMap {
	private int drawingWidth, drawingHeight,
				map_height, map_width,
				training_iters = 10;

	public double learning_rate = 0.1,
					learning_radius = 2;
	private double[][][][] map;
	private String[][] labels;
	// Representation data type
	
	public SelfOrgMap(int width, int height, int dWidth, int dHeight) {
		this.drawingWidth = dWidth;
		this.drawingHeight = dHeight;
		this.map_height = height;
		this.map_width = width;
		map = new double[map_height][map_width][drawingHeight][drawingWidth];
		labels = new String[map_height][map_width];
		for (int i = 0; i < map_height; ++i){
			for (int j = 0; j < map_width; ++j){
				map[i][j] = new double[drawingHeight][drawingWidth];
				for (int h = 0; h < drawingHeight; ++h){
					for (int l = 0; l < drawingWidth; ++l){
						map[i][j][h][l] = Math.random();
					}
				}
			}
		}
		/* TODO: Initialize your representation here */
	}

	public int getWidth() {return map_width;}
	public int getHeight() {return map_height;}

	public int getDrawingWidth() {return drawingWidth;}
	public int getDrawingHeight() {return drawingHeight;}
	
	public SOMPoint bestFor(Drawing example) {
		// TODO: Return the best matching node for "example"
		double min_dist = Double.MAX_VALUE;
		SOMPoint closest = new SOMPoint(0, 0);
		for (int i = 0; i < map_height; ++i){
			for (int j = 0; j < map_width; ++j){
				double[][] index = map[i][j];
				double dist = distance(example, index);
				if (dist < min_dist){
					min_dist = dist;
					closest = new SOMPoint(j, i);
				}
			}
		}
		return closest;
	}

	public double distance(Drawing a, double[][] b){
		double distance = 0;
		for (int x = 0; x < drawingWidth; ++x){
			for (int y = 0; y < drawingWidth; ++y){
				distance += Math.pow(b[x][y] - (a.isSet(x, y) ? 1 : 0), 2);
			}
		}
		return distance;
	}

	public double distance(double[][] a, double[][] b){
		double distance = 0;
		for (int x = 0; x < drawingWidth; ++x){
			for (int y = 0; y < drawingWidth; ++y){
				distance += Math.pow(b[x][y] - a[x][y], 2);
			}
		}
		return distance;
	}
	
	public boolean isLegal(SOMPoint point) {
		return point.x() >= 0 && point.x() < getWidth() && point.y() >= 0 && point.y() < getHeight();
	}
	
	public void train(Drawing example) {
		/* TODO: Train your som using "example" */
		SOMPoint closest = bestFor(example);
		SOMPoint[] neighbors = closest.getNeighbors(learning_radius,getWidth(), getHeight());
		trainMap(example, closest, learning_rate);
		for (SOMPoint neighbor : neighbors){
			double learning = learning_rate*(neighbor.distanceTo(closest.x(), closest.y())/learning_radius);
			trainMap(example, neighbor, learning);
		}
	}

	public void trainMap(Drawing example, SOMPoint cell, double scale){
		for (int x = 0; x < example.getWidth(); ++x){
			for (int y = 0; y < example.getHeight(); ++y){
				boolean isSet = example.isSet(x, y);
				double exampleVal = isSet ? 1.0 : 0.0;
				double source = map[cell.x()][cell.y()][x][y];
				source = (exampleVal - source)*scale + source;
				map[cell.x()][cell.y()][x][y] = source;
			}
		}
	}

	public void setLabel(Drawing d, String label){
		SOMPoint winner = bestFor(d);
		labels[winner.y()][winner.x()] = label;
	}

	public void setRemainingLabels(){
		for (int i = 0; i < getWidth(); ++i){
			for (int j = 0; j < getHeight(); ++j){
				if (labels[j][i] == null){
					//move labels in clever ways
					SOMPoint bestMatch = findCounterPart(new SOMPoint(i, j));
					labels[j][i] = labels[bestMatch.y()][bestMatch.x()];
				}
			}
		}
	}

	public SOMPoint findCounterPart(SOMPoint point){
		double min_dist = Double.MAX_VALUE;
		SOMPoint minimum = new SOMPoint(0, 0);
		double[][] value = map[point.y()][point.x()];
		for (int i = 0; i < getWidth(); ++i){
			for (int j = 0; j < getHeight(); ++j){
				double distance = distance(value, map[j][i]);
				if (distance < min_dist && labels[j][i] != null){
					min_dist = distance;
					minimum = new SOMPoint(i, j);
				}
			}
		}
		return minimum;
	}

	public String getLabel(SOMPoint point){
		return labels[point.y()][point.x()];
	}

	public Color getFillFor(int x, int y, SOMPoint node) {
		double value = map[node.x()][node.y()][x][y];
		if (value < 0){
			value = 0.0;
		}
		else if (value > 1){
			value = 1.0;
		}
		return new Color(value, value, value, 1.0);
	}

	public void visualize(Canvas surface) {
		final double cellWidth = surface.getWidth() / getWidth();
		final double cellHeight = surface.getHeight() / getHeight();
		final double pixWidth = cellWidth / getDrawingWidth();
		final double pixHeight = cellHeight / getDrawingHeight();
		GraphicsContext g = surface.getGraphicsContext2D();
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				SOMPoint cell = new SOMPoint(x, y);
				for (int x1 = 0; x1 < getDrawingWidth(); x1++) {
					for (int y1 = 0; y1 < getDrawingHeight(); y1++) {
						g.setFill(getFillFor(x1, y1, cell));
						g.fillRect(cellWidth * x + pixWidth * x1, cellHeight * y + pixHeight * y1, pixWidth, pixHeight);
					}
				}
			}
		}
	}
}
