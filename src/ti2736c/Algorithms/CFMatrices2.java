package ti2736c.Algorithms;

import ti2736c.Core.*;
import ti2736c.Drivers.Config;
import ti2736c.Drivers.Data;

/**
 * Created by codesalad on 4-3-16.
 */
public class CFMatrices2 {
    public static RatingList predictRatings(UserList users, MovieList movies, RatingList inputList, RatingList outputList) {
        double mean = Data.getInstance().getMean();

        //create utility matrix
        System.out.println("Creating utility matrix (row: movie; col: user) ...");
        double[][] utility = new double[movies.size()][users.size()];

        inputList.forEach(r -> {
            utility[r.getMovie().getIndex() - 1][r.getUser().getIndex()-1] = r.getRating();
        });

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

            // baseline estimate rxi: overall mean + bias user + bias movie
            // bias user = avg user x - overall mean
            double bxi = mean + (avgUserRatings[c] - mean)
                    + (avgMovieRatings[q] - mean);

            double numerator = 0.0;
            double denominator = 0.0;

            for (int r = 0; r < utility.length; r++) { // loop through all rows (movies)
                if (utility[r][c] > 0.0) {
                    double bxj = mean + (avgUserRatings[c] - mean)
                            + (avgMovieRatings[r] - mean);

//                    double distance = pearson(utility, avgMovieRatings, avgUserRatings, q,r);
                    double distance = cosine(utility, q,r);
                    numerator += (utility[r][c] - bxj) * distance;
                    denominator += distance;
                }
            }

            if (numerator == 0 || denominator == 0
                    || Double.isNaN(numerator) || Double.isNaN(denominator)) {
                toRate.setRating(bxi);
            } else {
                toRate.setRating(bxi + (numerator/denominator));
            }

            if (Config.ALLOW_STATUS_OUTPUT)
                System.out.printf("\rPredicting: %.1f%%", ((float) (i+1) / outputList.size()) * 100);

        }
        return outputList;
    }

    public static double euclid(double[][] utility, int query, int other) {
        double distance = 0.0;
        for (int c = 0; c < utility[0].length; c++) {
            distance += Math.pow(utility[query][c] - utility[other][c], 2);
        }
        return distance;
    }

    public static double cosine(double[][] utility, int query, int other) {
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int c = 0; c < utility[0].length ; c++) {
            if (utility[other][c] > 0) {
                dot += utility[query][c] * utility[other][c];
                normA += Math.pow(utility[query][c], 2);
                normB += Math.pow(utility[other][c], 2);
            }
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
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
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
