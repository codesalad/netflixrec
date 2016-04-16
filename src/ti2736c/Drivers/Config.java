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
    public static boolean USE_GENRES;
    public static boolean ALLOW_STATUS_OUTPUT;
    public static boolean ALLOW_LOG;
    public static String LOG_FILE;
    public static boolean ALLOW_CACHE;
    public static String RESULT_CACHE_LOC;

    public static boolean RANDOMIZE_SETS;

    public static double TRAINING_SET_SIZE;
    public static double TEST_SET_SIZE;

    /* CF */
    public static String[] SIMILARITIES = {"euclid", "cosine", "pearson"};

    public static String CF_II_SIMILARITY;
    public static double CF_II_KNN;
    public static int CF_II_THRESHOLD;
    public static double CF_II_GENRE_SIM;

    public static String CF_UU_SIMILARITY;
    public static double CF_UU_KNN;
    public static int CF_UU_THRESHOLD;

    /* LFM */
    public static boolean LF_BIAS;
    public static int LF_EPOCHS;
    public static int LF_FEATURE_LENGTH;
    public static double LF_LEARNING_RATE;
    public static double LF_REGULARIZATION;

    /* Data locations */
    public static String moviesFile; // movies
    public static String moviesExtendedFile; // movies including genres
    public static String ratingsFile; // ratings
    public static String usersFile; // users
    public static String predictionsFile; // predictions
    public static String outputFile; // output

    public static StringBuilder log;

    private Config() {}

    public static void read() {
        log = new StringBuilder();
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
                        case "use_genres" : USE_GENRES = parts[1].equals("true"); break;
                        case "allow_status_output": ALLOW_STATUS_OUTPUT = parts[1].equals("true"); break;
                        case "allow_log": ALLOW_LOG = parts[1].equals("true"); break;
                        case "log_file": LOG_FILE = parts[1]; break;
                        case "allow_cache": ALLOW_CACHE = parts[1].equals("true");
                        case "results_cache": RESULT_CACHE_LOC = parts[1]; break;

                        /* Training */
                        case "randomize_sets": RANDOMIZE_SETS = parts[1].equals("true"); break;
                        case "training_set_size": TRAINING_SET_SIZE = Double.parseDouble(parts[1]); break;
                        case "test_set_size": TEST_SET_SIZE = Double.parseDouble(parts[1]); break;

                        /* CF */
                        case "cf_ii_similarity": CF_II_SIMILARITY = SIMILARITIES[Integer.parseInt(parts[1])]; break;
                        case "cf_ii_knn": CF_II_KNN = Double.parseDouble(parts[1]);break;
                        case "cf_ii_threshold": CF_II_THRESHOLD = Integer.parseInt(parts[1]); break;
                        case "cf_ii_genre_sim": CF_II_GENRE_SIM = Double.parseDouble(parts[1]); break;

                        case "cf_uu_similarity": CF_UU_SIMILARITY = SIMILARITIES[Integer.parseInt(parts[1])]; break;
                        case "cf_uu_knn": CF_UU_KNN = Double.parseDouble(parts[1]);break;
                        case "cf_uu_threshold": CF_UU_THRESHOLD = Integer.parseInt(parts[1]); break;

                        /* LFM */
                        case "lf_bias" : LF_BIAS = parts[1].equals("true"); break;
                        case "lf_epochs" : LF_EPOCHS = Integer.parseInt(parts[1]); break;
                        case "lf_feature_length" : LF_FEATURE_LENGTH = Integer.parseInt(parts[1]); break;
                        case "lf_learning_rate" : LF_LEARNING_RATE = Double.parseDouble(parts[1]); break;
                        case "lf_regularization" : LF_REGULARIZATION = Double.parseDouble(parts[1]); break;

                        /* Files */
                        case "movies_file": moviesFile = parts[1]; break;
                        case "movies_extended_file": moviesExtendedFile = parts[1]; break;
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

    public String toString() {
        return log.toString();
    }

    public static synchronized Config getInstance() {
        if (instance == null)
            instance = new Config();
        return instance;
    }
}
