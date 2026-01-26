package org.example.paginarefiltraredb.domain.validation.implementation;

import org.example.paginarefiltraredb.domain.exceptions.ValidationException;
import org.example.paginarefiltraredb.domain.validation.Validator;

public class ProgramareValidator implements Validator<Programare> {
    @Override
    public void validate(Programare entity) throws ValidationException {
        if (entity == null) {
            throw new ValidationException("Programare entity cannot be null!");
        }

        String errors = "";

        if (entity.getIdMedic() == null ) {
            errors += "Medic cannot be empty.\n";
        }
        if (entity.getIdPacient() == null ) {
            errors += "Pacient cannot be empty.\n";
        }
        if (entity.getDataOra() == null ) {
            errors += "Data cannot be negative.\n";
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}