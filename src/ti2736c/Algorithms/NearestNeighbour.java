package ti2736c.Algorithms;

import ti2736c.Core.*;
import ti2736c.Drivers.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by codesalad on 28-2-16.
 */
public class NearestNeighbour {

    public static RatingList predictRatings(RatingList inputList, RatingList outputList) {

        // Convert data list to list of feature vectors.
        LinkedList<FeatureVector> dataSet = new LinkedList<>();
        System.out.println("Converting data set into Feature Vectors...");
        for (int i = 0; i < inputList.size(); i++) {
            Rating r = inputList.get(i);
            FeatureVector vector = new FeatureVector(r.getUser().getIndex(), r.getMovie().getIndex(), r.getRating());
            vector.add((double) r.getUser().getAge()); //  age
            vector.add((double) r.getUser().getProfession()); // profession
            vector.add((r.getUser().isMale()) ? 1.0 : 0.0); // gender
            vector.add((double) r.getMovie().getIndex()); //movieindex
            dataSet.add(vector);
        }

        // OutputList contains unrated ratings.
        // Convert these into feature vectors and get NNs
        for (int i = 0; i < outputList.size(); i++) {
            Rating r = outputList.get(i);
            FeatureVector toClassify = new FeatureVector(r.getUser().getIndex(), r.getMovie().getIndex(), r.getRating());
            // add info about user.
            toClassify.add((double) r.getUser().getAge()); //age
            toClassify.add((double) r.getUser().getProfession()); // profession
            toClassify.add((r.getUser().isMale()) ? 1.0 : 0.0); // gender
            toClassify.add((double) r.getMovie().getIndex()); //movieindex
            List<FeatureItem> res = kNearestNeighbors(Config.NN_k, toClassify, dataSet);

            double predMean = ratingFromNeighbours(r.getMovie().getIndex(), res);
            double prediction = predMean + r.getUser().getBias() + r.getMovie().getBias();
            r.setRating(Math.round(prediction));
            if (Config.ALLOW_STATUS_OUTPUT)
                System.out.printf("\rPredicting: %.1f%%", ((float) (i+1) / outputList.size()) * 100);
        }


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