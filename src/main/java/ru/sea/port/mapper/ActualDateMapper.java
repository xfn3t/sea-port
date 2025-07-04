package ru.sea.port.mapper;

import org.mapstruct.Mapper;
import ru.sea.port.dto.*;
import ru.sea.port.model.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ActualDateMapper {

    ShipActualDateResponse toShipResponse(ShipActualDate entity);

    List<ShipActualDateResponse> toShipResponseList(List<ShipActualDate> entities);

    ContainerActualDateResponse toContainerResponse(ContainerActualDate entity);

    List<ContainerActualDateResponse> toContainerResponseList(List<ContainerActualDate> entities);
}
