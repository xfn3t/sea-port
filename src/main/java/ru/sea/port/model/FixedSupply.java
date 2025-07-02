package ru.sea.port.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "fixed_supplies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FixedSupply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supply_id")
    private Long supplyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ship_id", nullable = false)
    @JsonIgnore
    private Ship ship;

    @Column(name = "actual_ship_arrival", nullable = false)
    private LocalDateTime actualShipArrival;

    @Column(name = "actual_ship_departure", nullable = false)
    private LocalDateTime actualShipDeparture;

    @Column(name = "actual_container_arrival", nullable = false)
    private LocalDateTime actualContainerArrival;

    @Column(name = "actual_container_departure", nullable = false)
    private LocalDateTime actualContainerDeparture;
}
