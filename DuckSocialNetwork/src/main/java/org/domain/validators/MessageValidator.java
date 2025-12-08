package org.domain.validators;

import org.domain.events.RaceEvent;
import org.domain.exceptions.ValidationException;
import org.domain.users.relationships.messages.Message;

public class MessageValidator implements Validator<Message>{
    @Override
    public void validate(Message entity) throws ValidationException {
    }
}
