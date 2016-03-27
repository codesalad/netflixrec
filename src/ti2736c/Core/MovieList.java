package ti2736c.Core;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;


public class MovieList extends ArrayList<Movie> {
    
    // Reads in a file with movies data
    public void readFile(String filename) {
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(filename));
            while((line = br.readLine()) != null) {
                String[] movieData = line.split(";");
                add(Integer.parseInt(movieData[0]) - 1,
                    new Movie(Integer.parseInt(movieData[0]),
                              Integer.parseInt(movieData[1]),
                              movieData[2]));
//                put(Integer.parseInt(movieData[0]),
//                        new Movie(Integer.parseInt(movieData[0]),
//                              Integer.parseInt(movieData[1]),
//                              movieData[2]));
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            if(br != null) {
                try {
                    br.close();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadGenres(String filename) {
        System.out.println("Loading movie genres...");
        assert (size() > 0);
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(filename));
            while((line = br.readLine()) != null) {
                String[] movieData = line.split("::");
                // 0: index, 1: title, 2: genres
                int index = Integer.parseInt(movieData[0]) - 1;
                if (index < size() && get(index) != null) {
                    String[] genreData = movieData[2].split("\\|");
                    for (String genre : genreData)
                        get(index).addGenre(genre);
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            if(br != null) {
                try {
                    br.close();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

