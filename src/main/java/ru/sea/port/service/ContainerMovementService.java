package ru.sea.port.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sea.port.model.*;
import ru.sea.port.model.location.Location;
import ru.sea.port.model.location.LocationType;
import ru.sea.port.model.location.departure.DepartureRail;
import ru.sea.port.model.location.departure.DepartureShip;
import ru.sea.port.model.location.departure.DepartureTruck;
import ru.sea.port.model.location.sorting.SortingRefrigerated;
import ru.sea.port.model.location.sorting.SortingRegular;
import ru.sea.port.model.location.warehouse.WarehouseDamaged;
import ru.sea.port.model.location.warehouse.WarehouseRefrigerated;
import ru.sea.port.model.location.warehouse.WarehouseRegular;
import ru.sea.port.repository.ContainerRepository;
import ru.sea.port.repository.location.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContainerMovementService {

	private final ContainerRepository containerRepo;
	private final SortingRegularRepository sortingRegularRepo;
	private final SortingRefrigeratedRepository sortingRefrigeratedRepo;
	private final WarehouseRegularRepository warehouseRegularRepo;
	private final WarehouseRefrigeratedRepository warehouseRefrigeratedRepo;
	private final WarehouseDamagedRepository warehouseDamagedRepo;
	private final DepartureRailRepository departureRailRepo;
	private final DepartureTruckRepository departureTruckRepo;
	private final DepartureShipRepository departureShipRepo;

	@Transactional
	public void moveToSorting(Container container, LocalDateTime arrivalTime) {
		Location location;

		if (container.getStorageType().getName() == StorageType.REFRIGERATED) {
			location = new SortingRefrigerated();
		} else {
			location = new SortingRegular();
		}

		location.setArrivalTime(arrivalTime);
		container.setCurrentLocation(location);
		saveLocation(location);
	}

	@Transactional
	public void moveToLocation(Container container, LocationType locationType, LocalDateTime arrivalTime) {
		switch (locationType) {
			case SORTING_REGULAR, SORTING_REFRIGERATED:
				moveToSorting(container, arrivalTime);
				break;
			case WAREHOUSE_REGULAR:
			case WAREHOUSE_REFRIGERATED:
			case WAREHOUSE_DAMAGED:
				moveToWarehouse(container, arrivalTime);
				break;
			case DEPARTURE_RAIL:
			case DEPARTURE_TRUCK:
			case DEPARTURE_SHIP:
				moveToDeparture(container, arrivalTime);
				break;
			default:
				throw new IllegalArgumentException("Unknown location type: " + locationType);
		}
	}

	@Transactional
	public void moveToWarehouse(Container container, LocalDateTime arrivalTime) {
		Location location;

		if (container.getDamageStatus()) {
			location = new WarehouseDamaged();
		} else if (container.getStorageType().getName() == StorageType.REFRIGERATED) {
			location = new WarehouseRefrigerated();
		} else {
			location = new WarehouseRegular();
		}

		location.setArrivalTime(arrivalTime);
		container.setCurrentLocation(location);
		saveLocation(location);
	}

	@Transactional
	public void moveToDeparture(Container container, LocalDateTime arrivalTime) {
		Location location = switch (container.getDepartureType().getName()) {
			case RAIL -> new DepartureRail();
			case TRUCK -> new DepartureTruck();
			case SHIP -> new DepartureShip();
			default -> throw new IllegalStateException("Unknown departure type: " + container.getDepartureType());
		};

		location.setArrivalTime(arrivalTime);
		container.setCurrentLocation(location);
		saveLocation(location);
	}

	private void saveLocation(Location location) {
		if (location instanceof SortingRegular) {
			sortingRegularRepo.save((SortingRegular) location);
		} else if (location instanceof SortingRefrigerated) {
			sortingRefrigeratedRepo.save((SortingRefrigerated) location);
		} else if (location instanceof WarehouseRegular) {
			warehouseRegularRepo.save((WarehouseRegular) location);
		} else if (location instanceof WarehouseRefrigerated) {
			warehouseRefrigeratedRepo.save((WarehouseRefrigerated) location);
		} else if (location instanceof WarehouseDamaged) {
			warehouseDamagedRepo.save((WarehouseDamaged) location);
		} else if (location instanceof DepartureRail) {
			departureRailRepo.save((DepartureRail) location);
		} else if (location instanceof DepartureTruck) {
			departureTruckRepo.save((DepartureTruck) location);
		} else if (location instanceof DepartureShip) {
			departureShipRepo.save((DepartureShip) location);
		}
	}

	@Transactional
	public void processContainerMovement() {
		List<Container> containers = containerRepo.findAll();
		LocalDateTime now = LocalDateTime.now();

		for (Container container : containers) {
			Location currentLocation = container.getCurrentLocation();
			if (currentLocation == null) continue;

			// Обработка контейнеров в сортировочных цехах
			if (currentLocation.getLocationType() == LocationType.SORTING_REGULAR ||
					currentLocation.getLocationType() == LocationType.SORTING_REFRIGERATED) {

				if (currentLocation.getArrivalTime().plusDays(1).isBefore(now)) {
					moveToWarehouse(container, now);
				}
			}

			// Обработка контейнеров на складах
			else if (currentLocation.getLocationType() == LocationType.WAREHOUSE_REGULAR ||
					currentLocation.getLocationType() == LocationType.WAREHOUSE_REFRIGERATED ||
					currentLocation.getLocationType() == LocationType.WAREHOUSE_DAMAGED) {

				if (container.getScheduledDepartureDate().minusDays(1).isBefore(now)) {
					moveToDeparture(container, now);
				}
			}
		}
	}
}