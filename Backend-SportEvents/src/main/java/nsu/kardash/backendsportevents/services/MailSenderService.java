package nsu.kardash.backendsportevents.services;

import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.publishers.EmailPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Retryable(
        value = { MailAuthenticationException.class },
        maxAttempts = 2,
        backoff = @Backoff(delay = 60000) // 1 минута
)
public class MailSenderService implements EmailPublisher {

    @Value("${spring.mail.username}")
    private String from;
    private final JavaMailSender mailSender;
    private final SimpleMailMessage mailMessage;

    @Override
    public void sendNotifyEmail(String toEmail, String eventName) {

        mailMessage.setTo(toEmail);
        mailMessage.setSubject("Регистрация на мероприятие подтверждено");
        mailMessage.setText("Поздравляем! Вы можете принять участие в мероприятии \"" + eventName + "\"");
        mailMessage.setFrom(from);

        mailSender.send(mailMessage);

    }

    @Override
    public void sendNotifyEmail(String toEmail, int verifyCode) {

        mailMessage.setTo(toEmail);
        mailMessage.setSubject("PetHost confirm registration");
        mailMessage.setText("Registration confirmation code: " + verifyCode);
        mailMessage.setFrom(from);

        mailSender.send(mailMessage);

    }

    @Override
    public void sendNotifyEmailResetPassword(String toEmail, int verifyCode) {

        mailMessage.setTo(toEmail);
        mailMessage.setSubject("PetHost reset password");
        mailMessage.setText("Reset password confirmation code: " + verifyCode);
        mailMessage.setFrom(from);

        mailSender.send(mailMessage);

    }

    @Override
    public void sendNotifyEmail(String toEmail, String personFirstName, String eventName, OffsetDateTime eventStartedAt) {

        String body    = String.format(
                "Здравствуйте, %s!\n\n" +
                        "Через %d минут начнётся Ваше мероприятие \"%s\".\n" +
                        "Ждём Вас на площадке!",
                personFirstName,
                ChronoUnit.MINUTES.between(OffsetDateTime.now(), eventStartedAt),
                eventName
        );

        System.out.println("Сообщение отправлено " + toEmail + ": \n" + body);

        mailMessage.setTo(toEmail);
        mailMessage.setSubject("Скоро начнётся мероприятие");
        mailMessage.setText(body);
        mailMessage.setFrom(from);

        mailSender.send(mailMessage);

    }

}
