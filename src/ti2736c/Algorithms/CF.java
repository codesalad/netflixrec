package ti2736c.Algorithms;

import ti2736c.Core.*;
import ti2736c.Drivers.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by codesalad on 3-3-16.
 */
public class CF {

    /**
     * Predicts new ratings for user-movie pairs.
     * @param inputList RatingList. Contains known ratings.
     * @param outputList RatingList. Contains yet-to-predict ratings (init at 0.0).
     * @return outputList containing predicted ratings.
     */
    public static RatingList predictRatings(UserList users, MovieList movies, RatingList inputList, RatingList outputList) {
        // Create utility matrix.
        System.out.println("Creating utility matrix...");
        Matrix utility = new Matrix(users.size(), movies.size());
        inputList.forEach(r -> {
            utility.set(r.getUser().getIndex() - 1, r.getMovie().getIndex()-  1, r.getRating());
        });

        for (int i = 0; i < outputList.size(); i++) {
            Rating toRate = outputList.get(i);
            ArrayList<Integer> candidates = (ArrayList<Integer>) selectCandidates(utility, toRate, Config.CF_threshold);
            List<FeatureItem> neighbours = kNearestNeighbours(Config.NN_k, toRate, candidates, utility);
            toRate.setRating(calculateRating(toRate, neighbours, utility));
            if (Config.ALLOW_STATUS_OUTPUT)
                System.out.printf("\rPredicting: %.1f%%", ((float) (i+1) / outputList.size()) * 100);
        }

        // Return predictions
        return outputList;
    }

    // take into accoutn distance
    public static double calculateRating(Rating toRate, List<FeatureItem> neighbours, Matrix utility) {
        double rating = 0.0;

        for (FeatureItem item : neighbours) {
            rating += utility.get(item.getIndex(), toRate.getMovie().getIndex() - 1);
        }

        rating /= neighbours.size();

        return Math.round(rating);
    }

    public static List<Integer> selectCandidates(Matrix utility, Rating toRate, double threshold) {
        User user = toRate.getUser();
        Movie movie = toRate.getMovie();

        ArrayList<Integer> candidates = new ArrayList<>();

        ArrayList<Integer> cols = new ArrayList<>();

        // analyze user
        for (int c = 0; c < utility.cols(); c++) {
            if (utility.get(user.getIndex() - 1, c) > 0) {
                cols.add(c);
            }
        }

//        System.out.println("matrix col: " + (movie.getIndex() - 1));
//
//        cols.forEach(i -> {
//            System.out.print(i + "\t");
//        });

        int tVal = (int) Math.round(threshold * cols.size()); // at least 20% same cols rated as user
//        System.out.println("atleast: " + tVal);

        // Search for users who HAVE seen the movie AND have some columns in common with toRate user.
        for (int r = 0; r < utility.rows(); r++) {
            if (r != user.getIndex() - 1) {
                int sum = 0;
                boolean rated = false;
                for (int c = 0; c < utility.cols(); c++) {
                    if (utility.get(r, c) > 0) { // IF the movie is RATED:
                        if (c == movie.getIndex() - 1) rated = true;
                        if (cols.contains(c)) sum++;
                    }
                }
                if (sum >= tVal && rated) // could be improved: larger sum the better
                    candidates.add(r); // add the INDEX of utility matrix
            }
        }

//        System.out.println("Checking: ");
//        candidates.forEach(u -> {
//            int rowIndex = u.getIndex() - 1;
//            for (int c = 0; c < utility.cols(); c++) {
//                if (utility.get(rowIndex, c) > 0)
//                    System.out.print(c + "\t");
//            }
//            System.out.println();
//        });

        if (candidates.isEmpty())
            System.err.println("No candidates found, lower threshold or increase data set");

        return candidates;
    }

    public static List<FeatureItem> kNearestNeighbours(int k, Rating toRate,  List<Integer> candidates, Matrix utility) {
        assert (!candidates.isEmpty());

        ArrayList<FeatureItem> items = new ArrayList<>();

        int qIndex = toRate.getUser().getIndex() - 1; // query index

        candidates.forEach(r -> { // index in utility matrix
            double squaredD = 0.0;
            for (int c = 0; c < utility.cols(); c++) {
                squaredD += Math.pow(utility.get(qIndex, c) - utility.get(r, c), 2);
            }
            FeatureItem item = new FeatureItem(r, Math.sqrt(squaredD));
            items.add(item);
        });

        if (items.isEmpty()) {
            items.add(new FeatureItem(toRate.getMovie().getIndex() - 1, 1));
        }

        Collections.sort(items);

        if (k > items.size()) {
//            System.err.println("K is too large, returning all sorted candidates");
            k = items.size();
        }

        return items.subList(0, k - 1);
    }
}