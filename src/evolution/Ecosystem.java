package evolution;

import handwriting.core.Drawing;
import handwriting.core.SampleData;
import search.core.Duple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class Ecosystem {
    String outPath = System.getProperty("user.dir") + File.separator + "EvolutionResults.csv";
    int num_animals = 1000,
        num_generation = 1000,
        carry_over = (int) (0.1 * num_animals),
        num_cores = 4;
    MutableMLP[] animals;
    double[] ranking;
    double mutationRate = 0.3, crossoverRate = 0.4, topTenChance = 0.7, topFiftyChance = 0.9;
    String[] allLabels;
    Duple<String, Drawing>[] testData;
    Random random;
    ArrayBlockingQueue<MutableMLP> toWork, worked;
    Thread[] threads = new Thread[num_cores];

    public Ecosystem(int size, Duple<String, Drawing>[] data, String[] allLabels) {
        this.num_animals = size;
        this.toWork = new ArrayBlockingQueue<>(this.num_animals);
        this.worked = new ArrayBlockingQueue<MutableMLP>(this.num_animals);
        this.carry_over = (int) (0.1 * num_animals);
        animals = new MutableMLP[num_animals];
        ranking = new double[num_animals];
        this.allLabels = allLabels;
        this.testData = data;
        SampleData dat = new SampleData();

        for (int i = 0; i < this.testData.length; ++i){
            dat.addDrawing(this.testData[i].getFirst(), this.testData[i].getSecond());
        }

        createPopulation();
        populateWorkQueue();

        for (int i = 0; i < num_cores; ++i){
            Thread trainer = new Thread(()->{
                while(!this.toWork.isEmpty()) {
                    try {
                         MutableMLP worker = toWork.take();
                        worker.train(dat);
                        worked.add(worker);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            });
            trainer.start();
            threads[i] = trainer;
        }

        while(!threadsFinished()){
            double percent = ((double)(this.num_animals - this.toWork.size()))/this.num_animals;
            System.out.print("\rBuilding Ecosystem... " + (percent*100) + "%");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            deloadWork();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        random = new Random();
        setLabels();
    }

    public boolean threadsFinished(){
        for (int i = 0; i < num_cores; ++i){
            if (threads[i].isAlive())
                return false;
        }
        return true;
    }

    public void populateWorkQueue(){
        for (int i = 0; i < this.num_animals; ++i){
            this.toWork.add(animals[i]);
        }
    }

    public void createPopulation(){
        for (int i = 0; i < this.num_animals; ++i){
            this.animals[i] = new MutableMLP();
        }
    }

    public void deloadWork() throws InterruptedException {
        for (int i = 0; i < this.num_animals; ++i){
            animals[i] = this.worked.take();
        }
    }

    public void run() throws InterruptedException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        System.out.println("Started at: " + dateFormat.format(new Date()));
        for (int i = 0; i < num_generation; ++i) {
            double progress = ((double)i/num_generation) * 100;
            System.out.print("\rGeneration: " + i + " of " + num_generation + ": " + progress + "%");
            evaluate();
            this.animals = nextGeneration();
            repopulate();
        }
        System.out.print("\n");
        System.out.println("Finished at: " + dateFormat.format(new Date()));
        try {
            printWeights();
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't write to file.");
            e.printStackTrace();
        }
        report();
    }

    public void printWeights() throws FileNotFoundException {
        MutableMLP[] strongest = nextGeneration();
        File saveLoc = new File(outPath);
        PrintWriter writer = new PrintWriter(saveLoc);
        for (int i = 0; i < carry_over; ++i){
            MutableMLP out = strongest[i];
            writer.println(i);
            writer.println(out.toString());
        }
    }

    public void report(){
        MutableMLP[] strongest = nextGeneration();
        for (int i = 0; i < carry_over; ++i){
            System.out.println(i + "; " + evaluate(strongest[i]));
        }
    }

    public void repopulate(){
        //assume top ten are correct
        //naive implementation.  Doesn't take into account what each is good at.
        for (int i = carry_over; i < animals.length; ++i){
            double magicNumber = Math.random();
            animals[i] = animals[random.nextInt(carry_over)];
            if (magicNumber < crossoverRate){
                MutableMLP parent2 = animals[random.nextInt(carry_over)];
                animals[i] = animals[i].crossover(parent2);
            }
            if (magicNumber < mutationRate)
                animals[i] = animals[i].mutate();
        }
        setLabels();
    }

    public MutableMLP[] nextGeneration(){
        MutableMLP[] generation = new MutableMLP[animals.length];
        for (int i = 0; i < carry_over; ++i){
            int sampleIndex = getSampleIndex();
            if (sampleIndex >= 128)
                sampleIndex = 127;
            generation[i] = animals[sampleIndex];
        }
        return generation;
    }

    public void setLabels(){
        ArrayList<String> label = new ArrayList<>();
        Collections.addAll(label, allLabels);
        for (int i = 0; i < num_animals; ++i){
            if (animals[i] != null)
                animals[i].setLabels(label);
        }
    }

    public void evaluate() throws InterruptedException {
        populateWorkQueue();
        for (int i = 0; i < num_cores; ++i){
            Thread evaluator = new Thread(()->{
                while(!this.toWork.isEmpty()){
                    try {
                        MutableMLP current = toWork.take();
                        current.score = evaluate(current);
                        this.worked.add(current);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            evaluator.start();
            this.threads[i] = evaluator;
        }
        while(!threadsFinished()){ /* Wait for Threads to Finish */ }
        deloadWork();
        Arrays.sort(animals);
    }

    public int getSampleIndex(){
        double sampleLocal = Math.random();
        int index = 0;
        if (sampleLocal < topTenChance)
            index = random.nextInt(animals.length/10);
        else if (sampleLocal < topFiftyChance)
            index = random.nextInt(animals.length/2);
        else {
            index = random.nextInt(animals.length - 1);
        }
        return index;
    }

    private double evaluate(MutableMLP animal){
        double num_correct = 0;
        for (int i = 0; i < testData.length; ++i){
            Duple<String, Drawing> element = testData[i];
            String answer = animal.classify(element.getSecond());
            if (answer.equals(element.getFirst()))
                num_correct++;
        }
        return num_correct/testData.length;
    }

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        //read in drawing files
        Duple<Duple<String, Drawing>[], String[]> info = getDrawings();
        Duple<String, Drawing>[] drawigns = info.getFirst();
        String[] labels = info.getSecond();
        //build ecosystem
        System.out.print("\rBuilding Ecosystem...");
        Ecosystem ecosystem = new Ecosystem(1000, drawigns, labels);
        //run
        System.out.print("\rRunning...");
        ecosystem.run();
    }

    public static Duple<Duple<String, Drawing>[], String[]> getDrawings() throws FileNotFoundException {
        String path = System.getProperty("user.dir") + File.separator+ "PerceptronTrainingData" + File.separator +"TrainingData8";
        File data = new File(path);
        System.out.print("Loading Data...");
        SampleData loaded = SampleData.parseDataFrom(data);
        Duple<String, Drawing>[] drawings = new Duple[loaded.numDrawings()];
        for (int i = 0; i < loaded.numDrawings(); ++i){
            drawings[i] = loaded.getLabelAndDrawing(i);
        }
        return new Duple<>(drawings, loaded.allLabels().toArray(new String[]{}));
    }
}
