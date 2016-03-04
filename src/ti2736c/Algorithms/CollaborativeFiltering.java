package ti2736c.Algorithms;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ti2736c.Core.*;
import ti2736c.Drivers.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by codesalad on 3-3-16.
 */
public abstract class CollaborativeFiltering {
    abstract RatingList predictRatings(UserList users, MovieList movies, RatingList inputList, RatingList outputList);

    abstract double calculateRating(Rating toRate, List<FeatureItem> neighbours, Matrix utility);

    abstract List<Integer> selectCandidates(Matrix utility, Rating toRate, double threshold);

    public static List<FeatureItem> kNearestNeighbours(int k, int rowIndex,  List<Integer> candidates, Matrix utility) {
        ArrayList<FeatureItem> items = new ArrayList<>();

        if (candidates.isEmpty())
            return items;

//        int qIndex = toRate.getUser().getIndex() - 1; // query rowIndex

        candidates.forEach(r -> { // rowIndex in utility matrix
            double distance = 0.0;
            if (Config.NN_distance_metric.equals("euclid"))
                distance = Matrix.euclid(utility, rowIndex, r);
            else if (Config.NN_distance_metric.equals("cosine"))
                distance = Matrix.cosine(utility, rowIndex, r);
            else throw new NotImplementedException();


            FeatureItem item = new FeatureItem(r, distance);
            items.add(item);
        });

        if (items.isEmpty()) {
            System.err.println("No neighbours.");
            return items;
        }

        Collections.sort(items);

        if (k > items.size())
            k = items.size();

        return items.subList(0, k - 1);
    }
}
