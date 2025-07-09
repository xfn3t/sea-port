package ru.sea.port.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ship_actual_dates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipActualDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // связь многие → один: у каждого Ship может быть много таких записей
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ship_id", nullable = false)
    private Ship ship;

    @Column(name = "actual_arrival")
    private LocalDateTime actualArrival;
    @Column(name = "actual_departure")
    private LocalDateTime actualDeparture;
}
