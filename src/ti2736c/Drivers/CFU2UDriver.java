package ti2736c.Drivers;

import ti2736c.Algorithms.CFU2U;
import ti2736c.Algorithms.RMSE;
import ti2736c.Core.RatingList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by codesalad on 3-3-16.
 */
public class CFU2UDriver {
    public static void main(String[] args) {
        /* Initialize configs */
        System.out.println("CONFIG LOADED:\n-----------------\n");
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
        ArrayList<Double> resRatings = null;
        RatingList predictions = null;

        if (Config.TRAINING_MODE) {
            resRatings = CFU2U.predictRatings(Data.getInstance().getUserList(), Data.getInstance().getMovieList(), trainingSet, testSet);
            predictions = testSet;
        } else {
            resRatings = CFU2U.predictRatings(Data.getInstance().getUserList(), Data.getInstance().getMovieList(),
                    Data.getInstance().getRatingList(), Data.getInstance().getPredictionList());
            predictions = Data.getInstance().getPredictionList();
        }

        // Converting back
        for (int i = 0; i < predictions.size(); i++) {
            predictions.get(i).setRating(resRatings.get(i));
        }

        if (!Config.TRAINING_MODE)
            predictions.writeResultsFile(Config.outputFile);

        // Verify the predictions.
        String rmse = RMSE.calcString(predictions, verificationSet);
        System.out.println(rmse);

        long endTime = System.currentTimeMillis();
        System.out.println("Duration: " + (endTime - startTime) / 1000 + "s" );

        for (int i = 0; i < 20; i++) {
            System.out.println("id: " + predictions.get(i).getMovie().getIndex() + "\t actual: " + verificationSet.get(i).getRating() + " \t predicted: " + predictions.get(i).getRating());
        }

        if (Config.ALLOW_LOG) {
            try {
                PrintWriter pw = new PrintWriter(new FileOutputStream(new File(Config.LOG_FILE), true));
                pw.println(new Date());
                pw.println(">Running CF user-user...");
                pw.println(Config.getInstance().toString());
                pw.println(rmse);
                pw.println("Duration: " + (endTime - startTime) / 1000 + "s");
                pw.println("----------------------------------------------");
                pw.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
