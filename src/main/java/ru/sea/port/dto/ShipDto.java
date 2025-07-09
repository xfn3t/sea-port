package ru.sea.port.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipDto {

    private String shipNumber;
    private LocalDateTime scheduledArrivalDate;
    private LocalDateTime scheduledDepartureDate;
    private Integer shipLength;
    private Integer containerCount;
}
