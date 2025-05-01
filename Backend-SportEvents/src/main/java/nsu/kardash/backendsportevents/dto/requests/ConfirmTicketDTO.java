package nsu.kardash.backendsportevents.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConfirmTicketDTO {

    @NotNull
    @Min(1)
    private long ticketID;

    @NotNull
    private boolean accepted;

}
