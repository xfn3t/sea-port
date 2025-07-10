package ru.sea.port.controller.admin;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.sea.port.dto.request.CreateContainerRequest;
import ru.sea.port.dto.request.CreateShipRequest;
import ru.sea.port.model.container.Container;
import ru.sea.port.model.pier.Pier;
import ru.sea.port.model.ship.Ship;
import ru.sea.port.model.ship.ShipLength;
import ru.sea.port.service.container.ContainerActualDateService;
import ru.sea.port.service.container.ContainerLogisticsService;
import ru.sea.port.service.pier.PierBookingService;
import ru.sea.port.service.pier.PierService;
import ru.sea.port.service.ship.ShipActualDateService;
import ru.sea.port.service.ship.ShipLengthService;
import ru.sea.port.service.ship.ShipQueueService;
import ru.sea.port.service.ship.ShipService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
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