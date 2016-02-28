package ti2736c.Drivers;

import ti2736c.Algorithms.NearestNeighbour;
import ti2736c.Core.FeatureItem;
import ti2736c.Core.FeatureVector;
import ti2736c.Core.Rating;
import ti2736c.Core.RatingList;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by codesalad on 27-2-16.
 */
public class NNDriver {
    public static void main(String[] args) {
        /* Initialize configs */
        Config.getInstance().read();

//        Example exampleAlgorithm = new Example();
//
//        UserList users = Data.getInstance().getUserList();
//        MovieList movies = Data.getInstance().getMovieList();
//
//        // Use known data to train.
//        RatingList trainingSet = Data.getInstance().getTrainingSet();
//
//        // Test set: this serves as a verifiable PredictionList
//        // The predictions algorithm will throw out a new predictions list that differs from this one.
//        RatingList verificationSet = Data.getInstance().getVerificationSet();
//
//        // Contains <user id, movie id>, ratings set at 0.
//        RatingList testSet = Data.getInstance().getTestSet();
//
//        // Predict ratings.
//        RatingList predictions = exampleAlgorithm.predictRatings(users, movies, trainingSet, testSet);
//
//        if (Config.ALLOW_WRITE)
//            predictions.writeResultsFile(Config.outputFile);
//
//        // Verify the predictions.
//        RMSE.calcPrint(predictions, verificationSet);

        RatingList trainingSet = Data.getInstance().getTrainingSet();
        LinkedList<FeatureVector> set = new LinkedList<>();
        trainingSet.forEach(r -> {
            FeatureVector vector = new FeatureVector(r.getUser().getIndex(), r.getMovie().getIndex(), r.getRating());
            vector.add((double) r.getUser().getAge()); //  age
            vector.add((double) r.getUser().getProfession()); // profession
            vector.add((r.getUser().isMale()) ? 1.0 : 0.0); // gender
            vector.add((double) r.getMovie().getIndex()); //movieindex
            set.add(vector);
        });

        // Item to classify
        Rating r = Data.getInstance().getRatingList().get(13);
        FeatureVector vector = new FeatureVector(r.getUser().getIndex(), r.getMovie().getIndex(), r.getRating());
        // add info about user.
        vector.add((double) r.getUser().getAge()); //age
        vector.add((double) r.getUser().getProfession()); // profession
        vector.add((r.getUser().isMale()) ? 1.0 : 0.0); // gender
        vector.add((double) r.getMovie().getIndex()); //movieindex

        List<FeatureItem> res = NearestNeighbour.kNearestNeighbors(100, vector, set);

        res.forEach(item -> {
        System.out.println("index: " + item.getVector().getUserIndex() +
                "\t age: " + item.getVector().get(0) +
                "\t profession: " + item.getVector().get(1) +
                "\t ismale: " + item.getVector().get(2) +
                "\t dist: " + item.getDistance() +
                "\t movie: " + item.getVector().get(3) +
                "\t rating: " + item.getVector().getRating() + "\n");
        });

        double rating = NearestNeighbour.ratingFromNeighbours(r.getMovie().getIndex(), res);
        System.out.println("movie: " + r.getMovie().getIndex() + "\tprediction: " + rating + "\tactual:" + r.getRating());
    }
}
