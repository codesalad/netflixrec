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

    public static double TRAINING_SET_SIZE; // Fixed size for datasets. Used for training.
    public static double TEST_SET_SIZE;
    public static boolean ALLOW_WRITE;
    public static boolean ALLOW_STATUS_OUTPUT;

    public static boolean RANDOMIZE_SETS;
    public static boolean NORMALIZE;
    public static boolean BIAS;

    /* Data locations */
    public static String moviesFile; // movies
    public static String ratingsFile; // ratings
    public static String usersFile; // users
    public static String predictionsFile; // predictions
    public static String outputFile; // output

    /* Collaborative Filtering */
    public static double CF_threshold;

    /* NEAREST NEIGHBOUR ALGORITHM */
    public static int NN_k;
    public static String NN_distance_metric; // euclid, angular, cosine, acos, hamming...

    private Config() {}

    public static void read() {
        StringBuilder log = new StringBuilder();
        log.append("CONFIG LOADED:\n-----------------\n");
        BufferedReader reader = null;
        String line;
        try {
            reader = new BufferedReader(new FileReader("settings.config"));
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("#") && !line.startsWith("[")) {
                    String[] parts = line.replace(" ", "").split("=");
                    switch (parts[0]) {
                        case "movies_file": moviesFile = parts[1]; break;
                        case "ratings_file": ratingsFile = parts[1]; break;
                        case "users_file": usersFile = parts[1]; break;
                        case "randomize_sets": RANDOMIZE_SETS = parts[1].equals("true"); break;
                        case "normalize": NORMALIZE = parts[1].equals("true"); break;
                        case "bias": BIAS = parts[1].equals("true"); break;
                        case "training_set_size": TRAINING_SET_SIZE = Double.parseDouble(parts[1]); break;
                        case "test_set_size": TEST_SET_SIZE = Double.parseDouble(parts[1]); break;
                        case "predictions_file": predictionsFile = parts[1]; break;
                        case "output_file": outputFile = parts[1]; break;
                        case "allow_write": ALLOW_WRITE = parts[1].equals("true"); break;
                        case "allow_status_output": ALLOW_STATUS_OUTPUT = parts[1].equals("true"); break;
                        case "NN_k": NN_k = Integer.parseInt(parts[1]); break;
                        case "NN_distance_metric": NN_distance_metric = parts[1]; break;
                        case "CF_threshold": CF_threshold = Double.parseDouble(parts[1]); break;
                        default: break;
                    }

                    if (parts.length == 2)
                        log.append(parts[0]).append(" = ").append(parts[1]).append("\n");
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
