package curs4.functionalinterface.functionalinterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@FunctionalInterface
public interface AritmeticExpression {
    double  pi=3.14;
    double e=2.71;
    double  calculate(double a, double b);  //abstract

    default double sqrt(double a) {
        return Math.sqrt(a);
    }

    default double power(double a, double  n) {
        return Math.pow(a, n);
    }

    default double numarLaPatrat(double n) {
        return power(n,2);
    }

    default double numarLaCub(double n) {
        return power(n,3);
    }

    default double patratBinom(double x, double y){ return Math.pow(x+y,2); }

    static double cubBinom(double x, double y){ return Math.pow(x+y,3); }

    static double suma(double x, double y) {return x+y;}
}

class TestFunctionalInterface{
    public static void main(String[] args) {

        AritmeticExpression expression=new AritmeticExpression() {
            @Override
            public double calculate(double a, double b) {
                return sqrt(AritmeticExpression.suma(patratBinom(a,b),AritmeticExpression.cubBinom(a,b)));
            }
        };
        System.out.println(expression.calculate(1,1));

        double f=AritmeticExpression.cubBinom(2,3);
        String s="valoarea expresiei ";
        System.out.format("%s (2+3)^3 este %.2f\n",s,f);

        AritmeticExpression instanceMethodReference=FormulaHelper::patratBinom;
        AritmeticExpression lambda= (x,y)-> Math.pow(x+y,2);



        System.out.format("%s (2+3)^2 este %.2f\n",s,instanceMethodReference.calculate(2,3));

        List<Integer> l=new ArrayList<>(Arrays.asList(1,2,3));
        l.sort((x,y)-> x-y);


    }
}