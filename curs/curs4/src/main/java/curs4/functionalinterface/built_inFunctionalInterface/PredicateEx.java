package curs4.functionalinterface.built_inFunctionalInterface;//package built_inFunctionalInterface;
//
//
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Comparator;
//import java.util.List;
//import java.util.function.Predicate;
//
//
//
//public class PredicateEx {
//
//    public static void main(String[] args) {
//
//        List<Student> list= new ArrayList(Arrays.asList(
//                new Student(22,"Aprogramatoarei",5.6f),
//                new Student(23,"Popescu",9.6f),
//                new Student(24,"Birlanescu",4.6f)));
//
//
//        Predicate<Student> estePromovat=x->x.getMedia()>=5;  //lambda function
//
//        Predicate<Student> estePromovat2=StudentHelper::promovat; //method reference
//        System.out.println(estePromovat.test(new Student(24,"Birlanescu",4.6f)));
//
//        Predicate<Student> promovatSiIncepeCuA=estePromovat.and(x->x.getNume().startsWith("A"));
//        System.out.println(estePromovat.test(new Student(24,"Birlanescu",4.6f))); //false
//
//
//        list.forEach(x-> {if (estePromovat.test(x)) System.out.println(x);} );
//
//        list.removeIf(estePromovat.negate());
//
//        list.forEach(System.out::println);
//    }
//}
