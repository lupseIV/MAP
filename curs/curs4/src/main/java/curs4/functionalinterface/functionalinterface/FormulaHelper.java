package curs4.functionalinterface.functionalinterface;


public  class FormulaHelper{
    private double a;
    private double b;

    public FormulaHelper(double a, double b) { this.a = a;this.b = b; }

    public static double patratBinom(double x, double y){
        return Math.pow(x+y,2);
    }

    public double distanta(double x, double y){
        return Math.sqrt(Math.pow(x-a,2) +Math.pow(y-b,2));
    }

}
