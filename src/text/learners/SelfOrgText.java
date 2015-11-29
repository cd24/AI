package text.learners;


import handwriting.learners.som.SOMPoint;
import search.core.Histogram;

public class SelfOrgText {
    int height, width;
    Histogram[][] data;
    public double learning_rate = 0.05,
            learning_radius = 2;
    private String[][] labels;
    private boolean mapFull = false;

    public SelfOrgText(int height, int width){
        this.width = width;
        this.height = height;
        this.data = new Histogram[height][width];
        this.labels = new String[height][width];

        for (int i = 0; i < height; ++i){
            for (int j = 0; j < width; ++j){
                data[i][j] = new Histogram<String>();
            }
        }
    }

    public SOMPoint bestFor(Histogram<String> sample){
        SOMPoint best = new SOMPoint(height/2, width/2);
        for (int i = 0; i < height; ++i){
            for (int j = 0; j < width; ++j){
                Histogram<String> element = data[i][j];
                if (data[best.x()][best.y()].distanceTo(sample) > element.distanceTo(sample))
                    best = new SOMPoint(i, j);
            }
        }
        return best;
    }

    public void train(Histogram<String> sample){
        SOMPoint best = bestFor(sample);
        SOMPoint[] neighbors = best.getNeighbors(learning_radius, width, height);
        trainMap(sample, best, learning_rate);
        for (SOMPoint neighbor : neighbors){
            double learning = learning_rate*(neighbor.distanceTo(best.x(), best.y())/learning_radius);
            trainMap(sample, neighbor, learning);
        }
    }

    public void trainMap(Histogram<String> example, SOMPoint cell, double scale){
        data[cell.x()][cell.y()] = data[cell.x()][cell.y()].combine(example, scale);
    }

    public void applyLabels(Histogram<String> sample, String label){
        SOMPoint toLabel = bestFor(sample);
        labels[toLabel.x()][toLabel.y()] = label;
    }

    public void setRemainingLabels(){
        if (mapFull){
            return;
        }
        for (int i = 0; i < height; ++i){
            for (int j = 0; j < width; ++j){
                if (labels[j][i] == null){
                    SOMPoint bestMatch = findCounterPart(new SOMPoint(i, j));
                    labels[j][i] = labels[bestMatch.y()][bestMatch.x()];
                }
            }
        }
        mapFull = true;
    }

    public SOMPoint findCounterPart(SOMPoint point){
        double min_dist = Double.MAX_VALUE;
        SOMPoint minimum = new SOMPoint(0, 0);
        Histogram<String> value = data[point.y()][point.x()];
        for (int i = 0; i < width; ++i){
            for (int j = 0; j < height; ++j){
                double distance = value.distanceTo(data[j][i]);
                if (distance < min_dist && labels[j][i] != null){
                    min_dist = distance;
                    minimum = new SOMPoint(i, j);
                }
            }
        }
        return minimum;
    }

    public String classify(Histogram<String> data){
        SOMPoint point = bestFor(data);
        System.out.print("Node (x, y) (" + point.x() + ", " + point.y() + ")");
        return labels[point.x()][point.y()];
    }
}
