package nsu.kardash.backendsportevents.dto.responses.positive;

import lombok.AllArgsConstructor;
import lombok.Data;
import nsu.kardash.backendsportevents.models.SportsVenue;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class SportVenueResponse {

    List<SportsVenue> venues;

}
