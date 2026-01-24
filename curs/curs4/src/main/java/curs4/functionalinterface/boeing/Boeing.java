package curs4.functionalinterface.boeing;




class Boeing {
    int height;

    public Boeing(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public int test(int a){ return 0;}

}


@FunctionalInterface
interface Flyable<T> {
    int canFly(T t); // the hight reached by T
}
class TestB{
    public static void main(String[] args) {
        Flyable<Boeing> f=Boeing::getHeight;
        int n=f.canFly(new Boeing(23));

        Flyable<Boeing> f2 = (Boeing b) -> b.getHeight();

    }
}