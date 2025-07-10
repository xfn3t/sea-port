package ru.sea.port.model.ship;

import jakarta.persistence.*;
import lombok.*;
import ru.sea.port.model.container.Container;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ship_id")
    private Long shipId;

    @Column(name = "ship_number", nullable = false, length = 50)
    private String shipNumber;

    @Column(name = "scheduled_arrival_date", nullable = false)
    private LocalDateTime scheduledArrivalDate;

    @Column(name = "scheduled_departure_date", nullable = false)
    private LocalDateTime scheduledDepartureDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ship_length_id", nullable = false)
    private ShipLength shipLength;

    @Column(name = "container_count", nullable = false)
    private Integer containerCount;

    @OneToMany(mappedBy = "ship", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Container> containers;

    @OneToMany(mappedBy = "ship", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShipActualDate> actualDates;
}
