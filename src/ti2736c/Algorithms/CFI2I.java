package ti2736c.Algorithms;

import ti2736c.Core.MovieList;
import ti2736c.Core.Rating;
import ti2736c.Core.RatingList;
import ti2736c.Core.UserList;
import ti2736c.Drivers.Config;
import ti2736c.Drivers.Data;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by codesalad on 4-3-16.
 */
public class CFI2I {

    public static ArrayList<Double> predictRatings(UserList users, MovieList movies, RatingList inputList, RatingList outputList) {
        ArrayList<Double> results = new ArrayList<>();

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

            TreeMap<Double, Integer> neighbours = new TreeMap<>();

            for (int r = 0; r < utility.length; r++) {
                if (utility[r][c] > 0.0 && r != q) { // if user has rated this movie

//                    System.out.println(movies.get(q).getGenres()
//                            +"\t" + movies.get(r).getGenres() + "\t" +
//                            movies.get(q).genreOverlap(movies.get(r)));

                    double similarity = 0.0;
                    if (Config.CF_II_SIMILARITY.equals("pearson"))
                        similarity = pearson(utility, avgMovieRatings, q, r);
                    else if (Config.CF_II_SIMILARITY.equals("cosine"))
                        similarity = cosine(utility, q, r);
                    else if (Config.CF_II_SIMILARITY.equals("euclid"))
                        similarity = euclid(utility, q, r);

                    double distance = 1 - similarity;

                    if (Config.USE_GENRES) {
                        double sim = movies.get(q).genreOverlap(movies.get(r));
                        if (sim >= Config.CF_II_GENRE_SIM) neighbours.put(distance, r);
                    } else {
                        neighbours.put(distance, r);
                    }
                }
            }

            // baseline estimate rxi: overall mean + bias user + bias movie
            // bias user = avg user x - overall mean
            double bxi = mean + (avgUserRatings[c] - mean)
                    + (avgMovieRatings[q] - mean);

            double numerator = 0.0;
            double denominator = 0.0;

            int k = 0;

            for (Map.Entry<Double, Integer> entry : neighbours.entrySet()) {
                double dist = 1 - entry.getKey();
                int index = entry.getValue();
                double bxj = mean + (avgUserRatings[c] - mean)
                        + (avgMovieRatings[index] - mean);

                numerator += (utility[index][c] - bxj) * dist;
                denominator += dist;

                if (k >= Config.CF_II_THRESHOLD && k >= (Config.CF_II_KNN * neighbours.size())) break;

                k++;
            }

//            System.out.println("size: " + k);

            if (numerator == 0 || denominator == 0
                    || Double.isNaN(numerator) || Double.isNaN(denominator)) {
//                toRate.setRating(bxi);
                results.add(bxi);
            } else {
//                toRate.setRating(bxi + (numerator/denominator));
                double prediction = bxi + (numerator/denominator);
                prediction = Math.min(Math.max(prediction, 1.0), 5.0);
                results.add(prediction);
            }

            if (Config.ALLOW_STATUS_OUTPUT)
                System.out.printf("\rPredicting: %.1f%%", ((float) (i+1) / outputList.size()) * 100);

        }
        return results;
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
            dot += utility[query][c] * utility[other][c];
            normA += Math.pow(utility[query][c], 2);
            normB += Math.pow(utility[other][c], 2);
        }
        double res = dot / (Math.sqrt(normA) * Math.sqrt(normB));
        return (Double.isNaN(res)) ? 0.0 : res;
    }

    public static double pearson(double[][] utility, double[] meanMovies, int queryMovie, int otherMovie) {
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int c = 0; c < utility[0].length ; c++) {
            double dot1 = (utility[queryMovie][c] > 0.0) ? (utility[queryMovie][c] - meanMovies[queryMovie]) : 0;
            double dot2 = (utility[otherMovie][c] > 0.0) ? (utility[otherMovie][c] - meanMovies[queryMovie]) : 0;

            dot += dot1 * dot2;

            normA += dot1 * dot1;
            normB += dot2 * dot2;
        }

        double res = dot / (Math.sqrt(normA) * Math.sqrt(normB));
        return (Double.isNaN(res)) ? 0.0 : res;
    }
}
