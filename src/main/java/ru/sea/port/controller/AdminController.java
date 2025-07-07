package ru.sea.port.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sea.port.model.Pier;
import ru.sea.port.model.ShipLength;
import ru.sea.port.repository.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
public class AdminController {

	private final PierRepository pierRepo;
	private final ShipRepository shipRepo;
	private final ShipQueueRepository queueRepo;
	private final PierBookingRepository bookingRepo;
	private final ShipActualDateRepository shipActualDateRepository;
	private final ContainerActualDateRepository containerActualDateRepository;
	private final ShipLengthRepository shipLengthRepository;


	@PostMapping("/reset")
	public ResponseEntity<Void> resetDatabase() {
		// Очистка всех таблиц в правильном порядке
		bookingRepo.deleteAll();
		queueRepo.deleteAll();
		containerActualDateRepository.deleteAll();
		shipActualDateRepository.deleteAll();
		pierRepo.deleteAll();
		shipRepo.deleteAll();
		return ResponseEntity.ok().build();
	}

	@PostMapping("/create-piers")
	public ResponseEntity<Void> createPiers(@RequestBody List<Integer> lengths) {

		for (Integer length : lengths) {
			ShipLength shipLength = new ShipLength();
			shipLength.setLengthValue(length);
			shipLengthRepository.save(shipLength); // Сохраняем объект

			Pier pier = new Pier();
			pier.setMaxShipLength(shipLength);
			pierRepo.save(pier);
		}
		return ResponseEntity.ok().build();
	}
}