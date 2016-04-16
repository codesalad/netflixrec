package ti2736c.Drivers;

import ti2736c.Algorithms.RMSE;
import ti2736c.Core.Rating;
import ti2736c.Core.RatingList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by codesalad on 6-3-16.
 */
public class CombinerCached {
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

        ArrayList<Double> A = new ArrayList<>(); // LFM
        ArrayList<Double> B = new ArrayList<>(); //LFM2
        ArrayList<Double> C = new ArrayList<>(); // II

        try {
            BufferedReader br = new BufferedReader(new FileReader(Config.RESULT_CACHE_LOC));
            String line;
            while((line = br.readLine()) != null) {
                if (line.contains("|")) {
                    String[] parts = line.split("\\|");
                    A.add(Double.parseDouble(parts[0]));
                    B.add(Double.parseDouble(parts[1]));
                    C.add(Double.parseDouble(parts[2]));
//                    System.out.println(parts[0] + "\t" + parts[1] + "\t" + parts[2]);
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < testSet.size(); i++) {
            Rating toRate = testSet.get(i);
            double a = A.get(i);
            double b = B.get(i);
            double c = C.get(i);

            double aval = .66;
            double bval = 0.0;

            double prediction = aval * a + bval * b + (1.0 - (aval + bval)) * c;

            if (prediction > 5.0)
                prediction = 5.0;
            else if (prediction < 1.0)
                prediction = 1.0;

            toRate.setRating(prediction);

            if (i < 50)
                System.out.println("LFM: " + a + "\tLFM2: " + b + "\tII:" + c + "\tprediction: " + prediction + "\tactual: " + verificationSet.get(i).getRating());
        }


        String rmse = RMSE.calcString(testSet, verificationSet);
        System.out.println(rmse);

        long endTime = System.currentTimeMillis();
        System.out.println("Duration: " + (endTime - startTime) / 1000 + "s");

    }
}
