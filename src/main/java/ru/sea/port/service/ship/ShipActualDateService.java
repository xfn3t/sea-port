package ru.sea.port.service.ship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sea.port.repository.ship.ShipActualDateRepository;

@Service
@RequiredArgsConstructor
public class ShipActualDateService {

	private final ShipActualDateRepository shipActualDateRepository;


	public void deleteAll() {
		shipActualDateRepository.deleteAll();
	}
}
