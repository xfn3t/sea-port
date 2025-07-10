package ru.sea.port.service.ship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sea.port.model.ship.ShipLength;
import ru.sea.port.repository.ship.ShipLengthRepository;

@Service
@RequiredArgsConstructor
public class ShipLengthService {

	private final ShipLengthRepository shipLengthRepository;

	public ShipLength save(ShipLength shipLength) {
		return shipLengthRepository.save(shipLength);
	}
}
