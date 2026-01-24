package curs4.functionalinterface.built_inFunctionalInterface;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class Ex1 {
    public static void main(String[] args) {

//        List<Student> list= new ArrayList(Arrays.asList(
//                new Student(22,"Aprogramatoarei",5.6f),
//                new Student(23,"Popescu",9.6f),
//                new Student(24,"Popica",4.6f)));

        List<String> list1= new ArrayList(Arrays.asList("ana", "are","o","atitudine","impozanta"));


        String sir="ana";
        Predicate<String> p1=sir::startsWith;
        System.out.println(p1.test("a"));
       // list1.removeIf(p1);



       list1.removeIf("ana"::startsWith);
//       list1.removeIf(x->x.startsWith("ana"));
//        list1.removeIf(x->x.contains("ana"));

        list1.forEach(System.out::println);






    }
}
