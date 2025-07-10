package ru.sea.port.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateShipRequest {
	private String shipNumber;
	private int shipLength;
	private int containerCount;
	private LocalDateTime scheduledArrival;
	private LocalDateTime scheduledDeparture;
}