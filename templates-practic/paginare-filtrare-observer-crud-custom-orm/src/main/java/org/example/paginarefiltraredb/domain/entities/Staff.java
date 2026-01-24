package org.example.paginarefiltraredb.domain.entities;

import org.example.paginarefiltraredb.customORM.annotations.DbColumn;
import org.example.paginarefiltraredb.customORM.annotations.DbTable;

import java.math.BigDecimal;
import java.time.LocalDate;

@DbTable(name = "staff")
public class Staff extends Person {

    @DbColumn(nullable = false)
    private BigDecimal salary;

    @DbColumn(name = "hire_date")
    private LocalDate hireDate;

    private String department; // "Sales", "IT", etc.

    // Getters/Setters
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}