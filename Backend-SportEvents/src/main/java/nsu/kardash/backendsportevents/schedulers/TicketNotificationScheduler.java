package nsu.kardash.backendsportevents.schedulers;

import lombok.RequiredArgsConstructor;
import nsu.kardash.backendsportevents.models.Constants;
import nsu.kardash.backendsportevents.models.Ticket;
import nsu.kardash.backendsportevents.repositories.TicketRepository;
import nsu.kardash.backendsportevents.services.MailSenderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TicketNotificationScheduler {

    private final TicketRepository ticketRepository;
    private final MailSenderService mailSender;

    @Value("${app.notification.threshold.minutes:60}")
    private long thresholdMinutes;

    @Scheduled(fixedRateString = "${app.notification.fixedRate.millis}")
    public void checkAndNotify() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime deadline = now.plus(thresholdMinutes, ChronoUnit.MINUTES);
        List<Ticket> toNotify = ticketRepository.findTicketsByStatusAndEvent_StartedAtBetween(
                Constants.confirmed, now, deadline
        );

        toNotify.forEach(this::sendReminder);
    }

    private void sendReminder(Ticket t) {
        mailSender.sendNotifyEmail(
                t.getPerson().getEmail(),
                t.getPerson().getFirstname(),
                t.getEvent().getName(),
                t.getEvent().getStartedAt()
        );
    }
}
