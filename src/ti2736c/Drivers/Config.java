package ti2736c.Drivers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Reads in config file
 * Created by codesalad on 26-2-16.
 */
public class Config {
    private static Config instance = null;

    /* General Vars */

    public static double SET_SIZE = 0.1; // Fixed size for datasets. Used for training.

    /* Data locations */
    /* Data locations */
    public static String moviesFile; // movies
    public static String ratingsFile; // ratings
    public static String usersFile; // users
    public static String predictionsFile; // predictions
    public static String outputFile; // output

    private void Config() {}

    public static void read() {
        BufferedReader reader = null;
        String line;
        try {
            reader = new BufferedReader(new FileReader("settings.config"));
            while ((line = reader.readLine()) != null) {
                if (!line.contains("#")) {
                    String[] parts = line.replace(" ", "").split("=");
                    switch (parts[0]) {
                        case "movies_file": moviesFile = parts[1]; break;
                        case "ratings_file": ratingsFile = parts[1]; break;
                        case "users_file": usersFile = parts[1]; break;
                        case "set_size": SET_SIZE = Double.parseDouble(parts[1]); break;
                        case "predictions_file": predictionsFile = parts[1];
                        case "output_file": outputFile = parts[1]; break;
                        default: break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static synchronized Config getInstance() {
        if (instance == null)
            return new Config();
        else return instance;
    }
}
