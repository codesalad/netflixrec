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
public class CF {

    /**
     * Predicts new ratings for user-movie pairs.
     * @param inputList RatingList. Contains known ratings.
     * @param outputList RatingList. Contains yet-to-predict ratings (init at 0.0).
     * @return outputList containing predicted ratings.
     */
    public static RatingList predictRatings(UserList users, MovieList movies, RatingList inputList, RatingList outputList) {

        if (Config.CF_method.equals(Config.CF_methods[0]))
            return userToUser(users, movies, inputList, outputList);
        else if (Config.CF_method.equals(Config.CF_methods[1]))
            throw new NotImplementedException();

        // Return predictions
        return outputList;
    }

    public static RatingList userToUser(UserList users, MovieList movies, RatingList inputList, RatingList outputList) {
        System.out.println("Running user-user CF algorithm");
        // Create utility matrix.
        System.out.println("Creating utility matrix (row: user; col: movie) ...");
        Matrix utility = new Matrix(users.size(), movies.size());
        inputList.forEach(r -> {
            utility.set(r.getUser().getIndex() - 1, r.getMovie().getIndex()-  1, r.getRating());
        });

        for (int i = 0; i < outputList.size(); i++) {
//            System.out.println("\n----------------------------------------------------------\n");
            Rating toRate = outputList.get(i);
//            System.out.println("To rate: " + toRate.getMovie().getIndex() + "\t" + toRate.getUser());
            ArrayList<Integer> candidates = (ArrayList<Integer>) selectCandidates(utility, toRate, Config.CF_threshold);
            List<FeatureItem> neighbours = kNearestNeighbours(Config.NN_k, toRate, candidates, utility);
            toRate.setRating(calculateRating(toRate, neighbours, utility));
            if (Config.ALLOW_STATUS_OUTPUT)
                System.out.printf("\rPredicting: %.1f%%", ((float) (i+1) / outputList.size()) * 100);
        }

        return outputList;
    }

    // take into account distance
    public static double calculateRating(Rating toRate, List<FeatureItem> neighbours, Matrix utility) {
        double rating = 0.0;

        if (neighbours.isEmpty()) {
            return toRate.getMovie().getMean();
        }

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

        // analyze user. add col indices of movies that have been rated.
        for (int c = 0; c < utility.cols(); c++) {
            if (utility.get(user.getIndex() - 1, c) > 0)
                cols.add(c);
        }

        // at least % same cols rated as user, defined in Config
        int tVal = (int) Math.round(threshold * cols.size());

        // Search for users who HAVE seen the movie AND have some columns in common with toRate user.
        for (int r = 0; r < utility.rows(); r++) {
            if (r != user.getIndex() - 1) {
                int sum = 0;
                for (int c = 0; c < utility.cols(); c++) {
                    // IF the movie is RATED AND the rating deviation is not more than specified value in CONFIG.
                    if (utility.get(r, c) > 0 && Math.abs(utility.get(user.getIndex() - 1, c) - utility.get(r, c)) <= Config.CF_user_deviation) {
                        if (cols.contains(c)) sum++;
                    }
                }
                if (sum >= tVal && utility.get(r, movie.getIndex() - 1) > 0) { // could be improved: larger sum the better
                    candidates.add(r); // add the INDEX of utility matrix
//                    System.out.println(">candidate rated: " + sum + " movies that the user also did");
                }
            }
        }

        if (candidates.isEmpty())
            System.err.println("No candidates found, lower threshold or increase data set");

        return candidates;
    }

    public static List<FeatureItem> kNearestNeighbours(int k, Rating toRate,  List<Integer> candidates, Matrix utility) {
        ArrayList<FeatureItem> items = new ArrayList<>();

        if (candidates.isEmpty())
            return items;

        int qIndex = toRate.getUser().getIndex() - 1; // query index

        candidates.forEach(r -> { // index in utility matrix
            double distance = 0.0;
            if (Config.NN_distance_metric.equals("euclid"))
                distance = euclid(utility, qIndex, r);
            else if (Config.NN_distance_metric.equals("cosine"))
                distance = cosine(utility, qIndex, r);
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

    public static double euclid(Matrix utility, int rowQuery, int rowOther) {
        double squaredD = 0.0;
        for (int c = 0; c < utility.cols(); c++) {
            squaredD += Math.pow(utility.get(rowQuery, c) - utility.get(rowOther, c), 2);
        }
        return Math.sqrt(squaredD);
    }

    public static double cosine(Matrix utility, int rowQuery, int rowOther) {
        double dot = 0.0;
        for (int c = 0; c < utility.cols(); c++) {
            dot += (utility.get(rowQuery, c) * utility.get(rowOther, c));
        }
        return dot / utility.cols();
    }
}
