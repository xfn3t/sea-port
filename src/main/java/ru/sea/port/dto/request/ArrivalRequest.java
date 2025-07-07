package ru.sea.port.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ArrivalRequest {
	private Long shipId;
	private String shipNumber;
	private Integer shipLength;
	private LocalDateTime arrival;
	private LocalDateTime departure;
}
