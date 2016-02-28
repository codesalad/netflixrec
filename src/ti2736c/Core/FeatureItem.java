package ti2736c.Core;

/**
 * Created by codesalad on 28-2-16.
 */
public class FeatureItem implements Comparable<FeatureItem> {

    double distance;
    FeatureVector vector;

    public FeatureItem(FeatureVector vector, double distance) {
        this.vector = vector;
        this.distance = distance;
    }

    public FeatureVector getVector() {
        return vector;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public int compareTo(FeatureItem o) {
        return Double.compare(getDistance(), o.getDistance());
    }
}