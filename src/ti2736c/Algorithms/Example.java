package ti2736c.Algorithms;

import ti2736c.Core.MovieList;
import ti2736c.Core.RatingList;
import ti2736c.Core.UserList;
import ti2736c.Drivers.Config;
import ti2736c.Drivers.Data;

/**
 * Created by codesalad on 27-2-16.
 */
public class Example {
    public static RatingList predictRatings() {
        UserList userList = Data.getInstance().getUserList();
        MovieList movieList = Data.getInstance().getMovieList();
        RatingList ratingList = Data.getInstance().getRatingList();

        RatingList predRatings = new RatingList();
        predRatings.readFile(Config.getInstance().predictionsFile, userList, movieList);

        // Compute mean of ratings
        double mean = ratingList.get(0).getRating();
        for (int i = 1; i < ratingList.size(); i++) {
            mean = ((double) i / ((double) i + 1.0)) * mean
                    + (1.0 / ((double) i + 1.0))
                    * ratingList.get(i).getRating();
        }

        // Predict mean everywhere
        for (int i = 0; i < predRatings.size(); i++) {
            predRatings.get(i).setRating(mean);
        }

        // Return predictions
        return predRatings;
    }
}
