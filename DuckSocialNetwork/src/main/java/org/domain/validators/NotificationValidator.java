package org.domain.validators;

import org.domain.exceptions.ValidationException;
import org.domain.users.relationships.notifications.FriendRequestNotification;
import org.domain.users.relationships.notifications.Notification;

public class NotificationValidator implements Validator<FriendRequestNotification> {
    @Override
    public void validate(FriendRequestNotification entity) throws ValidationException {

    }
}
