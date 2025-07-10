package ru.sea.port.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sea.port.dto.request.CreateContainerRequest;
import ru.sea.port.dto.response.ContainerLocationResponse;
import ru.sea.port.model.*;
import ru.sea.port.model.location.Location;
import ru.sea.port.model.location.LocationType;
import ru.sea.port.repository.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContainerLogisticsService {

	private final ContainerRepository containerRepo;
	private final ContainerMovementService movementService;
	private final ShipRepository shipRepo;
	private final ShipLengthRepository shipLengthRepo;
	private final StorageTypeRepository storageTypeRepository;
	private final DepartureTypeRepository departureTypeRepository;

	@Transactional(readOnly = true)
	public Optional<ContainerLocationResponse> getCurrentLocation(Long containerId) {
		return containerRepo.findById(containerId)
				.map(container -> {
					Location location = container.getCurrentLocation();
					if (location == null) return null;

					return new ContainerLocationResponse(
							containerId,
							location.getLocationType(),
							location.getArrivalTime(),
							location.getDepartureTime()
					);
				});
	}

	@Transactional
	public void moveContainer(Long containerId, LocationType locationType) {
		Container container = containerRepo.findById(containerId)
				.orElseThrow(() -> new EntityNotFoundException("Container not found"));

		movementService.moveToLocation(container, locationType, LocalDateTime.now());
	}

	@Transactional
	public void processContainerMovement() {
		movementService.processContainerMovement();
	}

	@Transactional
	public Container createContainer(CreateContainerRequest req) {
		Ship ship = shipRepo.findById(req.getShipId())
				.orElseThrow(() -> new EntityNotFoundException("Ship not found: " + req.getShipId()));

		StorageTypeEntity storageType = storageTypeRepository.findByStringName(req.getStorageType().name())
				.orElseThrow(() -> new EntityNotFoundException("Storage type not found: " + req.getStorageType()));

		DepartureTypeEntity departureType = departureTypeRepository.findByStringName(req.getDepartureType().name())
				.orElseThrow(() -> new EntityNotFoundException("Departure type not found: " + req.getDepartureType()));

		Container container = new Container();
		container.setShip(ship);
		container.setDamageStatus(req.getDamageStatus());
		container.setStorageType(storageType);
		container.setDepartureType(departureType);
		container.setScheduledArrivalDate(req.getScheduledArrivalDate());
		container.setScheduledDepartureDate(req.getScheduledDepartureDate());

		return containerRepo.save(container);
	}

	@Transactional
	public Ship createShip(String shipNumber, int shipLength, int containerCount,
						   LocalDateTime arrival, LocalDateTime departure) {
		ShipLength length = shipLengthRepo.findFirstByLengthValue(shipLength)
				.orElseGet(() -> {
					ShipLength nl = new ShipLength();
					nl.setLengthValue(shipLength);
					return shipLengthRepo.save(nl);
				});

		Ship ship = new Ship();
		ship.setShipNumber(shipNumber);
		ship.setShipLength(length);
		ship.setContainerCount(containerCount);
		ship.setScheduledArrivalDate(arrival);
		ship.setScheduledDepartureDate(departure);

		return shipRepo.save(ship);
	}
}