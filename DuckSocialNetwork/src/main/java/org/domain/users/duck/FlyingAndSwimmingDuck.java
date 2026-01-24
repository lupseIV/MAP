package org.domain.users.duck;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.domain.dtos.DuckData;
import org.utils.enums.types.DuckTypes;

@JsonTypeName("org.domain.users.duck.FlyingAndSwimmingDuck")
public class FlyingAndSwimmingDuck extends Duck implements Swimming,Flying{
    @Override
    public void fly() {

    }

    @Override
    public void swim() {

    }

    public FlyingAndSwimmingDuck() {
        super();
    }

    public FlyingAndSwimmingDuck(String username, String password, String email, DuckTypes duckType, Double speed, Double rezistance) {
        super(username, password, email, duckType, speed, rezistance);
    }

    public FlyingAndSwimmingDuck(DuckData duckData) {
        super(duckData.getUsername(), duckData.getPassword(), duckData.getEmail(), DuckTypes.FLYING_AND_SWIMMING, duckData.getSpeed(), duckData.getRezistance());

    }

    @Override
    public String toString() {
        return super.toString()+"FlyingAndSwimmingDuck{}";
    }


}
