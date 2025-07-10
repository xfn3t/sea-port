package ru.sea.port.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sea.port.dto.request.CreateContainerRequest;
import ru.sea.port.dto.request.CreateShipRequest;
import ru.sea.port.model.Container;
import ru.sea.port.model.Pier;
import ru.sea.port.model.Ship;
import ru.sea.port.model.ShipLength;
import ru.sea.port.service.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
public class AdminController {

	private final PierService pierService;
	private final ShipService shipService;
	private final ShipQueueService queueService;
	private final PierBookingService bookingService;
	private final ShipActualDateService shipActualDateService;
	private final ContainerActualDateService containerActualDateService;
	private final ShipLengthService shipLengthService;
	private final ContainerLogisticsService logisticsService;

	@PostMapping("/reset")
	public ResponseEntity<Void> resetDatabase() {
		// Очистка всех таблиц в правильном порядке
		bookingService.deleteAll();
		queueService.deleteAll();
		containerActualDateService.deleteAll();
		shipActualDateService.deleteAll();
		pierService.deleteAll();
		shipService.deleteAll();
		return ResponseEntity.ok().build();
	}

	@PostMapping("/create-piers")
	public ResponseEntity<Void> createPiers(@RequestBody List<Integer> lengths) {

		for (Integer length : lengths) {
			ShipLength shipLength = new ShipLength();
			shipLength.setLengthValue(length);
			shipLengthService.save(shipLength); // Сохраняем объект

			Pier pier = new Pier();
			pier.setMaxShipLength(shipLength);
			pierService.save(pier);
		}
		return ResponseEntity.ok().build();
	}

	@PostMapping("/create-container")
	public ResponseEntity<Long> createContainer(@RequestBody CreateContainerRequest req) {
		Container container = logisticsService.createContainer(req);
		return ResponseEntity.ok(container.getContainerId());
	}

	@PostMapping("/create-ship")
	public ResponseEntity<Long> createShip(@RequestBody CreateShipRequest request) {
		Ship ship = logisticsService.createShip(
				request.getShipNumber(),
				request.getShipLength(),
				request.getContainerCount(),
				request.getScheduledArrival(),
				request.getScheduledDeparture()
		);
		return ResponseEntity.ok(ship.getShipId());
	}


	@PostMapping("/process-container-movement")
	public ResponseEntity<Void> processContainerMovement() {
		logisticsService.processContainerMovement();
		return ResponseEntity.ok().build();
	}


}