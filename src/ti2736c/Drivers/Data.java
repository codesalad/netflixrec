package ti2736c.Drivers;

import ti2736c.Core.MovieList;
import ti2736c.Core.RatingList;
import ti2736c.Core.UserList;

import java.io.BufferedReader;
import java.io.FileReader;

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

        testSet = new RatingList();

        trainingSet = new RatingList();

        predictionList = new RatingList();
        predictionList.readFile(Config.getInstance().predictionsFile,
                userList, movieList);

        if (Config.TRAINING_SET)
            loadTrainingSet();
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

    public void loadTrainingSet() {
//        trainingSet.readFile(Config.getInstance().ratingsFile, userList, movieList);
//        testSet.readFile(Config.getInstance().ratingsFile, userList, movieList);

        int trainingSize = (int) (ratingList.size() * Config.TRAINING_SET_SIZE);
        trainingSet.addAll(ratingList.subList(0, trainingSize));
        testSet.addAll(ratingList.subList(trainingSize + 1, ratingList.size() - 1));
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

    public RatingList getTrainingSet() {
        return trainingSet;
    }

    public RatingList getTestSet() {
        return testSet;
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
