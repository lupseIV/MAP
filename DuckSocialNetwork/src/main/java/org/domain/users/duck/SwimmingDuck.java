package org.domain.users.duck;

import org.domain.dtos.DuckData;
import org.utils.enums.types.DuckTypes;

public class SwimmingDuck extends Duck implements Swimming {
    @Override
    public void swim() {

    }

    public SwimmingDuck(String username, String password, String email, DuckTypes duckType, Double speed, Double rezistance) {
        super(username, password, email, duckType, speed, rezistance);
    }
    public SwimmingDuck(DuckData duckData) {
        super(duckData.getUsername(), duckData.getPassword(), duckData.getEmail(), DuckTypes.SWIMMING, duckData.getSpeed(), duckData.getRezistance());

    }

    @Override
    public String toString() {
        return super.toString()+"SwimmingDuck{}";
    }


}
