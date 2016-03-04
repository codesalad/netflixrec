package ti2736c.Algorithms;

import ti2736c.Core.*;
import ti2736c.Drivers.Config;
import ti2736c.Drivers.Data;

/**
 * Created by codesalad on 3-3-16.
 */
public class CF_i2i {
    /**
     * Predicts new ratings for user-movie pairs.
     * @param inputList RatingList. Contains known ratings.
     * @param outputList RatingList. Contains yet-to-predict ratings (init at 0.0).
     * @return outputList containing predicted ratings.
     */
    public RatingList predictRatings(UserList users, MovieList movies, RatingList inputList, RatingList outputList) {
        System.out.println("Running item-item CF_i2i algorithm");

        //create utility matrix
        System.out.println("Creating utility matrix (row: movie; col: user) ...");
        Matrix utility = new Matrix(movies.size(), users.size());

        inputList.forEach(r -> {
            utility.set(r.getMovie().getIndex() - 1, r.getUser().getIndex() - 1, r.getRating());
        });

        int emptyCandidates = 0;
        int emptyNeighbours = 0;

        for (int i = 0; i < outputList.size(); i++) {
            Rating toRate = outputList.get(i);

            double bxi = Data.getInstance().getMean()
                    + toRate.getUser().getBias() + toRate.getMovie().getBias();

            double numerator = 0.0;
            double denominator = 0.0;

            for (int r = 0; r < utility.rows(); r++) {
                if (utility.get(r, toRate.getUser().getIndex() - 1) > 0) {
                    double bxj = Data.getInstance().getMean()
                            + Data.getInstance().getMovieList().get(r).getBias()
                            + toRate.getUser().getBias();
                    double distance = Matrix.cosine(utility, toRate.getMovie().getIndex() - 1, r);
                    numerator += (utility.get(r, toRate.getUser().getIndex() - 1)
                            - bxj) * distance;
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

        System.out.println("\nTotal empty candidates: " + emptyCandidates + "\tTotal empty Neighbours: " + emptyNeighbours);

        return outputList;
    }
}
