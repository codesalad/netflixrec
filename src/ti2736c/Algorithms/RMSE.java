package ti2736c.Algorithms;

import ti2736c.Core.RatingList;
import ti2736c.Drivers.Config;
import ti2736c.Drivers.Data;

/**
 * Calculates the RMSE
 * Created by codesalad on 26-2-16.
 */
public class RMSE {
    public static double calculate(RatingList predicted, RatingList actual) {
        assert (predicted.size() == actual.size());

        double sse = 0.0;

        for (int i = 0; i < actual.size(); i++) {
            sse += Math.pow(predicted.get(i).getRating()
                    - actual.get(i).getRating(), 2);
        }

        sse /= actual.size();
        return Math.sqrt(sse);
    }

    public static void calcPrint(RatingList predicted, RatingList actual) {
        StringBuilder builder = new StringBuilder();
        builder.append("Algorithm ran with training set of size: ").append(Data.getInstance().getTrainingSet().size())
                .append(" (").append(Config.TRAINING_SET_SIZE * 100).append("%)")
                .append("\ntest set of size: ").append(actual.size());
        builder.append("\nRMSE: ").append(calculate(predicted, actual));
        System.out.println(builder);
    }
}
