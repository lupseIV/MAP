package org.example.paginarefiltraredb.domain.validation.implementation;

import org.example.paginarefiltraredb.domain.entities.VipClient;
import org.example.paginarefiltraredb.domain.exceptions.ValidationException;
import org.example.paginarefiltraredb.domain.validation.Validator;

public class VipClientValidator implements Validator<VipClient> {
    @Override
    public void validate(VipClient entity) throws ValidationException {
        if (entity == null) throw new ValidationException("VipClient cannot be null");

        String errors = "";

        // 1. Base Client Logic
        if (entity.getName() == null || entity.getName().isEmpty()) errors += "Name required.\n";
        if (entity.getBudget() == null || entity.getBudget() < 0) errors += "Invalid budget.\n";

        // 2. VIP Specific Logic
        if (entity.getLoyaltyPoints() < 0) {
            errors += "Loyalty points cannot be negative.\n";
        }

        // Critical: A VIP must have a manager assigned
        if (entity.getPersonalManager() == null) {
            errors += "A VIP Client must have an assigned Personal Manager.\n";
        } else {
            // Optional: Check if manager has ID (is saved)
            if (entity.getPersonalManager().getId() == null) {
                errors += "Assigned Personal Manager must be a valid saved entity (ID is null).\n";
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}