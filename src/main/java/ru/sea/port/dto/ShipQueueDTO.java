package ru.sea.port.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShipQueueDTO {
	private Long shipId;
	private String shipNumber;
	private LocalDateTime arrivalTs;
	private Integer shipLength;
}