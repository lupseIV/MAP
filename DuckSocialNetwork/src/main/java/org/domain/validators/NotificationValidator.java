package org.domain.validators;

import org.domain.exceptions.ValidationException;
import org.domain.users.relationships.messages.Notification;

public class NotificationValidator implements Validator<Notification> {
    @Override
    public void validate(Notification entity) throws ValidationException {
        if (entity.getRecipient() == null) {
            throw new ValidationException("Notification recipient cannot be null");
        }
        if (entity.getSender() == null) {
            throw new ValidationException("Notification sender cannot be null");
        }
        if (entity.getMessagePreview() == null || entity.getMessagePreview().trim().isEmpty()) {
            throw new ValidationException("Notification message preview cannot be empty");
        }
    }
}
