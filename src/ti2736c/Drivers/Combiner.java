package ti2736c.Drivers;

import ti2736c.Algorithms.CFMatrices2;
import ti2736c.Algorithms.LFM;
import ti2736c.Core.Rating;
import ti2736c.Core.RatingList;

import java.util.ArrayList;

/**
 * Created by codesalad on 6-3-16.
 */
public class Combiner {
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

        System.out.print(">Starting Latent Factor Model algorithm... ");
        ArrayList<Double> lfmresults = LFM.predictRatings(Data.getInstance().getUserList(), Data.getInstance().getMovieList(), Data.getInstance().getRatingList(), Data.getInstance().getPredictionList());
        System.out.print(" - done.\n");

        System.out.print(">Starting CF item-item algorithm... ");
        ArrayList<Double> cfresults = CFMatrices2.predictRatings(Data.getInstance().getUserList(), Data.getInstance().getMovieList(), Data.getInstance().getRatingList(), Data.getInstance().getPredictionList());
        System.out.print(" - done.\n");

        for (int i = 0; i < Data.getInstance().getPredictionList().size(); i++) {
            Rating toRate = Data.getInstance().getPredictionList().get(i);
            double a = lfmresults.get(i);
            double b = cfresults.get(i);
            toRate.setRating(0.7 * a + 0.3 * b);
        }

        Data.getInstance().getPredictionList().writeResultsFile(Config.outputFile);

//        for (int i = 0; i < 50; i++) {
//            double a = lfmresults.get(i);
//            double b = cfresults.get(i);
//            System.out.println("LFM: " + a + "\tCF: " + b + "\tprediction: " + (0.7 * a + 0.3 * b) + "\tactual: " + verificationSet.get(i).getRating());
//        }

//        RMSE.calcPrint(testSet, verificationSet);

        long endTime = System.currentTimeMillis();
        System.out.println("Duration: " + (endTime - startTime) / 1000 + "s" );

    }
}
