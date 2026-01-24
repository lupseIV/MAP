package org.example.paginarefiltraredb.domain.entities;

import org.example.paginarefiltraredb.customORM.annotations.DbColumn;
import org.example.paginarefiltraredb.customORM.annotations.DbTable;

@DbTable(name = "managers")
public class Manager extends Staff {

    @DbColumn(nullable = false)
    private Double bonus;

    @DbColumn(name = "team_size")
    private int teamSize;

    @DbColumn(name = "access_level")
    private int accessLevel; // 1-5

    // Getters/Setters
    public Double getBonus() { return bonus; }
    public void setBonus(Double bonus) { this.bonus = bonus; }
    public int getTeamSize() { return teamSize; }
    public void setTeamSize(int teamSize) { this.teamSize = teamSize; }
    public int getAccessLevel() { return accessLevel; }
    public void setAccessLevel(int accessLevel) { this.accessLevel = accessLevel; }
}