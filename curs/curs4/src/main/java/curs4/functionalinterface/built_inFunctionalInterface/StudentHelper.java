package curs4.functionalinterface.built_inFunctionalInterface;



public class StudentHelper{

    public static int compareByAverage(Student a, Student b)
    {
        return (int) (a.getMedia()-b.getMedia());
    }

    public static int compareByName(Student a, Student b)
    {
        return (int) a.getNume().compareTo(b.getNume());
    }

    public static int compareById(Student a, Student b)
    {
        return (int) a.getNume().compareTo(b.getNume());
    }

    public static boolean promovat(Student s){
        return s.getMedia()>=5;
    }

    public static void printStudent(Student s)
    {
        System.out.println(s);
    }
}

