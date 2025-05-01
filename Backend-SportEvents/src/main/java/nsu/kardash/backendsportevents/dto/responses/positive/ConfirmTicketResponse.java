package nsu.kardash.backendsportevents.dto.responses.positive;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ConfirmTicketResponse {

    private long ticketID;
    private String firstName;
    private String surname;
    private String lastName;
    private String email;
    private boolean isConfirmedEmail;

    private String venueName;
    private String eventName;

}
