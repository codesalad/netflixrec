package ti2736c.Drivers;

import ti2736c.Algorithms.Example;
import ti2736c.Core.RatingList;

import java.util.Locale;

public class main {

	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
        Config.getInstance().read();

        RatingList exampleList = Example.predictRatings();
        exampleList.writeResultsFile(Config.getInstance().outputFile);
//        Data.getInstance().loadTrainingSet();
    }


}
