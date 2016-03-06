package ti2736c.Drivers;

import ti2736c.Core.RatingList;

/**
 * Created by codesalad on 3-3-16.
 */
public class CFDriver {
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
//        RatingList predictions = CFMatrices2.predictRatings(Data.getInstance().getUserList(), Data.getInstance().getMovieList(), trainingSet, testSet);

//        if (Config.ALLOW_WRITE)
//            predictions.writeResultsFile(Config.outputFile);

        // Verify the predictions.
//        RMSE.calcPrint(predictions, verificationSet);
//        long endTime = System.currentTimeMillis();
//        System.out.println("Duration: " + (endTime - startTime) / 1000 + "s" );

//        for (int i = 0; i < predictions.size(); i++) {
//            System.out.println("id: " + predictions.get(i).getMovie().getIndex() + "\t actual: " + verificationSet.get(i).getRating() + " \t predicted: " + predictions.get(i).getRating());
//        }
    }
}
