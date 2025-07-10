package ru.sea.port.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ShipNotFoundException extends RuntimeException {
	public ShipNotFoundException(Long shipId) {
		super("Ship with id=" + shipId + " not found");
	}
}