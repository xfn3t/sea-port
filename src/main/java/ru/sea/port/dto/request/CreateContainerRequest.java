package ru.sea.port.dto.request;

import lombok.Getter;
import lombok.Setter;
import ru.sea.port.model.container.DepartureType;
import ru.sea.port.model.container.StorageType;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateContainerRequest {
	private Long shipId;
	private Boolean damageStatus;
	private StorageType storageType;
	private DepartureType departureType;
	private LocalDateTime scheduledArrivalDate;
	private LocalDateTime scheduledDepartureDate;
}