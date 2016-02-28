package ti2736c.Algorithms;

import ti2736c.Core.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by codesalad on 28-2-16.
 */
public class NearestNeighbour {

    public static RatingList predictRatings(UserList userList, MovieList movieList, RatingList inputList, RatingList outputList) {

        // Convert data list to list of feature vectors.
        LinkedList<FeatureVector> set = new LinkedList<>();
        inputList.forEach(r -> {
            FeatureVector vector = new FeatureVector(r.getUser().getIndex(), r.getMovie().getIndex(), r.getRating());
            vector.add((double) r.getUser().getAge()); //  age
            vector.add((double) r.getUser().getProfession()); // profession
            vector.add((r.getUser().isMale()) ? 1.0 : 0.0); // gender
            vector.add((double) r.getMovie().getIndex()); //movieindex
            set.add(vector);
        });

        // OutputList contains unrated ratings.
        // Convert these into feature vectors and get NNs
        outputList.forEach(r -> {

        });


        // Return predictions
        return outputList;
    }

    public static List<FeatureItem> kNearestNeighbors(int k, FeatureVector toClassify, List<FeatureVector> features) {
        ArrayList<FeatureItem> items = new ArrayList<>();

        for (FeatureVector fv : features)
            items.add(new FeatureItem(fv, toClassify.euclidDist(fv)));

        Collections.sort(items);
        return items.subList(0, k);
    }

    /**
     * Calculates the predicted rating based on neighbour's ratings.
     * @param neighbours List of FeatureItems, sorted on distance.
     * @param movieIndex the movie that needs prediction.
     * @return Predicted rating.
     */
    public static double ratingFromNeighbours(int movieIndex, List<FeatureItem> neighbours) {
        double mean = neighbours.get(0).getVector().getRating();

        for (int i = 1; i < neighbours.size(); i++) {
            if (neighbours.get(i).getVector().getMovieIndex() == movieIndex) {
            mean = ((double) i / ((double) i + 1.0)) * mean
                    + (1.0 / ((double) i + 1.0))
                    * neighbours.get(i).getVector().getRating();
            }
        }

        return mean;
    }

}