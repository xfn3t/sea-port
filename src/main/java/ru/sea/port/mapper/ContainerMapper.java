package ru.sea.port.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.sea.port.dto.ContainerDto;
import ru.sea.port.model.container.*;
import ru.sea.port.model.location.LocationType;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface ContainerMapper {

	@Mapping(source = "container", target = "currentLocationType", qualifiedByName = "mapLocationType")
	@Mapping(source = "container", target = "locationArrivalTime", qualifiedByName = "mapArrivalTime")
	@Mapping(target = "storageType", expression = "java(mapStorageType(container.getStorageType()))")
	@Mapping(target = "departureType", expression = "java(mapDepartureType(container.getDepartureType()))")
	@Mapping(source = "actualDate.actualArrival", target = "actualArrival")
	@Mapping(source = "actualDate.actualDeparture", target = "actualDeparture")
	ContainerDto toDto(Container container);

	@org.mapstruct.Named("mapLocationType")
	default LocationType mapLocationType(Container container) {
		if (container.getCurrentLocation() == null) return null;
		return container.getCurrentLocation().getLocationType();
	}

	@org.mapstruct.Named("mapArrivalTime")
	default LocalDateTime mapArrivalTime(Container container) {
		if (container.getCurrentLocation() == null) return null;
		return container.getCurrentLocation().getArrivalTime();
	}

	default StorageType mapStorageType(StorageTypeEntity storageType) {
		return storageType != null && storageType.getName() != null
				? storageType.getName()
				: null;
	}

	default DepartureType mapDepartureType(DepartureTypeEntity departureType) {
		return departureType != null && departureType.getName() != null
				? departureType.getName()
				: null;
	}
}