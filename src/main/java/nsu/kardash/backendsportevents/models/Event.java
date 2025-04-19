package nsu.kardash.backendsportevents.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private int cost;

    @NotNull
    private OffsetDateTime startedAt;

    @NotNull
    private OffsetDateTime endedAt;

    @NotNull
    private int seatCount;

    @ManyToOne
    @JoinColumn(name = "venueid", nullable = false)
    private SportsVenue venue;

    @ManyToOne
    @JoinColumn(name = "trainerid", nullable = false)
    private Trainer trainer;

}

