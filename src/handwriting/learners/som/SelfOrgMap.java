package handwriting.learners.som;

import handwriting.core.Drawing;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class SelfOrgMap {
	private int drawingWidth, drawingHeight,
				map_height, map_width,
				training_iters = 600;

	private double learning_rate = 0.1,
					learning_radius = 2;
	private Drawing[][] map;
	// Representation data type
	
	public SelfOrgMap(int width, int height, int dWidth, int dHeight) {
		this.drawingWidth = dWidth;
		this.drawingHeight = dHeight;
		this.map_height = height;
		this.map_width = width;
		map = new Drawing[map_height][map_width];
		for (int i = 0; i < map_height; ++i){
			for (int j = 0; j < map_width; ++j){
				map[i][j] = new Drawing(dWidth, dHeight);
			}
		}
		/* TODO: Initialize your representation here */
	}
	
	// TODO: Fix these two methods
	public int getWidth() {return map_width;}
	public int getHeight() {return map_height;}
	
	public SOMPoint bestFor(Drawing example) {
		// TODO: Return the best matching node for "example"
		int min_dist = Integer.MIN_VALUE;
		SOMPoint closest = new SOMPoint(0, 0);
		for (int i = 0; i < map_height; ++i){
			for (int j = 0; j < map_width; ++j){
				Drawing index = map[i][j];
				int dist = distance(example, index);
				if (dist < min_dist){
					min_dist = dist;
					closest = new SOMPoint(j, i);
				}
			}
		}
		return closest;
	}

	public int distance(Drawing a, Drawing b){
		return a.compare(b);
	}
	
	public boolean isLegal(SOMPoint point) {
		return point.x() >= 0 && point.x() < getWidth() && point.y() >= 0 && point.y() < getHeight();
	}
	
	public void train(Drawing example) {
		/* TODO: Train your SOM using "example" */
		SOMPoint closest = bestFor(example);
		SOMPoint[] neighbors = closest.getNeighbors(learning_radius);
		trainN(example, closest, closest);
		for (SOMPoint neighbor : neighbors){
			trainN(example, neighbor, closest);
		}
	}

	private void trainN(Drawing example, SOMPoint cell, SOMPoint hitNode){
		double distanceFromHit = Math.sqrt((hitNode.x()*hitNode.x()) + (hitNode.y()*hitNode.y()));
		double scale = (learning_radius - distanceFromHit)/learning_radius;
		for (int i = 0; i < training_iters; ++i){
			trainMap(example, cell, scale);
		}
	}

	public void trainMap(Drawing example, SOMPoint cell, double scale){
		for (int x = 0; x < example.getWidth(); ++x){
			for (int y = 0; y < example.getHeight(); ++y){
				boolean exampleVal = example.isSet(cell.x(), cell.y());
				Drawing source = map[cell.x()][cell.y()];
				boolean sourceVal = source.isSet(x, y);
				boolean newVal = Math.random() < scale ? exampleVal : sourceVal;
				source.set(cell.x(), cell.y(), newVal);
			}
		}
	}
	
	public void visualize(Canvas surface) {
		/* TODO: Develop a custom SOM visualization and draw it on "surface" */
		double single_pixel_height = surface.getHeight() / (map_height * drawingHeight);
		double single_pixel_width = surface.getWidth() / (map_width * drawingWidth);
		GraphicsContext context = surface.getGraphicsContext2D();
		for (int i = 0; i < map_height; ++i){
			for (int j = 0; j < map_width; ++j){
				Drawing drawing = map[i][j];
				for (int hpixel = 0; hpixel < drawingHeight; ++hpixel) {
					for (int wpixel = 0; wpixel < drawingWidth; ++wpixel) {
						double x = (hpixel + i) * single_pixel_height,
								y = (wpixel + j) * single_pixel_width;
						if (drawing.isSet(hpixel, wpixel))
							context.fillRect(x, y, single_pixel_width, single_pixel_height);
					}
				}
			}
		}
	}
}
