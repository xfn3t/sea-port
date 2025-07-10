package ru.sea.port.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContainerDepartureRequest {
    @NotNull
    private Long containerId;
    @NotNull
    private LocalDateTime actualContainerDeparture;
}
