package curs4.functionalinterface.built_inFunctionalInterface;
import java.util.Comparator;
import java.util.Optional;


public class Student implements Comparable<Student>{

    private int id;
    private String nume;
    private float media;
    public Student(int id, String nume, float media) {
        this.id = id;
        this.nume = nume;
        this.media = media;
    }
    
	@Override
	public int compareTo(Student s) {
		return this.getNume().compareTo(s.getNume());
	}

    @Override
    public String toString() {
        return id+";" + nume + ";" + media;
    }
    public int getId() {
        return id;
    }
    public float getMedia() {
        return media;
    }
    public String getNume() {
        return nume;
    }


//    public static Optional<Double> average(int... scores) {
//        if (scores.length == 0) return Optional.empty();
//        int sum = 0;
//        for (int score: scores) sum += score;
//        return Optional.of((double) sum / scores.length);

//    }
}

