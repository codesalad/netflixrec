package ti2736c.Core;



public class Movie {
    
    int index, year;
    double bias, mean;
    String title;
    
	public Movie(int _index, int _year, String _title) {
        this.index = _index;
        this.year  = _year;
        this.title = _title;
        this.bias = 0;
        this.mean = 0;
    }

    public void setBias(double b) {
        this.bias = b;
    }

    public double getBias() {
        return bias;
    }

    public void setMean(double m) {
        this.mean = m;
    }

    public double getMean() {
        return mean;
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
                .append(getTitle()).append("]");
        return result.toString();
    }
}

