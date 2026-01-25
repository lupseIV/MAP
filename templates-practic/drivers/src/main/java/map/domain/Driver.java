package map.domain;

public class Driver extends Entity<Integer>{
    String name;
    public Driver(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
