package nsu.kardash.backendsportevents.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tickets")
@Getter
@Setter
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "eventid", nullable = false)
    private Event event;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "visitorid", nullable = false)
    private Person person;

    @NotNull
    private OffsetDateTime registrationTime = OffsetDateTime.now();

    @NotNull
    @Size(max = 20)
    private String status = "Pending";

}
