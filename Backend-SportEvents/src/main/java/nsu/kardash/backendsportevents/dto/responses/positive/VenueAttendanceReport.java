package nsu.kardash.backendsportevents.dto.responses.positive;

public record VenueAttendanceReport(
        long venueId,
        String venueName,
        long attendedCount
) {}
