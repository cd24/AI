package handwriting.learners;

import handwriting.core.Drawing;
import handwriting.core.RecognizerAI;
import handwriting.core.SampleData;
import handwriting.learners.som.SOMPoint;
import handwriting.learners.som.SelfOrgMap;
import javafx.scene.canvas.Canvas;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by john on 10/9/15.
 */
public class SOM implements RecognizerAI {
    SelfOrgMap map;
    int iterations = 60;
    @Override
    public void train(SampleData data, ArrayBlockingQueue<Double> progress) throws InterruptedException {
        Drawing sample = data.getDrawing(0);
        double doneness = 0;
        map = new SelfOrgMap(10, 10, sample.getWidth(), sample.getHeight());
        for (int k = 0; k < iterations; ++k) {
            for (String label : data.allLabels()) {
                for (int i = 0; i < data.numDrawingsFor(label); ++i) {
                    Drawing current = data.getDrawing(label, i);
                    SOMPoint best = map.bestFor(current);
                    for (SOMPoint neighbor : best.getNeighbors(map.learning_radius, map.getWidth(), map.getHeight())){
                        trainMap(current, neighbor, best);
                    }
                    doneness += 1.0 / (k * data.numLabels() * data.numDrawings());
                    progress.add(doneness);
                }
            }
        }

        for (String label : data.allLabels()){
            for (int k = 0; k < data.numDrawingsFor(label); ++k){
                Drawing s = data.getDrawing(label, k);
                map.setLabel(s, label);
            }
        }
    }

    @Override
    public String classify(Drawing d) {
        SOMPoint labelLoc = map.bestFor(d);
        String result = map.getLabel(labelLoc);
        return result == null ? "" : result;
    }

    @Override
    public void visualize(Canvas surface) {
        map.visualize(surface);
    }

    private void trainMap(Drawing example, SOMPoint cell, SOMPoint hitNode){
        double distanceFromHit = cell.distanceTo(hitNode.x(), hitNode.y());
        double scale = (1 - ((map.learning_radius - distanceFromHit)/map.learning_radius)) * map.learning_rate;
        map.trainMap(example, cell, scale);
    }

    private void adjustLearningRate(){

    }
}
