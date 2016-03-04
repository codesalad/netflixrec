package ti2736c.Algorithms;

import ti2736c.Core.*;
import ti2736c.Drivers.Config;
import ti2736c.Drivers.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by codesalad on 3-3-16.
 */
public class CF_u2u extends CollaborativeFiltering {

    /**
     * Predicts new ratings for user-movie pairs.
     * @param inputList RatingList. Contains known ratings.
     * @param outputList RatingList. Contains yet-to-predict ratings (init at 0.0).
     * @return outputList containing predicted ratings.
     */
    public RatingList predictRatings(UserList users, MovieList movies, RatingList inputList, RatingList outputList) {
        System.out.println("Running user-user CF_u2u algorithm");
        // Create utility matrix.
        System.out.println("Creating utility matrix (row: user; col: movie) ...");
        Matrix utility = new Matrix(users.size(), movies.size());
        inputList.forEach(r -> {
            utility.set(r.getUser().getIndex() - 1, r.getMovie().getIndex()-  1, r.getRating());
        });

        int emptyCandidates = 0;
        int emptyNeighbours = 0;

        for (int i = 0; i < outputList.size(); i++) {
//            System.out.println("\n----------------------------------------------------------\n");
            Rating toRate = outputList.get(i);
//            System.out.println("To rate: " + toRate.getMovie().getIndex() + "\t" + toRate.getUser());
            ArrayList<Integer> candidates = (ArrayList<Integer>) selectCandidates(utility, toRate, Config.CF_threshold);

            if (candidates.isEmpty()) emptyCandidates++;

            List<FeatureItem> neighbours = kNearestNeighbours(Config.NN_k, toRate.getUser().getIndex() - 1, candidates, utility);

            if (neighbours.isEmpty()) emptyNeighbours++;

            toRate.setRating(calculateRating(toRate, neighbours, utility));
            if (Config.ALLOW_STATUS_OUTPUT)
                System.out.printf("\rPredicting: %.1f%%", ((float) (i+1) / outputList.size()) * 100);
        }

        System.out.println("\nTotal empty candidates: " + emptyCandidates + "\tTotal empty Neighbours: " + emptyNeighbours);

        return outputList;
    }

    // take into account distance
    @Override
    public double calculateRating(Rating toRate, List<FeatureItem> neighbours, Matrix utility) {
        double bxi = Data.getInstance().getMean()
                + toRate.getUser().getBias() + toRate.getMovie().getBias();

        if (neighbours.isEmpty())
            return Math.round(bxi);

        double numerator = 0.0;
        double denominator = 0.0;

//        double bxi = toRate.getMovie().getMean() + toRate.getUser().getBias() + toRate.getMovie().getBias();
//
        for (FeatureItem item : neighbours) {
            double bxj = Data.getInstance().getMean()
                    + Data.getInstance().getUserList().get(item.getIndex()).getBias()
                    + toRate.getMovie().getBias();
            numerator += (utility.get(item.getIndex(), toRate.getMovie().getIndex() - 1)
                    - bxj) * item.getDistance();
            denominator += item.getDistance();
        }

        return Math.round(bxi + (numerator/denominator));
    }

    @Override
    public List<Integer> selectCandidates(Matrix utility, Rating toRate, double threshold) {
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

//        if (candidates.isEmpty())
//            System.err.println("No candidates found, lower threshold or increase data set");

        return candidates;
    }
}
