package ti2736c.Core;

public class User {

    int index, age, profession;
    double bias, mean;
    boolean male;
    
	public User(int _index, boolean _male, int _age, int _profession) {
        this.index      = _index;
        this.male       = _male;
        this.age        = _age;
        this.profession = _profession;
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
    
    public boolean isMale() {
        return male;
    }
    
    public int getAge() {
        return age;
    }
    
    public int getProfession() {
        return profession;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("[USER\t index:")
            .append(getIndex()).append(", male: ")
            .append(isMale()).append(", age: ")
            .append(getAge()).append(", prof: ")
            .append(getProfession())
            .append("]");

        return result.toString();
    }
}
