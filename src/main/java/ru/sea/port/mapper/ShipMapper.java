package ru.sea.port.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.sea.port.dto.ShipDto;
import ru.sea.port.model.Ship;
import ru.sea.port.model.ShipLength;

@Mapper(componentModel = "spring")
public interface ShipMapper {

    @Mapping(source = "shipLength.lengthValue", target = "shipLength")
    ShipDto toDto(Ship ship);

    default Integer map(ShipLength shipLength) {
        if (shipLength == null) {
            return null;
        }
        return shipLength.getLengthValue();
    }
}
