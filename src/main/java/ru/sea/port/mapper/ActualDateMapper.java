package ru.sea.port.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.sea.port.dto.response.ContainerActualDateResponse;
import ru.sea.port.dto.response.ShipActualDateResponse;
import ru.sea.port.model.container.ContainerActualDate;
import ru.sea.port.model.ship.ShipActualDate;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ActualDateMapper {
    @Mappings({
            @Mapping(source = "ship.shipId",     target = "shipId"),
            @Mapping(source = "ship.shipNumber", target = "shipNumber")
    })
	ShipActualDateResponse toShipResponse(ShipActualDate entity);

    List<ShipActualDateResponse> toShipResponseList(List<ShipActualDate> entities);

    ContainerActualDateResponse toContainerResponse(ContainerActualDate entity);

    List<ContainerActualDateResponse> toContainerResponseList(List<ContainerActualDate> entities);
}
