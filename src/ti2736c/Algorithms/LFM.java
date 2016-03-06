package ti2736c.Algorithms;


import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import ti2736c.Core.MovieList;
import ti2736c.Core.RatingList;
import ti2736c.Core.UserList;
import ti2736c.Drivers.Data;

/**
 * Created by codesalad on 5-3-16.
 */
public class LFM {

    /* Max epochs to run */
    private static int EPOCHS = 150;

    /* Length of feature matrices */
    private static int FEATURE_LENGTH = 20;

    /* Learning rate */
    private static double ALPHA = 0.0035;

    /* Regulate value */
    private static double BETA = 0.02;

    /* Value to stop at */
    private static double EPSILON = 0.0002;

    public static RatingList predictRatings(UserList users, MovieList movies, RatingList inputList, RatingList outputList) {
        double mean = Data.getInstance().getMean();

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

        for (int m = 0; m < movies.size(); m++) {
            for (int f = 0; f < FEATURE_LENGTH; f++) {
                movieFactors.setEntry(m, f, 0.1);
            }
        }

        for (int f = 0; f < FEATURE_LENGTH; f++) {
            for (int u = 0; u < users.size(); u++) {
                userFactors.setEntry(f, u, 0.1);
            }
        }

        // create movie mean matrix
        System.out.println("Creating movie mean matrix (row: movie) ...");
        double[] avgMovieRatings = new double[movies.size()];

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
        double[] avgUserRatings = new double[users.size()];
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

        double rmse = 1.0;

        // loop through epochs
        for (int e = 0; e < EPOCHS; e++) {

            System.out.println("Beginning epoch: " + (e + 1));
            for (int i = 0; i < movies.size(); i++) { // rows are movies
                for (int j = 0; j < users.size(); j++) { // columns are users
                    if (utility.getEntry(i, j) > 0.0) {
                        // Calculate squared error:
                        double predTemp = movieFactors.getRowVector(i)
                                .dotProduct(userFactors.getColumnVector(j));

                        double eij = utility.getEntry(i, j) - predTemp;
                        for (int k = 0; k < FEATURE_LENGTH; k++) {
                            // Update factor matrices according to error

                            // old values
                            double movieCurrent = movieFactors.getEntry(i, k);
                            double userCurrent = userFactors.getEntry(k, j);

                            // gradient decent (differentiate, etc)
                            double movieUpdated = movieCurrent + (ALPHA * (2 *eij*userCurrent - (BETA*movieCurrent) ) );
                            double userUpdated = userCurrent + (ALPHA * (2*eij*movieCurrent - (BETA*userCurrent) ) );

                            movieFactors.setEntry(i, k, movieUpdated);
                            userFactors.setEntry(k, j, userUpdated);
                        }
                    }
                }
            }
        }

        // this is the resulting matrix
        RealMatrix result = movieFactors.multiply(userFactors);

//        for (int i = 0; i < movies.size(); i++) { // rows are movies
//            for (int j = 0; j < users.size(); j++) { // columns are users
//                System.out.print(result.getEntry(i, j) + "\t");
//            }
//            System.out.println();
//        }

        outputList.forEach(r -> {
            r.setRating(result.getEntry(r.getMovie().getIndex() - 1, r.getUser().getIndex() - 1));
        });

        return outputList;
    }
}
