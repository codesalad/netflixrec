package ti2736c.Drivers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Reads in config file
 * Created by codesalad on 26-2-16.
 */
public class Config {
    public static Config instance = null;

    /* General Vars */

    public static boolean TRAINING_SET;
    public static double TRAINING_SET_SIZE; // Fixed size for datasets. Used for training.
    public static boolean ALLOW_WRITE;

    /* Data locations */
    /* Data locations */
    public static String moviesFile; // movies
    public static String ratingsFile; // ratings
    public static String usersFile; // users
    public static String predictionsFile; // predictions
    public static String outputFile; // output

    private Config() {}

    public static void read() {
        StringBuilder log = new StringBuilder();
        log.append("CONFIG LOADED:\n-----------------\n");
        BufferedReader reader = null;
        String line;
        try {
            reader = new BufferedReader(new FileReader("settings.config"));
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("#")) {
                    String[] parts = line.replace(" ", "").split("=");
                    switch (parts[0]) {
                        case "movies_file": moviesFile = parts[1];
                            log.append(parts[0]).append(" = ").append(parts[1]).append("\n");
                            break;
                        case "ratings_file": ratingsFile = parts[1];
                            log.append(parts[0]).append(" = ").append(parts[1]).append("\n");
                            break;
                        case "users_file": usersFile = parts[1];
                            log.append(parts[0]).append(" = ").append(parts[1]).append("\n");
                            break;
                        case "training_set_size": TRAINING_SET_SIZE = Double.parseDouble(parts[1]);
                            log.append(parts[0]).append(" = ").append(parts[1]).append("\n");
                            break;
                        case "predictions_file": predictionsFile = parts[1];
                            log.append(parts[0]).append(" = ").append(parts[1]).append("\n");
                            break;
                        case "output_file": outputFile = parts[1];
                            log.append(parts[0]).append(" = ").append(parts[1]).append("\n");
                            break;
                        case "allow_write": ALLOW_WRITE = parts[1].equals("true");
                            log.append(parts[0]).append(" = ").append(parts[1]).append("\n");
                            break;
                        case "training_set": TRAINING_SET = parts[1].equals("true");
                            log.append(parts[0]).append(" = ").append(parts[1]).append("\n");
                            break;
                        default: break;
                    }
                }
            }
            System.out.println(log.toString());
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
            instance = new Config();
        return instance;
    }
}
