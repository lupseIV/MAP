package curs4.functionalinterface.built_inFunctionalInterface;



import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class BinaryOperatorEx {
    public static void main(String[] args) {
        BinaryOperator<String> o1 = String::concat;
        BinaryOperator<String> o2 = (x,y)->x.concat(y);

        System.out.println(o1.apply("ana"," blandiana"));
        System.out.println(o2.apply("ana"," blandiana"));

//
        Student s=new Student(12,
                "nume",2.3f);

        //Student s=null;

        Optional<Student> op; //Optional.ofNullable(s);
        op=Optional.ofNullable(s);
        System.out.println(op.get());

    }
}
