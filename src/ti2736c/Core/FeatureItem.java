package ti2736c.Core;

/**
 * Created by codesalad on 28-2-16.
 */
public class FeatureItem implements Comparable<FeatureItem> {

    double distance;
    Integer index; // (row) index

    public FeatureItem(Integer index, double distance) {
        this.index = index;
        this.distance = distance;
    }

    public Integer getIndex() {
        return index;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public int compareTo(FeatureItem o) {
        return Double.compare(getDistance(), o.getDistance());
    }
}