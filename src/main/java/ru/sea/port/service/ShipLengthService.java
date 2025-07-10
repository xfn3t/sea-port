package ru.sea.port.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sea.port.model.ShipLength;
import ru.sea.port.repository.ShipLengthRepository;

@Service
@RequiredArgsConstructor
public class ShipLengthService {

	private final ShipLengthRepository shipLengthRepository;

	public ShipLength save(ShipLength shipLength) {
		return shipLengthRepository.save(shipLength);
	}
}
