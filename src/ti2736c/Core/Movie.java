package ti2736c.Core;


import java.util.HashSet;
import java.util.Set;

public class Movie {
    
    int index, year;
    String title;
    Set<String> genres;
    
	public Movie(int _index, int _year, String _title) {
        this.index = _index;
        this.year  = _year;
        this.title = _title;
        this.genres = new HashSet<>();
    }

    public double genreOverlap(Movie other) {
        HashSet<String> diff = new HashSet<>(getGenres());
        diff.retainAll(other.getGenres());

        return (double) diff.size() / (getGenres().size() + other.getGenres().size() - diff.size());
    }

    public Set<String> getGenres() {
        return genres;
    }

    public void addGenre(String genre) {
        genres.add(genre);
    }

    public int getIndex() {
        return index;
    }
    
    public int getYear() {
        return year;
    }
    
    public String getTitle() {
        return title;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("[MOVIE\t index:")
                .append(getIndex()).append(", year: ")
                .append(getYear()).append(", title: ")
                .append(getTitle()).append(", genres: ")
                .append(getGenres()).append("]");
        return result.toString();
    }
}

