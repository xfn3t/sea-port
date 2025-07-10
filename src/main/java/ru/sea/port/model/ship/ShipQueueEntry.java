package ru.sea.port.model.ship;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ship_queue")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipQueueEntry {

	@Id
	private Long shipId;

	@Column(nullable = false)
	private LocalDateTime arrivalTs;
}