package evolution;

import handwriting.core.Drawing;
import handwriting.core.SampleData;
import search.core.Duple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class Ecosystem {
    String outPath = System.getProperty("os.home") + "Desktop/EvolutionResults.csv";
    int num_animals = 10000,
        num_generation = 1000,
        carry_over = (int) (0.1 * num_animals);
    MutableMLP[] animals;
    double[] ranking;
    double mutationRate = 0.1, crossoverRate = 0.4, topTenChance = 0.7, topFiftyChance = 0.9;
    String[] allLabels;
    Duple<String, Drawing>[] testData;
    Random random;

    public Ecosystem(int size, Duple<String, Drawing>[] data, String[] allLabels){
        this.num_animals = size;
        this.carry_over = (int) (0.1 * num_animals);
        animals = new MutableMLP[num_animals];
        ranking = new double[num_animals];
        this.allLabels = allLabels;
        this.testData = data;
        for (int i = 0; i < size; ++i){
            animals[i] = new MutableMLP();
        }
        random = new Random();
        setLabels();
    }

    public void run() {
        for (int i = 0; i < num_generation; ++i) {
            double progress = ((double)i/num_generation) * 100;
            System.out.print("\rGeneration: " + i + " of " + num_generation + ": " + progress + "%");
            evaluate();
            this.animals = nextGeneration();
            repopulate();
        }
        try {
            printWeights();
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't write to file.");
            e.printStackTrace();
        }
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

    public void evaluate(){
        for (int i = 0; i < this.num_animals; ++i){
            animals[i].score = evaluate(animals[i]);
        }
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
        int num_correct = 0;
        for (int i = 0; i < testData.length; ++i){
            Duple<String, Drawing> element = testData[i];
            String answer = animal.classify(element.getSecond());
            if (answer.equals(element.getFirst()))
                num_correct++;
        }
        return num_correct/testData.length;
    }

    public Duple<MutableMLP[], double[]> sort(MutableMLP[] animal, double[] rankings){
        if (animal.length == 1){
            return new Duple<>(animal, rankings);
        }
        else {
            Duple<MutableMLP[], double[]>[] split = split(animal, rankings);
            MutableMLP[] left = split[0].getFirst(), right = split[1].getFirst();
            double[] leftr = split[1].getSecond(), rightr = split[1].getSecond();
            return combine(sort(left, leftr), sort(right, rightr));
        }
    }

    public Duple<MutableMLP[], double[]> combine(Duple<MutableMLP[], double[]> first, Duple<MutableMLP[], double[]> second){

        double[] firstRankings = first.getSecond(),
                    secondRankings = second.getSecond();
        MutableMLP[] firstAnimals = first.getFirst(),
                    secondAnimals = second.getFirst();
        int fIdx = 0, sIdx = 0, newIdx = 0, totalSize = firstRankings.length + secondRankings.length;
        double[] newRankings = new double[totalSize];
        MutableMLP[] newAnimals = new MutableMLP[totalSize];
        int firstLen = first.getFirst().length,
            secondLen = second.getFirst().length;

        while(fIdx < firstLen && sIdx < secondLen){
            double fRank = firstRankings[fIdx], sRank = secondRankings[sIdx];
            if (fRank < sRank){
                newRankings[newIdx] = fRank;
                newAnimals[newIdx] = firstAnimals[fIdx];
                newIdx++;
                fIdx++;
            }
            else {
                newRankings[newIdx] = sRank;
                newAnimals[newIdx] = secondAnimals[sIdx];
                newIdx++;
                sIdx++;
            }
        }

        if (firstLen < secondLen){
            int remainder = secondLen - firstLen;
            for (int i = 0; i < remainder; ++i){
                newAnimals[firstLen + i] = secondAnimals[firstLen + i];
            }
        }
        else {
            int remainder = firstLen - secondLen;
            for (int i = 0; i < remainder; ++i){
                newAnimals[secondLen + i] = secondAnimals[secondLen + i];
            }
        }
        return new Duple<>(newAnimals, newRankings);
    }

    public Duple<MutableMLP[], double[]>[] split(MutableMLP[] ans, double[] rankings){
        int split = ans.length/2;
        MutableMLP[] firstHalfA = new MutableMLP[split], secondHalfA = new MutableMLP[ans.length - split];
        double[] firstHalfB = new double[split], secondHalfB = new double[ans.length - split];
        for (int i = 0; i < ans.length; ++i){
            if (i < split){
                firstHalfA[i] = ans[i];
                firstHalfB[i] = rankings[i];
            }
            else {
                secondHalfA[i - split] = ans[i];
                secondHalfB[i - split] = rankings[i];
            }
        }
        Duple<MutableMLP[], double[]>[] splits = new Duple[2];
        splits[0] = new Duple<>(firstHalfA, firstHalfB);
        splits[1] = new Duple<>(secondHalfA, secondHalfB);
        return splits;
    }

    public static void main(String[] args) throws FileNotFoundException {
        //read in drawing files
        Duple<Duple<String, Drawing>[], String[]> info = getDrawings();
        Duple<String, Drawing>[] drawigns = info.getFirst();
        String[] labels = info.getSecond();
        //build ecosystem
        Ecosystem ecosystem = new Ecosystem(drawigns.length, drawigns, labels);
        //run
        ecosystem.run();
    }

    public static Duple<Duple<String, Drawing>[], String[]> getDrawings() throws FileNotFoundException {
        String path = System.getProperty("user.dir") + File.separator+ "PerceptronTrainingData" + File.separator +"TrainingData8";
        File data = new File(path);
        SampleData loaded = SampleData.parseDataFrom(data);
        Duple<String, Drawing>[] drawings = new Duple[loaded.numDrawings()];
        for (int i = 0; i < loaded.numDrawings(); ++i){
            drawings[i] = loaded.getLabelAndDrawing(i);
        }
        return new Duple<>(drawings, loaded.allLabels().toArray(new String[]{}));
    }
}
