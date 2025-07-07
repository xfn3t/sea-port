package ru.sea.port.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DepartureRequest {
	private Long shipId;
	private LocalDateTime departure;
}
