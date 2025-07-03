package ru.sea.port.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ship_length_id", foreignKey = @ForeignKey(name = "fk_ships_length_ref"))
    private ShipLength shipLength;

    @Column(name = "container_count", nullable = false)
    private Integer containerCount;

    @OneToMany(mappedBy = "ship", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("ship-containers")
    private List<Container> containers;

    @OneToMany(mappedBy = "ship", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("ship-fixedSupplies")
    private List<FixedSupply> fixedSupplies;

    @OneToMany(mappedBy = "ship", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("ship-piers")
    private List<Pier> piers;
}