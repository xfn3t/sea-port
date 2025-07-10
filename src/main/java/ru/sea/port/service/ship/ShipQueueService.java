package ru.sea.port.service.ship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sea.port.repository.ship.ShipQueueRepository;

@Service
@RequiredArgsConstructor
public class ShipQueueService {

	private final ShipQueueRepository queueRepo;

	public void deleteAll() {
		queueRepo.deleteAll();
	}
}
