package ti2736c.Drivers;

import ti2736c.Algorithms.NearestNeighbour;
import ti2736c.Algorithms.RMSE;
import ti2736c.Core.RatingList;

/**
 * Created by codesalad on 27-2-16.
 */
public class NNDriver {

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
        RatingList predictions = NearestNeighbour.predictRatings(trainingSet, testSet);

        if (Config.ALLOW_WRITE)
            predictions.writeResultsFile(Config.outputFile);

        // Verify the predictions.
        RMSE.calcPrint(predictions, verificationSet);
        long endTime = System.currentTimeMillis();
        System.out.println("Duration: " + (endTime - startTime) / 1000 + "s" );
    }
}
