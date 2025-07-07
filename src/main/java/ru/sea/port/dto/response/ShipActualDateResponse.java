package ru.sea.port.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipActualDateResponse {
    private Long id;
    private Long shipId;
    private String shipNumber;
    private LocalDateTime actualArrival;
    private LocalDateTime actualDeparture;
}
