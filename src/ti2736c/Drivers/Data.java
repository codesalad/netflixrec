package ti2736c.Drivers;

import ti2736c.Core.MovieList;
import ti2736c.Core.Rating;
import ti2736c.Core.RatingList;
import ti2736c.Core.UserList;

import java.util.*;

/**
 * Create training/test/validation sets based on Config.getInstance().
 * Created by codesalad on 26-2-16.
 */
public class Data {
    private static Data instance = null;

    // lists
    private UserList userList;
    private MovieList movieList;
    private RatingList trainingSet;
    private RatingList ratingList;
    private RatingList testSet;
    private RatingList verificationSet;
    private RatingList predictionList;

    private Map<Integer, List<Double>> user_rating;

    private Data() {
        init();
    }

    public void init() {
        userList = new UserList();
        userList.readFile(Config.getInstance().usersFile);

        movieList = new MovieList();
        movieList.readFile(Config.getInstance().moviesFile);

        ratingList = new RatingList();
        ratingList.readFile(Config.getInstance().ratingsFile,
                userList, movieList);

        trainingSet = new RatingList();
        testSet = new RatingList();
        verificationSet = new RatingList();

        predictionList = new RatingList();
        predictionList.readFile(Config.getInstance().predictionsFile,
                userList, movieList);

        // Only user rating is normalized at the moment.
        if (Config.NORMALIZE)
            normalizeData();

        initSets();
    }

    public void normalizeData() {
        System.out.println("Normalizing data. Takes a while.");

        // Create map for simplicity
        user_rating = new HashMap<>();
        ratingList.forEach(r -> {
            if (!user_rating.containsKey(r.getUser().getIndex()))
                user_rating.put(r.getUser().getIndex(), new LinkedList<>());
            user_rating.get(r.getUser().getIndex()).add(r.getRating());
        });

        // Calculate mean and std of each user
        // mean gets added as last element of (k, l)
        user_rating.forEach((k, l) -> {
            double mean = l.stream().mapToDouble(a -> a).average().getAsDouble();
            l.add(mean);
        });

        user_rating.forEach((k, l) -> {
            double mean = l.get(l.size() - 1);

            final double[] sumdev = {0.0};
            l.stream().map(r -> sumdev[0] += Math.pow(r - mean, 2));

            l.add(Math.sqrt(sumdev[0]) / (l.size() - 1));
        });
//
//        // normalize
//        ratingList.forEach(r -> {
//            LinkedList<Double> list = (LinkedList) user_rating.get(r.getUser().getIndex());
//            r.setRating( (r.getRating() - list.get(list.size() - 2))
//                        / (list.get(list.size() - 1)) );
//        });
    }

    /**
     * Creates training sets, test sets and verification sets.
     * If RANDOMIZE_SETS is set to true (see Config), random elements
     * are picked from ratingList until the set is full.
     * Else, elements are added in the order they're in in ratingList.
     * If trainingSize + testSize > ratingList size, elements could occur in both
     * the training and test sets. This is bad.
     */
    public void initSets() {
        int trainingSize = (int) (ratingList.size() * Config.TRAINING_SET_SIZE);
        int testSize = (int) (ratingList.size() * Config.TEST_SET_SIZE);

        assert (trainingSize <= ratingList.size());

        Random rnd = new Random();

        if (Config.RANDOMIZE_SETS) {
            System.out.println("Randomizing training/test sets...");
            RatingList temp = new RatingList();
            temp.addAll(ratingList);

            while (trainingSet.size() != trainingSize)
                trainingSet.add(temp.remove(rnd.nextInt(temp.size())));

            while (verificationSet.size() != testSize) {
                if (!temp.isEmpty()) verificationSet.add(temp.remove(rnd.nextInt(temp.size())));
                else verificationSet.add(ratingList.get(rnd.nextInt(ratingList.size())));
            }
        } else {
            System.out.println("Creating non-random training/test sets...");
            trainingSet.addAll(ratingList.subList(0, trainingSize));
            if (trainingSize + testSize > ratingList.size()) {
                verificationSet.addAll(ratingList.subList(trainingSize + 1, ratingList.size() - 1));
                while (verificationSet.size() != testSize)
                    verificationSet.add(ratingList.get(rnd.nextInt(ratingList.size())));
            } else {
                verificationSet.addAll(ratingList.subList(trainingSize + 1, trainingSize + testSize - 1));
            }
        }
        // add all ratings to test set, but set ratings at 0.
        verificationSet.forEach(r -> testSet.add(new Rating(r.getUser(), r.getMovie(), 0.0)));
    }

    /**
     * Outputs a subset of RatingList.
     * Meant for training the algorithms.
     * Ratings are already defined.
     * @return trainingSet
     */
    public RatingList getTrainingSet() {
        return trainingSet;
    }

    /**
     * Outputs a subset of RatingList.
     * Meant for testing the algorithms.
     * Ratings are already defined, used to check predictions.
     * @return verificationSet
     */
    public RatingList getVerificationSet() {
        return verificationSet;
    }

    /**
     * Test Set contains everything in the verificationSet.
     * Ratings are initialized at 0.0.
     * @return testSet
     */
    public RatingList getTestSet() {
        return testSet;
    }

    public UserList getUserList() {
        return userList;
    }

    public MovieList getMovieList() {
        return movieList;
    }

    public RatingList getRatingList() {
        return ratingList;
    }

    public RatingList getPredictionList() {
        return predictionList;
    }

    public static synchronized Data getInstance() {
        if (instance == null)
            instance = new Data();
        return instance;
    }
}
