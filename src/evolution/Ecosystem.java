package evolution;

import handwriting.core.Drawing;
import handwriting.core.SampleData;
import search.core.Duple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class Ecosystem {
    String outPath = "";
    static int num_animals = 100,
        num_generation = 1000,
        carry_over = (int) (0.1 * num_animals),
        num_cores = Runtime.getRuntime().availableProcessors(),
        crossoverType = 0;
    MutableMLP[] animals;
    double[] ranking;
    static boolean crossover_enabled = false;
    static double mutationRate = 0.4, crossoverRate = 0.1, topTenChance = 0.7, topFiftyChance = 0.9;
    String[] allLabels;
    Duple<String, Drawing>[] testData;
    Random random;
    ArrayBlockingQueue<MutableMLP> toWork, worked;
    Thread[] threads = new Thread[num_cores];
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


    public Ecosystem(int size, Duple<String, Drawing>[] data, String[] allLabels) {
        this.num_animals = size;
        this.toWork = new ArrayBlockingQueue<>(this.num_animals);
        this.worked = new ArrayBlockingQueue<>(this.num_animals);
        this.carry_over = (int) (0.1 * num_animals);
        animals = new MutableMLP[num_animals];
        ranking = new double[num_animals];
        this.allLabels = allLabels;
        this.testData = data;
        SampleData dat = new SampleData();

        for (Duple<String, Drawing> aTestData : this.testData) {
            dat.addDrawing(aTestData.getFirst(), aTestData.getSecond());
        }

        createPopulation();
        populateWorkQueue();
        System.out.println("\rEnvironment using " + this.num_cores + " threads for training and evaluation.");

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
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        while(!threadsFinished()){
            double percent = ((double)(this.num_animals - this.toWork.size()))/this.num_animals;
            System.out.printf("\rBuilding Ecosystem... %.2f%s", (percent*100), "%");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("\rEcosystem finished building  at: " + dateFormat.format(new Date()));

        try {
            deloadWork();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        random = new Random();
        setLabels();
    }

    public boolean threadsFinished(){
        boolean stuck = this.toWork.isEmpty();
        for (int i = 0; i < num_cores; ++i){
            if (stuck)
                threads[i].interrupt();
            else if (threads[i].isAlive())
                return false;
        }
        return true;
    }

    public void populateWorkQueue(){
        this.toWork.addAll(Arrays.asList(animals));
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
        System.out.println("Started evolution at: " + dateFormat.format(new Date()));
        for (int i = 0; i < num_generation; ++i) {
            double progress = ((double)(i + 1)/num_generation) * 100;
            System.out.printf("\rGeneration %d of %d: %.2f%s Complete", i+ 1, num_generation, progress, "%");
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
        report(null);
    }

    public void printWeights() throws FileNotFoundException {
        outPath = System.getProperty("user.dir") + File.separator + "EvolutionResults" + mutationRate + "_" + crossoverRate + "_mut" + crossoverType +".csv";
        MutableMLP[] strongest = nextGeneration();
        File saveLoc = new File(outPath);
        PrintWriter writer = new PrintWriter(saveLoc);
        report(writer);
        for (int i = 0; i < carry_over; ++i){
            MutableMLP out = strongest[i];
            writer.println(i);
            writer.println(out.toString());
        }
    }

    public void report(PrintWriter out){
        MutableMLP[] strongest = nextGeneration();
        for (int i = 0; i < carry_over; ++i){
            if (out != null)
                out.println(i + "; " + evaluate(strongest[i]));
            else
                System.out.println(i + "; " + evaluate(strongest[i]));
        }
        if (out != null)
            out.println("-------------------------\n-------------------------\n");
    }

    public void repopulate(){
        //assume top ten are correct
        //naive implementation.  Doesn't take into account what each is good at.
        for (int i = carry_over; i < animals.length; ++i){
            double magicNumber = Math.random();
            animals[i] = animals[random.nextInt(carry_over)];
            if (magicNumber < crossoverRate && crossover_enabled){
                MutableMLP parent2 = animals[random.nextInt(carry_over)];
                animals[i] = animals[i].crossover(parent2, crossoverType);
            }
            if (magicNumber < mutationRate)
                animals[i] = animals[i].mutate(mutationRate);
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
        while(!threadsFinished()){Thread.sleep(100); }
        deloadWork();
        Arrays.sort(animals);
    }

    public int getSampleIndex(){
        double sampleLocal = Math.random();
        int index;
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
        for (Duple<String, Drawing> element : testData) {
            String answer = animal.classify(element.getSecond());
            if (answer.equals(element.getFirst()))
                num_correct++;
        }
        return num_correct/testData.length;
    }

    public static void main(String[] args) throws Exception {
        //read in drawing files
        setupFromArgs(args);

        Duple<Duple<String, Drawing>[], String[]> info = getDrawings();
        Duple<String, Drawing>[] drawigns = info.getFirst();
        String[] labels = info.getSecond();
        //build ecosystem
        System.out.print("\rBuilding Ecosystem...");
        Ecosystem ecosystem = new Ecosystem(num_animals, drawigns, labels);
        ecosystem.run();
    }

    public static Duple<Duple<String, Drawing>[], String[]> getDrawings() throws FileNotFoundException {
        String path = System.getProperty("user.dir") + File.separator+ "PerceptronTrainingData" + File.separator +"TrainingData8";
        File data = new File(path);
        System.out.print("Loading Data...");
        SampleData loaded = SampleData.parseDataFrom(data);
        Duple<String,Drawing>[] drawings = new Duple[loaded.numDrawings()];
        for (int i = 0; i < loaded.numDrawings(); ++i){
            drawings[i] = loaded.getLabelAndDrawing(i);
        }
        return new Duple<>(drawings, loaded.allLabels().toArray(new String[loaded.numLabels()]));
    }

    public static void setupFromArgs(String[] args) throws Exception {
        if (args.length % 2 == 1){
            throw new Exception("Please format the input correctly (.jar -flag value)");
        }
        String crossoverFlag = "-c", mutationFlag = "-m",  generationFlag = "-g", populationFlag = "-p", crossoverTypeFlag = "-ct";
        for (int i = 0; i < args.length; i += 2){
            if (args[i].equals(crossoverFlag))
                crossoverRate = Double.parseDouble(args[i+1]);
            else if (args[i].equals(mutationFlag))
                mutationRate = Double.parseDouble(args[i+1]);
            else if (args[i].equals(generationFlag))
                num_generation = Integer.parseInt(args[i+1]);
            else if (args[i].equals(populationFlag))
                num_animals = Integer.parseInt(args[i+1]);
            else if (args[i].equals(crossoverTypeFlag))
                crossoverType = Integer.parseInt(args[i+1]);
        }
    }
}
