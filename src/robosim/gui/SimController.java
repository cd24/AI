package robosim.gui;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import robosim.ai.Controller;
import robosim.core.SimObjMaker;
import robosim.core.SimObject;
import robosim.core.Simulator;
import search.core.AIReflector;

public class SimController {
	public static final double RADIUS = 10.0;
	public static final long PERIOD = 60;
	public static final long INTERVAL = 1000000000 / PERIOD;
	
	@FXML
	ChoiceBox<SimObjMaker> objectToPlace;
	
	@FXML
	ChoiceBox<String> ai;
	
	@FXML
	Canvas canvas;
	
	@FXML
	Button start;
	
	@FXML
	Button stop;
	
	@FXML
	TextField total;
	
	@FXML
	TextField forward;
	
	@FXML
	TextField collisions;
	
	Simulator map;
	
	AIReflector<Controller> ais;
	
	AnimationTimer timer;
	
	@FXML
	void initialize() {
		map = new Simulator(canvas.getWidth(), canvas.getHeight());
		
		for (SimObjMaker maker: SimObjMaker.values()) {objectToPlace.getItems().add(maker);}
		objectToPlace.getSelectionModel().select(0);
		
		map.drawOn(canvas);
		canvas.setOnMouseClicked(event -> {
			SimObject obj = objectToPlace.getSelectionModel().getSelectedItem().makeAt(event.getX(), event.getY());
			map.add(obj);
			map.drawOn(canvas);
		});
		
		start.setOnAction(event -> {
			try {
				Controller controller = ais.newInstanceOf(ai.getSelectionModel().getSelectedItem());
				halt();
				map.reset();
				timer = new AnimationTimer() {
					long next = 0;
					@Override
					public void handle(long now) {
						if (now > next) {
							next = now + INTERVAL;
							controller.control(map);
							map.move();
							map.drawOn(canvas);
							Platform.runLater(() -> {
								total.setText(Integer.toString(map.getTotalMoves()));
								forward.setText(Integer.toString(map.getForwardMoves()));
								collisions.setText(Integer.toString(map.getCollisions()));
							});
						}
					}
				};
				timer.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		stop.setOnAction(event -> halt());
		
		ais = new AIReflector<>(Controller.class, "robosim.ai");
		for (String typeName: ais.getTypeNames()) {
			ai.getItems().add(typeName);
		}
		if (ai.getItems().size() > 0) {
			ai.getSelectionModel().select(0);
		}
		
		total.setEditable(false);
		collisions.setEditable(false);
		forward.setEditable(false);
	}
	
	void halt() {
		if (timer != null) timer.stop();
	}
}
