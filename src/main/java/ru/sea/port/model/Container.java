package ru.sea.port.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "containers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Container {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "container_id")
    private Long containerId;

    @Column(name = "damage_status", nullable = false)
    private Boolean damageStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_type_id", nullable = false)
    @JsonIgnore
    private StorageType storageType;

    @Column(name = "scheduled_arrival_date", nullable = false)
    private LocalDateTime scheduledArrivalDate;

    @Column(name = "scheduled_departure_date", nullable = false)
    private LocalDateTime scheduledDepartureDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_type_id", nullable = false)
    @JsonIgnore
    private DepartureType departureType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ship_id", nullable = false)
    @JsonIgnore
    private Ship ship;

    @OneToOne(mappedBy = "container", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private ContainerActualDate actualDate;
}
