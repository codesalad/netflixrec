package ti2736c.Algorithms;

import ti2736c.Core.*;
import ti2736c.Drivers.Config;
import ti2736c.Drivers.Data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by codesalad on 14-3-16.
 */
public class CFU2U {
    public static ArrayList<Double> predictRatings(UserList users, MovieList movies, RatingList inputList, RatingList outputList) {
        ArrayList<Double> results = new ArrayList<>();

        double mean = Data.getInstance().getMean();

        //create utility matrix
        System.out.println("Creating utility matrix (row: movie; col: user) ...");
        double[][] utility = new double[movies.size()][users.size()];

        inputList.forEach(r -> {
            utility[r.getMovie().getIndex() - 1][r.getUser().getIndex()-1] = r.getRating();
        });

        //create utility matrix
//        System.out.println("Creating user matrix (row: user; col: user's features) ...");
//        // col: isMale, age, profession
//        double[][] userMatrix = new double[users.size()][4];
//
//        inputList.forEach(r -> {
//            User u = r.getUser();
//            userMatrix[u.getIndex()-1][0] = (u.isMale()) ? 1 : 0; // ismale
//            userMatrix[u.getIndex()-1][1] = u.getAge();
//            userMatrix[u.getIndex()-1][2] = u.getProfession();
//        });

        // create movie mean matrix
        System.out.println("Creating movie mean matrix (row: movie) ...");
        double[] avgMovieRatings = new double[movies.size()];

        for (int r = 0; r < utility.length; r++) {
            double sum = 0.0;
            int total = 0;
            for (int c = 0; c < utility[0].length; c++) {
                if (utility[r][c] > 0.0) {
                    sum += utility[r][c];
                    total++;
                }
            }
            if (total == 0) total = 1;
            avgMovieRatings[r] = sum / total;
        }

        System.out.println("Creating user mean matrix (row: user) ... ");
        double[] avgUserRatings = new double[users.size()];
        for (int c = 0; c < utility[0].length; c++) {
            double sum = 0.0;
            int total = 0;
            for (int r = 0; r < utility.length; r++)
                if (utility[r][c] > 0.0) {
                    sum += utility[r][c];
                    total++;
                }
            if (total == 0) total = 1;
            avgUserRatings[c] = sum / total;
        }

        for (int i = 0; i < outputList.size(); i++) {
            Rating toRate = outputList.get(i);

            int q = toRate.getMovie().getIndex() - 1; // query movie
            int c = toRate.getUser().getIndex() - 1; // user

            // Look for users who have rated the same movie
            LinkedList<Integer> userIndices = new LinkedList<>();
            for (int cc = 0; cc < utility[0].length; cc++) {
                if (utility[q][cc] > 0.0)
                    userIndices.add(cc);
            }

            TreeMap<Double, Integer> neighbours = new TreeMap<>();
            for (int u = 0; u < userIndices.size(); u++) {
                double similarity = 0.0;
                if (Config.CF_UU_SIMILARITY.equals("pearson"))
                    similarity = pearson(utility, avgMovieRatings, avgUserRatings, c, userIndices.get(u));
                else if (Config.CF_UU_SIMILARITY.equals("cosine"))
                    similarity = cosine(utility, c, userIndices.get(u));

                double distance = 1 - similarity;
                neighbours.put(distance, userIndices.get(u));
            }

            // baseline estimate rxi: overall mean + bias user + bias movie
            // bias user = avg user x - overall mean
            double bxi = mean + (avgUserRatings[c] - mean)
                    + (avgMovieRatings[q] - mean);

//            System.out.println("mean: " + mean + " \t " + (avgUserRatings[c] - mean) + "\t" + (avgMovieRatings[q] - mean));

            double numerator = 0.0;
            double denominator = 0.0;

            int k = 0;

            for (Map.Entry<Double, Integer> entry : neighbours.entrySet()) {
                double dist = entry.getKey();
                int index = entry.getValue();

                double bxj = mean + (avgUserRatings[index] - mean)
                        + (avgMovieRatings[q] - mean);

                numerator += (utility[q][index] - bxj) * dist;
                denominator += dist;

                if (k >= Config.CF_UU_THRESHOLD && k >= (Config.CF_UU_KNN * neighbours.size())) break;
                k++;
            }

            if (numerator == 0 || denominator == 0
                    || Double.isNaN(numerator) || Double.isNaN(denominator)) {
//                toRate.setRating(bxi);
                results.add(bxi);
            } else {
                double prediction = bxi + (numerator/denominator);
                prediction = Math.min(Math.max(prediction, 1.0), 5.0);
                results.add(bxi + prediction);
            }

            if (Config.ALLOW_STATUS_OUTPUT)
                System.out.printf("\rPredicting: %.1f%%", ((float) (i+1) / outputList.size()) * 100);
        }

        return results;
    }

    public static double cosine(double[][] utility, int queryUser, int otherUser) {
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int r = 0; r < utility.length ; r++) {
            if (utility[r][queryUser] > 0.0 && utility[r][otherUser] > 0.0) {
                dot += utility[r][queryUser] * utility[r][otherUser];
                normA += Math.pow(utility[r][queryUser], 2);
                normB += Math.pow(utility[r][otherUser], 2);
            }
        }
        double res = dot / (Math.sqrt(normA) * Math.sqrt(normB));
        return (Double.isNaN(res)) ? 0.0 : res;
    }

    public static double pearson(double[][] utility, double[] meanMovies, double[] meanUsers, int query, int other) {
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int c = 0; c < utility[0].length ; c++) {
            dot += (utility[query][c] - meanMovies[query])
                    * (utility[other][c] - meanMovies[other]);
            normA += Math.pow((utility[query][c] - meanMovies[query]), 2);
            normB += Math.pow((utility[other][c] - meanMovies[other]), 2);
        }
        if (normA == 0) normA = 1;
        if (normB == 0) normB = 1;
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}