package ti2736c.Drivers;

import ti2736c.Algorithms.Example;
import ti2736c.Algorithms.RMSE;
import ti2736c.Core.MovieList;
import ti2736c.Core.RatingList;
import ti2736c.Core.UserList;

/**
 * Created by codesalad on 27-2-16.
 */
public class ExampleDriver {
    public static void main(String[] args) {
        /* Initialize configs */
        Config.getInstance().read();

        Example exampleAlgorithm = new Example();

        UserList users = Data.getInstance().getUserList();
        MovieList movies = Data.getInstance().getMovieList();

        // Use known data to train.
        RatingList trainingSet = Data.getInstance().getTrainingSet();

        // Test set: this serves as a verifiable PredictionList
        // The predictions algorithm will throw out a new predictions list that differs from this one.
        RatingList verificationSet = Data.getInstance().getVerificationSet();

        // Contains <user id, movie id>, ratings set at 0.
        RatingList testSet = Data.getInstance().getTestSet();

        // Predict ratings.
        RatingList predictions = exampleAlgorithm.predictRatings(users, movies, trainingSet, testSet);

        System.out.println(predictions.get(0));

        if (!Config.TRAINING_MODE)
            predictions.writeResultsFile(Config.outputFile);

        // Verify the predictions.
        System.out.println(RMSE.calcString(predictions, verificationSet));
    }
}
