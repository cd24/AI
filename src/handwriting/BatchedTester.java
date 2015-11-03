package handwriting;

import handwriting.core.Drawing;
import handwriting.core.RecognizerAI;
import handwriting.core.SampleData;
import handwriting.gui.Result;
import handwriting.learners.*;
import maze.gui.AIReflector;
import planner.core.Domain;
import search.core.Duple;
import search.core.Histogram;
import search.core.Triple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Supplier;

public class BatchedTester {
    public static final String outFile = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "AutomatedTestingHandwriting.csv",
                                projectDir = "C:\\Users\\John\\Documents\\Github\\AI",
                                trainingDataRoot = projectDir + File.separator + "PerceptronTrainingData" + File.separator + "TrainingData",
                                testingDataRoot = projectDir + File.separator + "PerceptronTrainingData"+ File.separator +"TestingData";

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        ArrayBlockingQueue<Double> progress = new ArrayBlockingQueue<Double>(3);
        popperThread(progress);
        PrintWriter writer = getWriter();

        Supplier<RecognizerAI>[] ais = getSuppliers();
        writer.write("Label, Percent Correct, Num Correct, Num Incorrect");
        for (Supplier<RecognizerAI> ai : ais){
            for (int k = 2; k <= 8 ; ++k) {
                RecognizerAI runner = ai.get();
                String title = runner.getClass().getCanonicalName() + "; File Numbers: " + k;
                SampleData testData = SampleData.parseDataFrom(new File(trainingDataRoot + k));
                runner.train(testData, progress);
                testData = SampleData.parseDataFrom(new File(testingDataRoot + k));
                Triple<Histogram, Histogram, Double> results = runTests(runner, testData);
                writeResultToFile(writer, results, title, testData);
            }
        }
    }

    public static Supplier<RecognizerAI>[] getSuppliers(){
        return new Supplier[]{
                BaggedSOM::new,
                BaggedMLP::new,
                SOM::new,
                MultiLayerBitwise::new
        };
    }

    public static void popperThread(ArrayBlockingQueue<Double> toPop){
        Thread t = new Thread(()->{
            try {
                Double current = toPop.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.run();
    }

    static Triple<Histogram, Histogram, Double> runTests(RecognizerAI trainer, SampleData testData) {
        int numCorrect = 0;
        Histogram<String> correct = new Histogram<>(), incorrect = new Histogram<>();
        for (int i = 0; i < testData.numDrawings(); i++) {
            Duple<String,Drawing> test = testData.getLabelAndDrawing(i);
            if (trainer.classify(test.getSecond()).equals(test.getFirst())) {
                numCorrect += 1;
                correct.bump(test.getFirst());
            } else {
                incorrect.bump(test.getFirst());
            }
        }
        double percent = 100.0 * numCorrect / testData.numDrawings();
        return new Triple<>(correct, incorrect, percent);
    }

    static void writeResultToFile(PrintWriter writer, Triple<Histogram, Histogram, Double> results, String title, SampleData data){
        writer.write(title + "\n");
        Histogram correct = results.getFirst(), incorrect = results.getSecond();
        Double percent = results.getThird();
        writer.write(title + "\n" + percent + " Correct\n");
        for (String label : data.allLabels()){
            Result res = new Result(label, correct.getCountFor(label), incorrect.getCountFor(label));
            writer.write(res.labelProperty() + ", " + res.percentProperty() + ", " + res.successProperty() + ", " + res.failureProperty() + "\n");
        }
    }

    static PrintWriter getWriter(){
        try {
            return new PrintWriter(outFile);
        } catch (FileNotFoundException e) {

        }
        return null;
    }
}
