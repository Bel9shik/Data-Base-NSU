package nsu.kardash.backendsportevents.dto.responses.positive;

import lombok.Data;
import nsu.kardash.backendsportevents.models.SportsVenue;

import java.util.Map;

@Data
public class SportVenueResponse {

    Map<Integer, Map<String, Object>> venues;
//    Map<String, Object> venues;

    public SportVenueResponse(SportsVenue venue) {

    }

    public SportVenueResponse(Map<Integer, Map<String, Object>> venues) {

        this.venues = venues;
    }

}
