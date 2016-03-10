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

    public static boolean TRAINING_MODE;
    public static boolean ALLOW_STATUS_OUTPUT;

    public static boolean RANDOMIZE_SETS;

    public static double TRAINING_SET_SIZE;
    public static double TEST_SET_SIZE;

    /* LFM */
    public static int LF_EPOCHS;
    public static int LF_FEATURE_LENGTH;
    public static double LF_LEARNING_RATE;
    public static double LF_REGULARIZATION;

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
                if (!line.startsWith("#") && !line.startsWith("[")) {
                    String[] parts = line.replace(" ", "").split("=");
                    switch (parts[0]) {

                        /* General */
                        case "training_mode": TRAINING_MODE = parts[1].equals("true"); break;
                        case "allow_status_output": ALLOW_STATUS_OUTPUT = parts[1].equals("true"); break;

                        /* Training */
                        case "randomize_sets": RANDOMIZE_SETS = parts[1].equals("true"); break;
                        case "training_set_size": TRAINING_SET_SIZE = Double.parseDouble(parts[1]); break;
                        case "test_set_size": TEST_SET_SIZE = Double.parseDouble(parts[1]); break;

                        /* LFM */
                        case "lf_epochs" : LF_EPOCHS = Integer.parseInt(parts[1]); break;
                        case "lf_feature_length" : LF_FEATURE_LENGTH = Integer.parseInt(parts[1]); break;
                        case "lf_learning_rate" : LF_LEARNING_RATE = Double.parseDouble(parts[1]); break;
                        case "lf_regularization" : LF_REGULARIZATION = Double.parseDouble(parts[1]); break;

                        /* Files */
                        case "movies_file": moviesFile = parts[1]; break;
                        case "ratings_file": ratingsFile = parts[1]; break;
                        case "users_file": usersFile = parts[1]; break;
                        case "predictions_file": predictionsFile = parts[1]; break;
                        case "output_file": outputFile = parts[1]; break;
                        default: break;
                    }

                    /* Post: some things are deductive */
                    RANDOMIZE_SETS = RANDOMIZE_SETS && TRAINING_MODE;

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
