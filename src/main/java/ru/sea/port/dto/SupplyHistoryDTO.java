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
public class SupplyHistoryDTO {
	private Long supplyId;
	private Long shipId;
	private String shipNumber;
	private LocalDateTime actualShipArrival;
	private LocalDateTime actualShipDeparture;
	private Long pierId;
}