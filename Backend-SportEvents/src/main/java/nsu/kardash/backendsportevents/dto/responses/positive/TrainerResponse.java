package nsu.kardash.backendsportevents.dto.responses.positive;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrainerResponse {

    private long id;
    private String firstName;
    private String surname;
    private String lastName;

}
