package handwriting.gui;

import handwriting.core.Drawing;
import handwriting.core.RecognizerAI;
import handwriting.core.SampleData;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import search.core.AIReflector;

import java.util.concurrent.ArrayBlockingQueue;

public class DrawingEditorController {
	public final static int DRAWING_WIDTH = 40, DRAWING_HEIGHT = 40;
	
	@FXML
	Button record;
	
	@FXML
	Button drawErase;
	
	@FXML
	Button lookup;
	
	@FXML
	Button classify;
	
	@FXML
	Button train;
	
	@FXML
	Canvas canvas;
	
	@FXML
	ChoiceBox<String> labelChoice;
	
	@FXML
	ChoiceBox<Integer> drawingChoice;
	
	@FXML
	ChoiceBox<String> algorithmChoice;
	
	@FXML
	TextField recordingClassificationLabel;
	
	@FXML
	ProgressBar trainingProgress;
	
	@FXML
	MenuItem newData;
	
	@FXML
	MenuItem openData;
	
	@FXML
	MenuItem saveData;
	
	RecognizerAI trainer;
	
	SampleData drawings;
	
	boolean isDrawing;
	
	Drawing sketch;
	
	AIReflector<RecognizerAI> ais;
	
	@FXML
	void initialize() {
		setupVars();
		setupMenus();
		setupButtons();
		setupCanvas();
	}
	
	void setupVars() {
		newData();
		setupDrawErase();
		setupDefaultTrainer();
		findTrainers();	
		resetSketch();
	}
	
	void setupDefaultTrainer() {
		trainer = new RecognizerAI() {
			@Override
			public void train(SampleData data, ArrayBlockingQueue<Double> progress) throws InterruptedException {}
			@Override
			public String classify(Drawing d) {return "Unknown";}};
	}
	
	void resetSketch() {
		sketch = new Drawing(DRAWING_WIDTH, DRAWING_HEIGHT);
	}
	
	void setupDrawErase() {
		isDrawing = true;
		setDrawEraseText();
		drawErase.setOnAction(event -> {
			isDrawing = !isDrawing;
			setDrawEraseText();
		});		
	}
	
	void setDrawEraseText() {
		drawErase.setText(isDrawing ? "Erase" : "Draw");
	}
	
	void newData() {
		drawings = new SampleData();
	}
	
	void setupMenus() {
		newData.setOnAction(event -> newData());
	}
	
	void findTrainers() {
		ais = new AIReflector<>(RecognizerAI.class, "handwriting.learners");
		for (String typeName: ais.getTypeNames()) {
			algorithmChoice.getItems().add(typeName);
		}
		if (algorithmChoice.getItems().size() > 0) {
			algorithmChoice.getSelectionModel().select(0);
		}
	}
	
	void setupButtons() {
		setupRecord();
		
		setupTrain();
		
		lookup.setOnAction(event -> 
			sketch = drawings.getDrawing(getCurrentLabel(), getCurrentDrawing()));
		
		classify.setOnAction(event -> 
			recordingClassificationLabel.setText(trainer.classify(sketch)));
	}
	
	void setupRecord() {
		record.setOnAction(event -> {
			String label = recordingClassificationLabel.getText();
			if (label.length() > 0) {
				addSample(label, sketch);
				resetSketch();
			} else {
				Alert popup = new Alert(AlertType.ERROR);
				popup.setContentText("No label specified");
				popup.show();
			}
		});		
	}
	
	void setupTrain() {
		ArrayBlockingQueue<Double> progress = new ArrayBlockingQueue<>(2);
		new Thread(() -> {
			double prog = 0;
			for (;;) {
				trainingProgress.setProgress(prog);
				try {
					prog = progress.take();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		
		train.setOnAction(event -> {
			ArrayBlockingQueue<RecognizerAI> result = new ArrayBlockingQueue<>(1);

			new Thread(() -> {try {trainer = result.take();} catch (Exception e) {}}).start();
			
			new Thread(() -> {
				try {
					progress.put(0.0);
					RecognizerAI created = ais.newInstanceOf(algorithmChoice.getSelectionModel().getSelectedItem());
					created.train(drawings, progress);
					result.put(created);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}).start();
		});		
	}
	
	void addSample(String label, Drawing sample) {
		drawings.addDrawing(label, sample);
		if (!labelChoice.getItems().contains(label)) {
			labelChoice.getItems().add(label);
		}
		labelChoice.getSelectionModel().select(label);
		resetDrawingList();
	}
	
	void resetDrawingList() {
		drawingChoice.getItems().clear();
		for (int i = 0; i < drawings.numDrawingsFor(getCurrentLabel()); i++) {
			drawingChoice.getItems().add(i);
		}
	}
	
	String getCurrentLabel() {
		return labelChoice.getSelectionModel().getSelectedItem();
	}
	
	int getCurrentDrawing() {
		return drawingChoice.getSelectionModel().getSelectedItem();
	}
	
	void setupCanvas() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		canvas.setOnMouseDragged(mouse -> {
			int xGrid = (int)(mouse.getX() / xCell());
			int yGrid = (int)(mouse.getY() / yCell());
			plot(xGrid, yGrid);
			plot(xGrid - 1, yGrid);
			plot(xGrid + 1, yGrid);
			plot(xGrid - 1, yGrid - 1);
			plot(xGrid, yGrid - 1);
			plot(xGrid + 1, yGrid - 1);
			plot(xGrid - 1, yGrid + 1);
			plot(xGrid, yGrid + 1);
			plot(xGrid + 1, yGrid + 1);
		});
	}
	
	double xCell() {return canvas.getWidth() / sketch.getWidth();}
	double yCell() {return canvas.getHeight() / sketch.getHeight();}
	
	void plot(int x, int y) {
		if (x >= 0 && x < sketch.getWidth() && y >= 0 && y < sketch.getHeight()) {
			sketch.set(x, y, isDrawing);		
			GraphicsContext gc = canvas.getGraphicsContext2D();
			gc.setFill(isDrawing ? Color.BLACK : Color.WHITE);
			gc.fillRect(x * xCell(), y * yCell(), xCell(), yCell());
		}
	}
}
