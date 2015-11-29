package text.learners;

import handwriting.core.Drawing;
import handwriting.core.RecognizerAI;
import handwriting.core.SampleData;
import handwriting.learners.som.SOMPoint;
import handwriting.learners.som.SelfOrgMap;
import javafx.scene.canvas.Canvas;
import text.core.Sentence;
import text.core.TextLearner;
import text.learners.SelfOrgText;
import java.util.concurrent.ArrayBlockingQueue;

public class SOMText implements TextLearner {
    SelfOrgText map = new SelfOrgText(10, 10);
    int iterations = 5;

    @Override
    public void train(Sentence words, String lbl) {
        for (int i = 0; i < iterations; ++i){
            map.train(words.wordCounts());
        }
        map.applyLabels(words.wordCounts(), lbl);
    }

    @Override
    public String classify(Sentence words) {
        map.setRemainingLabels();
        return map.classify(words.wordCounts());
    }

}
