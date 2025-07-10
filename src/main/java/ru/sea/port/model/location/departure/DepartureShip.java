package ru.sea.port.model.location.departure;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.sea.port.model.Container;
import ru.sea.port.model.location.Location;
import ru.sea.port.model.location.LocationType;

import java.time.LocalDateTime;

@Entity
@Table(name = "departure_ship")
@Getter
@Setter
public class DepartureShip implements Location {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "container_id")
	private Container container;

	@Column(name = "arrival_time", nullable = false)
	private LocalDateTime arrivalTime;

	@Column(name = "departure_time")
	private LocalDateTime departureTime;

	@Override
	public LocationType getLocationType() {
		return LocationType.DEPARTURE_SHIP;
	}
}