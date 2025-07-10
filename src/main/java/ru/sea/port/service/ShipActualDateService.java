package ru.sea.port.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sea.port.repository.ShipActualDateRepository;

@Service
@RequiredArgsConstructor
public class ShipActualDateService {

	private final ShipActualDateRepository shipActualDateRepository;


	public void deleteAll() {
		shipActualDateRepository.deleteAll();
	}
}
