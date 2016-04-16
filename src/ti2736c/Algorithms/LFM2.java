package ti2736c.Algorithms;


import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import ti2736c.Core.MovieList;
import ti2736c.Core.Rating;
import ti2736c.Core.RatingList;
import ti2736c.Core.UserList;
import ti2736c.Drivers.Config;
import ti2736c.Drivers.Data;

import java.util.ArrayList;

/**
 * Created by codesalad on 5-3-16.
 */
public class LFM2 {

    /* Max epochs to run */
    private static int EPOCHS = 150;

    /* Length of feature matrices */
    private static int FEATURE_LENGTH = 10;

    /* Learning rate */
    private static double ALPHA = 0.002;

    /* Regulate value */
    private static double BETA = 0.04;

    /* Value to stop at */
    private static double EPSILON = 0.0002;

    public static ArrayList<Double> predictRatings(UserList users, MovieList movies, RatingList inputList, RatingList outputList) {
        ArrayList<Double> result = new ArrayList<>();

        double mean = Data.getInstance().getMean();
        System.out.println(mean);


        System.out.println("Creating utility matrix...");
        RealMatrix utility = new BlockRealMatrix(movies.size(), users.size());

        inputList.forEach(r -> {
            utility.setEntry(r.getMovie().getIndex() - 1,
                    r.getUser().getIndex() - 1, r.getRating());;
        });

        System.out.println("Creating movie-factors list...");
        RealMatrix movieFactors = new BlockRealMatrix(movies.size(), FEATURE_LENGTH);

        System.out.println("Creating user-factors list...");
        RealMatrix userFactors = new BlockRealMatrix(FEATURE_LENGTH, users.size());

        double init = (double) 1/50 + (Math.random() / 50);

        System.out.println("init: " + init);
        for (int m = 0; m < movies.size(); m++) {
            for (int f = 0; f < FEATURE_LENGTH; f++) {
                movieFactors.setEntry(m, f, (double) 1/50 + (Math.random() / 50));
            }
        }

        for (int f = 0; f < FEATURE_LENGTH; f++) {
            for (int u = 0; u < users.size(); u++) {
                userFactors.setEntry(f, u, (double) 1/50 + (Math.random() / 50));
            }
        }

        double[] avgMovieRatings = null;
        double[] avgUserRatings = null;

        if (Config.LF_BIAS) {
            System.out.println("Bias is ON");
            // create movie mean matrix
            System.out.println("Creating movie mean matrix (row: movie) ...");
            avgMovieRatings = new double[movies.size()];


            for (int r = 0; r < movies.size(); r++) {
                double sum = 0.0;
                int total = 0;
                for (int c = 0; c < users.size(); c++) {
                    if (utility.getEntry(r, c) > 0.0) {
                        sum += utility.getEntry(r, c);
                        total++;
                    }
                }
                if (total == 0) total = 1;
                avgMovieRatings[r] = sum / total;
            }

            System.out.println("Creating user mean matrix (row: user) ... ");
            avgUserRatings = new double[users.size()];
            for (int c = 0; c < users.size(); c++) {
                double sum = 0.0;
                int total = 0;
                for (int r = 0; r < movies.size(); r++)
                    if (utility.getEntry(r, c) > 0.0) {
                        sum += utility.getEntry(r, c);
                        total++;
                    }
                if (total == 0) total = 1;
                avgUserRatings[c] = sum / total;
            }
        }
        for (int k = 0; k < FEATURE_LENGTH; k++) {
            System.out.printf("\rBeginning feature: %d/%d", (k+1), FEATURE_LENGTH);
        // loop through epochs
        for (int e = 0; e < EPOCHS; e++) {


            for (int i = 0; i < movies.size(); i++) { // rows are movies
                for (int j = 0; j < users.size(); j++) { // columns are users
                    if (utility.getEntry(i, j) > 0.0) { // check if rated.
                        // Calculate squared error:
                        double predTemp = movieFactors.getRowVector(i)
                                .dotProduct(userFactors.getColumnVector(j));

                        double prediction = predTemp;
                        if (Config.LF_BIAS) {
                            prediction += (mean + (avgMovieRatings[i] - mean)
                                    + (avgUserRatings[j] - mean));
                        }

                        if(prediction > 5.0)
                            prediction = 5.0;
                        else if(prediction < 1.0)
                            prediction = 1.0;

                        double eij = utility.getEntry(i, j) - prediction;


                            // Update factor matrices according to error

                            // old values
                            double movieCurrent = movieFactors.getEntry(i, k);
                            double userCurrent = userFactors.getEntry(k, j);

                            // gradient decent (differentiate, etc)
                            //2*eij..
                            double movieUpdated = movieCurrent + (ALPHA * (eij*userCurrent - (BETA*movieCurrent) ) );
                            double userUpdated = userCurrent + (ALPHA * (eij*movieCurrent - (BETA*userCurrent) ) );

                            movieFactors.setEntry(i, k, movieUpdated);
                            userFactors.setEntry(k, j, userUpdated);
                        }
                    }
                }
            }
        }

        // this is the resulting matrix
        RealMatrix product = movieFactors.multiply(userFactors);

        for (int i = 0; i < outputList.size(); i++) {
            Rating r = outputList.get(i);
            double dot = product.getEntry(r.getMovie().getIndex() - 1, r.getUser().getIndex() - 1);

//            r.setRating(dot + mean + uBias + mBias);
            if (Config.LF_BIAS) {
                double uBias = avgUserRatings[r.getUser().getIndex() - 1] - mean;
                double mBias = avgMovieRatings[r.getMovie().getIndex() - 1] - mean;
                result.add(dot + mean + uBias + mBias);
            } else {
                result.add(dot);
            }
        }

        return result;
    }
}
