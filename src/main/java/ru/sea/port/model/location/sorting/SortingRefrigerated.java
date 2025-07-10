package ru.sea.port.model.location.sorting;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.sea.port.model.container.Container;
import ru.sea.port.model.location.Location;
import ru.sea.port.model.location.LocationType;

import java.time.LocalDateTime;

@Entity
@Table(name = "sorting_refrigerated")
@Getter
@Setter
public class SortingRefrigerated implements Location {
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
		return LocationType.SORTING_REFRIGERATED;
	}
}