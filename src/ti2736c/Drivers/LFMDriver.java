package ti2736c.Drivers;

import ti2736c.Algorithms.LFM;
import ti2736c.Algorithms.RMSE;
import ti2736c.Core.RatingList;

/**
 * Created by codesalad on 6-3-16.
 */
public class LFMDriver {
    public static void main(String[] args) {
        /* Initialize configs */
        Config.getInstance().read();

        // Use known data to train.
        RatingList trainingSet = Data.getInstance().getTrainingSet();

        // Test set: this serves as a verifiable PredictionList
        // The predictions algorithm will throw out a new predictions list that differs from this one.
        RatingList verificationSet = Data.getInstance().getVerificationSet();

        // Contains <user id, movie id>, ratings set at 0.
        RatingList testSet = Data.getInstance().getTestSet();

        long startTime = System.currentTimeMillis();
        // Predict ratings.
        RatingList predictions = LFM.predictRatings(Data.getInstance().getUserList(), Data.getInstance().getMovieList(), trainingSet, testSet);

        /* testing data

        UserList testUsers = new UserList();
        MovieList testMovies = new MovieList();
        RatingList testRatings = new RatingList();

        User u0 = new User(1, true, 1, 1);
        User u1 = new User(2, true, 1, 1);
        User u2 = new User(3, true, 1, 1);
        User u3 = new User(4, true, 1, 1);
        testUsers.add(u0);
        testUsers.add(u1);
        testUsers.add(u2);
        testUsers.add(u3);

        Movie m0 = new Movie(1, 2011, "m1");
        Movie m1 = new Movie(2, 2011, "m1");
        Movie m2 = new Movie(3, 2011, "m1");
        Movie m3 = new Movie(4, 2011, "m1");
        Movie m4 = new Movie(5, 2011, "m1");
        testMovies.add(m0);
        testMovies.add(m1);
        testMovies.add(m2);
        testMovies.add(m3);
        testMovies.add(m4);

        testRatings.add(new Rating(u0, m0, 5));
        testRatings.add(new Rating(u1, m0, 3));
        testRatings.add(new Rating(u2, m0, 0));
        testRatings.add(new Rating(u3, m0, 1));

        testRatings.add(new Rating(u0, m1, 4));
        testRatings.add(new Rating(u1, m1, 0));
        testRatings.add(new Rating(u2, m1, 0));
        testRatings.add(new Rating(u3, m1, 1));

        testRatings.add(new Rating(u0, m2, 1));
        testRatings.add(new Rating(u1, m2, 1));
        testRatings.add(new Rating(u2, m2, 0));
        testRatings.add(new Rating(u3, m2, 5));

        testRatings.add(new Rating(u0, m3, 1));
        testRatings.add(new Rating(u1, m3, 0));
        testRatings.add(new Rating(u2, m3, 0));
        testRatings.add(new Rating(u3, m3, 4));

        testRatings.add(new Rating(u0, m4, 0));
        testRatings.add(new Rating(u1, m4, 1));
        testRatings.add(new Rating(u2, m4, 5));
        testRatings.add(new Rating(u3, m4, 4));

        RatingList predictions = LFM.predictRatings(testUsers, testMovies, testRatings, testSet);
        /* =====================*/

        if (Config.ALLOW_WRITE)
            predictions.writeResultsFile(Config.outputFile);

        // Verify the predictions.
        RMSE.calcPrint(predictions, verificationSet);
        long endTime = System.currentTimeMillis();
        System.out.println("Duration: " + (endTime - startTime) / 1000 + "s" );

//        for (int i = 0; i < predictions.size(); i++) {
//            System.out.println("id: " + predictions.get(i).getMovie().getIndex() + "\t actual: " + verificationSet.get(i).getRating() + " \t predicted: " + predictions.get(i).getRating());
//        }
    }
}
