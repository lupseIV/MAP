package curs4.functionalinterface.built_inFunctionalInterface;

import java.util.function.UnaryOperator;

public class UnaryOperatorEx {
    public static void main(String[] args) {
        UnaryOperator<String> u1 = String::toUpperCase;
        UnaryOperator<String> u2 = x -> x.toUpperCase();

        System.out.println(u1.apply("ana"));
        System.out.println(u2.apply("ana"));
    }
}
