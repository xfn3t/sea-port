package ru.sea.port.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sea.port.model.location.LocationType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContainerLocationResponse {
	private Long containerId;
	private LocationType currentLocation;
	private LocalDateTime arrivalTime;
	private LocalDateTime departureTime;
}