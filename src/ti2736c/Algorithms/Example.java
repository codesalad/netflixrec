package ti2736c.Algorithms;

import ti2736c.Core.MovieList;
import ti2736c.Core.RatingList;
import ti2736c.Core.UserList;

/**
 * Created by codesalad on 27-2-16.
 */
public class Example {
    public static RatingList predictRatings(UserList userList, MovieList movieList, RatingList inputList, RatingList outputList) {

//        RatingList predRatings = new RatingList();
//        outputList.readFile(Config.getInstance().predictionsFile, userList, movieList);

        // Compute mean of ratings
        double mean = inputList.get(0).getRating();
        for (int i = 1; i < inputList.size(); i++) {
            mean = ((double) i / ((double) i + 1.0)) * mean
                    + (1.0 / ((double) i + 1.0))
                    * inputList.get(i).getRating();
        }

        // Predict mean everywhere
        for (int i = 0; i < outputList.size(); i++) {
            outputList.get(i).setRating(mean);
        }

        // Return predictions
        return outputList;
    }
}
