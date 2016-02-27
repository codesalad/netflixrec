package ti2736c.Drivers;

import ti2736c.Core.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * Create training/test/validation sets based on Config.getInstance().
 * Created by codesalad on 26-2-16.
 */
public class Data {
    // lists
    private UserList userList;
    private MovieList movieList;
    private RatingList ratingList;
    // Map so that we can use a subset of users
    private Map<Integer, User> users;
    private Map<Integer, Movie> movies;
    private Map<Integer, List<Rating>> ratings;

    public Data() {
        userList = new UserList();
        movieList = new MovieList();
        ratingList = new RatingList();

        users = new HashMap<>();
        movies = new HashMap<>();
        ratings = new HashMap<>();

        loadUsers();
        loadMovies();
        loadRatings();
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

    public void loadUsers() {
		userList.readFile(Config.getInstance().usersFile);
        int maxLines = countLines(Config.getInstance().usersFile);
        while (users.size() != (maxLines * Config.getInstance().SET_SIZE)) {
            Random rnd = new Random();
            int index = rnd.nextInt(maxLines);
            if (!users.containsKey(index)) {
                users.put(index, userList.get(index));
            }
        }
    }

    public void loadMovies() {
        movieList.readFile(Config.getInstance().moviesFile);
        movieList.forEach(m -> {
            movies.put(m.getIndex(), m);
        });
    }

    public void loadRatings() {
        ratingList.readFile(Config.getInstance().ratingsFile, userList, movieList);
        ratingList.forEach(r -> {
            if (!ratings.containsKey(r.getUser().getIndex())) {
                ratings.put(r.getUser().getIndex(), new LinkedList<>());
            }
            ratings.get(r.getUser().getIndex()).add(r);
        });
    }

    public Map<Integer, User> getUsers() {
        return users;
    }

    public Map<Integer, Movie> getMovies() {
        return movies;
    }

    public Map<Integer, List<Rating>> getRatings() {
        return ratings;
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

}
