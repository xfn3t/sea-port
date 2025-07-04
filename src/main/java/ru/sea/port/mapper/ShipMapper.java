package ru.sea.port.mapper;

import org.mapstruct.Mapper;
import ru.sea.port.dto.ShipDto;
import ru.sea.port.model.Ship;

@Mapper(componentModel = "spring")
public interface ShipMapper {
    ShipDto toDto(Ship ship);
}
