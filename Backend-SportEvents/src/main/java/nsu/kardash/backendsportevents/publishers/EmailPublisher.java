package nsu.kardash.backendsportevents.publishers;

import java.time.OffsetDateTime;

public interface EmailPublisher {

    void sendNotifyEmail(String toEmail, int verifyCode);

    void sendNotifyEmail(String toEmail, String personFirstName, String eventName, OffsetDateTime eventStartedAt);
}
