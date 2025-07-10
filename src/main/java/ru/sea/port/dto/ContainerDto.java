package ru.sea.port.dto;

import lombok.*;
import ru.sea.port.model.DepartureType;
import ru.sea.port.model.StorageType;
import ru.sea.port.model.location.LocationType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContainerDto {
	private Long containerId;
	private Boolean damageStatus;
	private StorageType storageType;
	private DepartureType departureType;
	private LocalDateTime scheduledArrivalDate;
	private LocalDateTime scheduledDepartureDate;
	private LocalDateTime actualArrival;
	private LocalDateTime actualDeparture;
	private LocationType currentLocationType;
	private LocalDateTime locationArrivalTime;
}