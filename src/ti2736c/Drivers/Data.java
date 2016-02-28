package ti2736c.Drivers;

import ti2736c.Core.MovieList;
import ti2736c.Core.Rating;
import ti2736c.Core.RatingList;
import ti2736c.Core.UserList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

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

        initSets();
    }

    public int countLines(String location) {
        int lines = 0;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(location));
            while (reader.readLine() != null)
                lines++;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return lines;
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
