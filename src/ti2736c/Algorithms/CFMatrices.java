package ti2736c.Algorithms;

import ti2736c.Core.MovieList;
import ti2736c.Core.Rating;
import ti2736c.Core.RatingList;
import ti2736c.Core.UserList;
import ti2736c.Drivers.Config;
import ti2736c.Drivers.Data;

/**
 * Created by codesalad on 4-3-16.
 */
public class CFMatrices {
    public static RatingList predictRatings(UserList users, MovieList movies, RatingList inputList, RatingList outputList) {

        //create utility matrix
        System.out.println("Creating utility matrix (row: movie; col: user) ...");
        double[][] utility = new double[movies.size()][users.size()];

        inputList.forEach(r -> {
            utility[r.getMovie().getIndex() - 1][r.getUser().getIndex()-1] = r.getRating();
        });

        for (int i = 0; i < outputList.size(); i++) {
            Rating toRate = outputList.get(i);

            double bxi = Data.getInstance().getMean()
                    + toRate.getUser().getBias() + toRate.getMovie().getBias();

            double numerator = 0.0;
            double denominator = 0.0;

            int q = toRate.getMovie().getIndex() - 1;
            int c = toRate.getUser().getIndex() - 1;
            for (int r = 0; r < utility.length; r++) {
                if (utility[r][c] > 0.0) {
                    double bxj = Data.getInstance().getMean()
                            + Data.getInstance().getMovieList().get(r).getBias()
                            + toRate.getUser().getBias();

                    double distance = pearson(utility, q,r);
                    numerator += (utility[r][c] - bxj) * distance;
                    denominator += distance;
                }
            }

            if (numerator == 0 || denominator == 0) {
                toRate.setRating(Math.round(bxi));
            } else {
                toRate.setRating(Math.round(bxi + (numerator/denominator)));
            }



            if (Config.ALLOW_STATUS_OUTPUT)
                System.out.printf("\rPredicting: %.1f%%", ((float) (i+1) / outputList.size()) * 100);
        }

        return outputList;
    }

    public static double cosineSimRows(double[][] utility, int query, int other) {
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int c = 0; c < utility[0].length ; c++) {
            dot += utility[query][c] * utility[other][c];
            normA += Math.pow(utility[query][c], 2);
            normB += Math.pow(utility[other][c], 2);
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public static double pearson(double[][] utility, int query, int other) {
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int c = 0; c < utility[0].length ; c++) {
            dot += (utility[query][c] - Data.getInstance().getMovieList().get(query).getMean())
                    * (utility[other][c] - Data.getInstance().getMovieList().get(other).getMean());
            normA += Math.pow((utility[query][c] - Data.getInstance().getMovieList().get(query).getMean()), 2);
            normB += Math.pow((utility[other][c] - Data.getInstance().getMovieList().get(other).getMean()), 2);
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
