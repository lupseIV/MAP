package curs4.functionalinterface.grile;

class A {
    public int x = 0;
}
public class G10 {
    public int foo() {
        A a = new A();
        int b=1;
        try { a.x = 1;
            b=2;
            throw new NullPointerException();
        } catch (Exception e) {
            a.x = 2;
            return b;
        } finally {
            a.x = 3;
            b=4;
            return b;
        }
    }
    public static void main(String[] args) {
        G10 ex = new G10();
        System.out.println(ex.foo());
    }
}