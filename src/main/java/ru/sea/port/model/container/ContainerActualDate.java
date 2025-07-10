package ru.sea.port.model.container;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "container_actual_dates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerActualDate {
    @Id
    @Column(name = "container_id")
    private Long containerId;

    // связь один → один: PK этой таблицы — FK на containers.container_id
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "container_id", nullable = false)
    private Container container;

    @Column(name = "actual_arrival")
    private LocalDateTime actualArrival;
    @Column(name = "actual_departure")
    private LocalDateTime actualDeparture;
}
