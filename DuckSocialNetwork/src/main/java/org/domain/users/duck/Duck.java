package org.domain.users.duck;

import org.domain.users.User;
import org.domain.users.duck.flock.Flock;
import org.utils.enums.DuckTypes;
import org.utils.enums.UserTypes;

public  abstract class Duck extends User {
    private DuckTypes duckType;
    private Double speed;
    private Double rezistance;
    private Flock<? extends Duck> flock;

    public Flock<? extends Duck> getFlock() {
        return flock;
    }

    public void setFlock(Flock<? extends Duck> flock) {
        this.flock = flock;
    }

    public DuckTypes getDuckType() {
        return duckType;
    }

    public void setDuckType(DuckTypes duckType) {
        this.duckType = duckType;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getRezistance() {
        return rezistance;
    }

    public void setRezistance(Double rezistance) {
        this.rezistance = rezistance;
    }




    public Duck( String username, String password, String email, DuckTypes duckType, Double speed, Double rezistance) {
        super( username, password, email,UserTypes.DUCK);
        this.duckType = duckType;
        this.speed = speed;
        this.rezistance = rezistance;
    }

    @Override
    public void login() {

    }

    @Override
    public void logout() {

    }

    @Override
    public void sendMessage() {

    }

    @Override
    public void receiveMessage() {

    }

    @Override
    public void update() {
        System.out.println("I am duck " + id + "and i was added to the event");
    }

    @Override
    public String toString() {

        return super.toString()+"Duck{" +
                ", rezistance=" + rezistance +
                ", speed=" + speed +
                ", duckType=" + duckType +
                '}';
    }
}
