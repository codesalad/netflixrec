package ti2736c.Drivers;

import java.util.ArrayList;
import java.util.Locale;

public class main {

	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
        Config.getInstance().read();

        ArrayList<Double> results = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Data.getInstance();
//            results.add(CFUUDriver.testRun());
            Data.destroy();
        }

        for (int i = 0; i < results.size(); i++){
            System.out.println("RMSE" + i + "\t" + results.get(i));
        }
    }


}
