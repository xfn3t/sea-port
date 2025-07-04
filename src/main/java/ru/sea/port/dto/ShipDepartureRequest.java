package ru.sea.port.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipDepartureRequest {
    @NotNull
    private Long shipId;
    @NotNull
    private LocalDateTime actualShipDeparture;
}
