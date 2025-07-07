package ru.sea.port.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContainerActualDateResponse {
    private Long containerId;
    private LocalDateTime actualArrival;
    private LocalDateTime actualDeparture;
}
