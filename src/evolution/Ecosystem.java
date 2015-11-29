package evolution;

import handwriting.core.Drawing;
import search.core.Duple;

import java.util.PriorityQueue;
import java.util.Random;

public class Ecosystem {
    int num_animals = 10000,
        num_generation = 1000,
        carry_over = (int) (0.1 * num_animals);
    MutableMLP[] animals;
    double[] ranking;
    double mutationRate = 0.1, crossoverRate = 0.4, topTenChance = 0.7, topFiftyChance = 0.9;
    String[] allLabels;
    Duple<Drawing, String>[] testData;
    Random random;

    public Ecosystem(int size, Duple<Drawing, String>[] data, String[] allLabels){
        this.num_animals = size;
        animals = new MutableMLP[size];
        ranking = new double[size];
        this.allLabels = allLabels;
        this.testData = data;
        for (int i = 0; i < size; ++i){
            animals[i] = new MutableMLP();
        }
        random = new Random();
    }

    public void run() {
        //todo: run
        for (int i = 0; i < num_generation; ++i) {
            evaluate();
            this.animals = nextGeneration();
            repopulate();
        }
    }

    public void repopulate(){
        //assume top ten are correct
        //naive implementation.  Doesn't take into account what each is good at.
        int upperBound = (int) (0.1 * animals.length);
        for (int i = carry_over; i < animals.length; ++i){
            double magicNumber = Math.random();
            animals[i] = animals[random.nextInt(upperBound)];
            if (magicNumber < crossoverRate){
                MutableMLP parent2 = animals[random.nextInt(upperBound)];
                animals[i] = animals[i].crossover(parent2);
            }
            if (magicNumber < mutationRate)
                animals[i].mutate();
        }
    }

    public MutableMLP[] nextGeneration(){
        MutableMLP[] generation = new MutableMLP[animals.length];
        for (int i = 0; i < carry_over; ++i){
            generation[i] = animals[getSampleIndex()];
        }
        return generation;
    }

    public void evaluate(){
        for (int i = 0; i < this.num_animals; ++i){
            ranking[i] = evaluate(animals[i]);
        }
        Duple<MutableMLP[], double[]> sorted = sort(animals, ranking);
        this.animals = sorted.getFirst();
        this.ranking = sorted.getSecond();
    }

    public int getSampleIndex(){
        double sampleLocal = Math.random();
        if (sampleLocal < topTenChance)
            return random.nextInt(animals.length/10);
        else if (sampleLocal < topFiftyChance)
            return random.nextInt(animals.length/2);
        else {
            return random.nextInt(animals.length);
        }
    }

    private double evaluate(MutableMLP animal){
        int num_correct = 0;
        for (int i = 0; i < testData.length; ++i){
            Duple<Drawing, String> element = testData[i];
            String answer = animal.classify(element.getFirst());
            if (answer.equals(element.getSecond()))
                num_correct++;
        }
        return num_correct/testData.length;
    }

    public Duple<MutableMLP[], double[]> sort(MutableMLP[] animal, double[] rankings){
        if (animal.length == 0){
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

    public Duple<MutableMLP[], double[]>[] split(MutableMLP[] animals, double[] rankings){
        int split = animals.length/2;
        MutableMLP[] firstHalfA = new MutableMLP[split], secondHalfA = new MutableMLP[animals.length - split];
        double[] firstHalfB = new double[split], secondHalfB = new double[animals.length - split];
        for (int i = 0; i < animals.length; ++i){
            if (i < split){
                firstHalfA[i] = animals[i];
                firstHalfB[i] = rankings[i];
            }
            else {
                secondHalfA[i] = animals[i];
                secondHalfB[i] = rankings[i];
            }
        }
        Duple<MutableMLP[], double[]>[] splits = new Duple[2];
        splits[0] = new Duple<>(firstHalfA, firstHalfB);
        splits[1] = new Duple<>(secondHalfA, secondHalfB);
        return splits;
    }
}
