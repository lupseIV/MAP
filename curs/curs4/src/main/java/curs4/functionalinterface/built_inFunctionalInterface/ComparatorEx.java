package curs4.functionalinterface.built_inFunctionalInterface;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ComparatorEx {
    public static void main(String[] args) {
        List<Student> list= new ArrayList(Arrays.asList(
                new Student(22,"Aprogramatoarei",5.6f),
                new Student(23,"Popescu",9.6f),
                new Student(24,"Birlanescu",4.6f)));

        Comparator<Student> byName=Student::compareTo;
        Comparator<Student> byName2=StudentHelper::compareByName;
        Comparator<Student> byMedia=StudentHelper::compareByAverage;

        list.sort(byName);
        list.forEach(System.out::println);
        System.out.println("--------------------");

        list.sort(byName2);
        list.forEach(System.out::println);
        System.out.println("--------------------");

        list.sort(byMedia);
        list.forEach(System.out::println);
        System.out.println("--------------------");

        list.sort((x,y)->x.getId()-y.getId());
        list.forEach(System.out::println);
    }


}
