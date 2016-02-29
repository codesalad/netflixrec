package ti2736c.Core;

import ti2736c.Drivers.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by codesalad on 28-2-16.
 */
public class FeatureVector extends ArrayList<Double> {
    int userIndex;
    int movieIndex;
    double rating;

    public FeatureVector(int uindex, int mindex, double r) {
        userIndex = uindex;
        movieIndex = mindex;
        rating = r;
    }

    public int getUserIndex() {
        return userIndex;
    }

    public int getMovieIndex() {return movieIndex; }

    public double getRating() {
        return rating;
    }

    public double dot(List<Double> other) {
        assert (size() == other.size());

        double result = 0.0;
        for (Double a : this)
            for (Double b : other)
                result += result + (a * b);

        return result;
    }

    public double dist(List<Double> other) {
        switch (Config.NN_distance_metric) {
            case "euclid": return euclidDist(other);
            default: System.err.println("Not a valid distance metric."); break;
        }

        return 0.0;
    }

    /**
     * Calculates euclidean distance between this and other vector.
     * @param other Other vector to calculate distance with.
     * @return Distance between this and the other vector.
     */
    public double euclidDist(List<Double> other) {
        assert (size() == other.size() && size() != 0 && other.size() != 0);

        double distance = 0.0;
        for (int i = 0; i < size(); i++) {
            distance += Math.pow(get(i) - other.get(i), 2);
        }

        return Math.sqrt(distance);
    }

//    public double

    /**
     * Converts this object to a String object.
     */
    @Override
    public String toString() {
        return "<" + rating + ", " + super.toString() + ">";
    }
}