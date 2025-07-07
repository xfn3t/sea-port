package ru.sea.port.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PierStatusDTO {
	private Long pierId;
	private boolean occupied;
	private Long shipId;
	private String shipNumber;
	private Integer maxShipLength;
	private Integer currentShipLength;
}