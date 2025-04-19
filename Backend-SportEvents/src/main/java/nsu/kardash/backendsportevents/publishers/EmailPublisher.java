package nsu.kardash.backendsportevents.publishers;

public interface EmailPublisher {

    void sendEmail(String toEmail, int verifyCode);

    void sendEmail(String toEmail, String subject, String body);
}
