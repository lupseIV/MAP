package curs4.functionalinterface.built_inFunctionalInterface;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ConsumerEx {

    public static void main(String[] args) {

        Consumer<Student> consumer=System.out::println; //method reference
        consumer.accept(new Student(123,"Dan",4.5f));


        Consumer<Student> consumer2=x-> System.out.println(x); //lambda
        consumer.accept(new Student(123,"Dan",4.5f));

        Consumer<Student> consumer3=Student::toString; //method reference
        consumer.accept(new Student(123,"Dan",4.5f));


        List<Student> list= new ArrayList(Arrays.asList(
                new Student(22,"Aprogramatoarei",5.6f),
                new Student(23,"Popescu",9.6f),
                new Student(24,"Birlanescu",8.6f)));

        list.forEach(x-> System.out.println(x));
        list.forEach(System.out::println);


    }
}
