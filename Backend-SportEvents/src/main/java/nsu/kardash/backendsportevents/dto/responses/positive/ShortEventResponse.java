package nsu.kardash.backendsportevents.dto.responses.positive;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ShortEventResponse implements Serializable {
    private final long id;
    private final String name;
    private final int freeSeatCount;
}
