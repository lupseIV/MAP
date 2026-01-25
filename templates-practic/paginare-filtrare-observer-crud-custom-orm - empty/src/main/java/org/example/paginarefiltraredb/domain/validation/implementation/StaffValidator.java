//package org.example.paginarefiltraredb.domain.validation.implementation;
//
//import org.example.paginarefiltraredb.domain.exceptions.ValidationException;
//import org.example.paginarefiltraredb.domain.validation.Validator;
//
//import java.math.BigDecimal;
//
//public class StaffValidator implements Validator<Staff> {
//    @Override
//    public void validate(Staff entity) throws ValidationException {
//        if (entity == null) {
//            throw new ValidationException("Staff entity cannot be null!");
//        }
//
//        String errors = "";
//
//        if (entity.getName() == null || entity.getName().trim().isEmpty()) {
//            errors += "Staff Name cannot be empty.\n";
//        }
//        if (entity.getEmail() == null || !entity.getEmail().contains("@")) {
//            errors += "Invalid email format.\n";
//        }
//        if (entity.getSalary() == null || entity.getSalary().compareTo(BigDecimal.ZERO) <= 0) {
//            errors += "Salary must be greater than 0.\n";
//        }
//        if (entity.getDepartment() == null || entity.getDepartment().isEmpty()) {
//            errors += "Department cannot be empty.\n";
//        }
//
//        if (!errors.isEmpty()) {
//            throw new ValidationException(errors);
//        }
//    }
//}