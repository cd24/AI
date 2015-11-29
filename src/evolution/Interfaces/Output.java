package evolution.interfaces;

/**
 * Created by john on 11/24/15.
 */
public interface Output<T> {
    double distanceTo(Output<T> other);
    double[] distances(Output<T> other, int numDivisions);
    T get();
}
