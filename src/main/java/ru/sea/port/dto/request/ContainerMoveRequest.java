package ru.sea.port.dto.request;

import lombok.Getter;
import lombok.Setter;
import ru.sea.port.model.location.LocationType;

@Getter
@Setter
public class ContainerMoveRequest {
	private LocationType locationType;
}