package ti2736c.Drivers;

import ti2736c.Algorithms.CFI2I;
import ti2736c.Algorithms.LFM;
import ti2736c.Algorithms.RMSE;
import ti2736c.Core.Rating;
import ti2736c.Core.RatingList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by codesalad on 6-3-16.
 */
public class Combiner {
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
        RatingList testSet = Data.getInstance().getPredictionList();

        long startTime = System.currentTimeMillis();

        System.out.print(">Starting Latent Factor Model algorithm... ");
        ArrayList<Double> lfmresults = LFM.predictRatings(Data.getInstance().getUserList(), Data.getInstance().getMovieList(), trainingSet, testSet);
        System.out.print(" - done.\n");

        System.out.print(">Starting CF item-item algorithm... ");
        ArrayList<Double> cfII = CFI2I.predictRatings(Data.getInstance().getUserList(), Data.getInstance().getMovieList(), trainingSet, testSet);
        System.out.print(" - done.\n");

        for (int i = 0; i < testSet.size(); i++) {
            Rating toRate = testSet.get(i);
            double a = lfmresults.get(i);
            double b = cfII.get(i);
            double prediction = 0.66 * a + (1.0 - .66) * b;

            if (prediction > 5.0)
                prediction = 5.0;
            else if (prediction < 1.0)
                prediction = 1.0;

            toRate.setRating(prediction);

//            if (i < 50)
//                System.out.println("LFM: " + a + "\tII:" + b + "\tprediction: " + prediction + "\tactual: " + verificationSet.get(i).getRating());
        }

        // CACHE
        if (Config.ALLOW_CACHE) {
            try {
                PrintWriter pw = new PrintWriter(new FileOutputStream(new File(Config.RESULT_CACHE_LOC), false));
                pw.println(new Date());
                pw.println("LFM & LFM2 & II");
                for (int i = 0; i < testSet.size(); i++) {
                    double a = lfmresults.get(i);
//                    double b = lfmresults2.get(i);
                    double c = cfII.get(i);
//                    pw.println(a + "|" + b + "|" + c);
                }
                pw.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

        if (!Config.TRAINING_MODE)
            testSet.writeResultsFile(Config.outputFile);


        String rmse = RMSE.calcString(testSet, verificationSet);
        System.out.println(rmse);

        long endTime = System.currentTimeMillis();
        System.out.println("Duration: " + (endTime - startTime) / 1000 + "s");

        if (Config.ALLOW_LOG) {
            try {
                PrintWriter pw = new PrintWriter(new FileOutputStream(new File(Config.LOG_FILE), true));
                pw.println(new Date());
                pw.println(">Running combiner LFM & CF Item-Item...");
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
