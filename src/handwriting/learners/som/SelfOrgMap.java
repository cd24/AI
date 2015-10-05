package handwriting.learners.som;

import handwriting.core.Drawing;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class SelfOrgMap {
	private int drawingWidth, drawingHeight,
				map_height, map_width;
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
		return new SOMPoint(0, 0);
	}
	
	public boolean isLegal(SOMPoint point) {
		return point.x() >= 0 && point.x() < getWidth() && point.y() >= 0 && point.y() < getHeight();
	}
	
	public void train(Drawing example) {
		/* TODO: Train your SOM using "example" */
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
