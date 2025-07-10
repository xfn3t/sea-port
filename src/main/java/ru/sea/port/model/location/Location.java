package ru.sea.port.model.location;

import java.time.LocalDateTime;

public interface Location {
	LocationType getLocationType();
	LocalDateTime getArrivalTime();
	void setArrivalTime(LocalDateTime arrivalTime);
	LocalDateTime getDepartureTime();
	void setDepartureTime(LocalDateTime departureTime);
}