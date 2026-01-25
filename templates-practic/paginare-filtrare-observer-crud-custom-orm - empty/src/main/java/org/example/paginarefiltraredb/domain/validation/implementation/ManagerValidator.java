//package org.example.paginarefiltraredb.domain.validation.implementation;
//
//import org.example.paginarefiltraredb.domain.exceptions.ValidationException;
//import org.example.paginarefiltraredb.domain.validation.Validator;
//
//import java.math.BigDecimal;
//
//public class ManagerValidator implements Validator<Manager> {
//    @Override
//    public void validate(Manager entity) throws ValidationException {
//        if (entity == null) throw new ValidationException("Manager cannot be null");
//
//        String errors = "";
//
//        // 1. Base Staff Validation
//        if (entity.getName() == null || entity.getName().isEmpty()) errors += "Name is required.\n";
//        if (entity.getEmail() == null || !entity.getEmail().contains("@")) errors += "Invalid email.\n";
//        if (entity.getSalary() == null || entity.getSalary().compareTo(BigDecimal.ZERO) <= 0)
//            errors += "Salary must be positive.\n";
//
//        // 2. Manager Specific Validation
//        if (entity.getTeamSize() < 0) {
//            errors += "Team size cannot be negative.\n";
//        }
//        if (entity.getBonus() < 0) {
//            errors += "Bonus cannot be negative.\n";
//        }
//        if (entity.getAccessLevel() < 1 || entity.getAccessLevel() > 10) {
//            errors += "Access Level must be between 1 and 10.\n";
//        }
//
//        if (!errors.isEmpty()) {
//            throw new ValidationException(errors);
//        }
//    }
//}