package ti2736c.Algorithms;

import ti2736c.Core.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by codesalad on 28-2-16.
 */
public class NearestNeighbour {

    public static RatingList predictRatings(UserList userList, MovieList movieList, RatingList inputList, RatingList outputList) {

        // Return predictions
        return outputList;
    }

    public static List<FeatureItem> kNearestNeighbors(int k, FeatureVector toClassify, List<FeatureVector> features) {
        ArrayList<FeatureItem> items = new ArrayList<>();

        for (FeatureVector fv : features) {
            items.add(new FeatureItem(fv, toClassify.euclidDist(fv)));
        }

        Collections.sort(items);

        return (List<FeatureItem>) items.subList(0, k);
    }

//    public static void cluster(List<Double> toClassify, RatingList inputList) {
//
//        FeatureVector query = new FeatureVector(toClassify.get(toClassify.size() - 1));
//        toClassify.forEach(d -> {
//            query.add(d);
//        });
//
//        ArrayList<FeatureVector> features = new ArrayList<>();
//
//        // create features
//        // feature: userindex, m/f, age, profession, movie, rating
//        for (Rating r : inputList) {
//            FeatureVector vector = new FeatureVector(r.getRating());
//            vector.add( (double) r.getUser().getIndex()); //user index
////            vector.add((r.getUser().isMale()) ? 1.0:0.0); // m/f
////            vector.add((double) r.getUser().getAge()); // age
////            vector.add((double) r.getUser().getProfession());
//            vector.add((double) r.getMovie().getIndex());
//            vector.add(r.getRating());
//            features.add(vector);
//        }
//
//        ArrayList<Item> list = new ArrayList<>();
//        for (FeatureVector fv : features) {
//            double d = query.angDist(fv);
//            list.add(new Item(fv, d));
//        }
//
//        Collections.sort(list);
//
//        System.out.println("user: " + toClassify.get(0)+
//                "\t movie: " + toClassify.get(1) +
//                "\trating: " + toClassify.get(2) + "\n");
//
//        for (int i = 0; i < 10; i++) {
//            Item item = list.get(i);
//            System.out.println("user: " + item.getVector().get(0)+
//                                "\t movie: " + item.getVector().get(1) +
//                                "\trating: " + item.getVector().get(2) +
//                                "\tdist: " + item.getDistance() + "\n");
//        }
//    }
}