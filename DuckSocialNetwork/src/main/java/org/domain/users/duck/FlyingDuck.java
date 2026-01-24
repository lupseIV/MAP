package org.domain.users.duck;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.domain.dtos.DuckData;
import org.utils.enums.types.DuckTypes;

@JsonTypeName("org.domain.users.duck.FlyingDuck")
public class FlyingDuck extends Duck implements  Flying {
    @Override
    public void fly() {

    }

    public FlyingDuck() {
        super();
    }

    public FlyingDuck(String username, String password, String email, DuckTypes duckType, Double speed, Double rezistance) {
        super(username, password, email, duckType, speed, rezistance);
    }

    @Override
    public String toString() {
        return super.toString()+"FlyingDuck{}";
    }

    public FlyingDuck(DuckData duckData) {
        super(duckData.getUsername(), duckData.getPassword(), duckData.getEmail(), DuckTypes.FLYING, duckData.getSpeed(), duckData.getRezistance());

    }


}
