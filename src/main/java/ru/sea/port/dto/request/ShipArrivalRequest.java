package ru.sea.port.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShipArrivalRequest {
    @NotNull
    private Long shipId;
    @NotNull
    private LocalDateTime actualShipArrival;
}
