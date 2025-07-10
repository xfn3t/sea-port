package ru.sea.port.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.sea.port.dto.request.ContainerArrivalRequest;
import ru.sea.port.dto.request.ContainerDepartureRequest;
import ru.sea.port.dto.request.ShipArrivalRequest;
import ru.sea.port.dto.request.ShipDepartureRequest;
import ru.sea.port.model.*;
import ru.sea.port.repository.*;

import java.util.List;
import java.util.Optional;

@Service
public class ActualDateService {

    private final ShipRepository shipRepo;
    private final ContainerRepository containerRepo;
    private final ShipActualDateRepository shipEventRepo;
    private final ContainerActualDateRepository containerEventRepo;
    private final ContainerMovementService movementService;

    public ActualDateService(ShipRepository shipRepo,
                             ContainerRepository containerRepo,
                             ShipActualDateRepository shipEventRepo,
                             ContainerActualDateRepository containerEventRepo,
                             ContainerMovementService movementService) {
        this.shipRepo = shipRepo;
        this.containerRepo = containerRepo;
        this.shipEventRepo = shipEventRepo;
        this.containerEventRepo = containerEventRepo;
        this.movementService = movementService;
    }

    @Transactional
    public ShipActualDate recordShipArrival(ShipArrivalRequest req) {
        Ship ship = shipRepo.findById(req.getShipId())
                .orElseThrow(() -> new EntityNotFoundException("Ship not found: " + req.getShipId()));
        ShipActualDate ev = new ShipActualDate();
        ev.setShip(ship);
        ev.setActualArrival(req.getActualShipArrival());
        return shipEventRepo.save(ev);
    }

    @Transactional
    public ShipActualDate recordShipDeparture(ShipDepartureRequest req) {
        ShipActualDate ev = shipEventRepo.findFirstByShipShipIdOrderByIdDesc(req.getShipId())
                .orElseThrow(() -> new EntityNotFoundException("No arrival record for ship: " + req.getShipId()));
        ev.setActualDeparture(req.getActualShipDeparture());
        return shipEventRepo.save(ev);
    }

    @Transactional
    public ContainerActualDate recordContainerArrival(ContainerArrivalRequest req) {
        Container container = containerRepo.findById(req.getContainerId())
                .orElseThrow(() -> new EntityNotFoundException("Container not found: " + req.getContainerId()));

        ContainerActualDate actualDate = new ContainerActualDate();
        actualDate.setContainer(container);
        actualDate.setActualArrival(req.getActualContainerArrival());
        container.setActualDate(actualDate);

        // Автоматическое перемещение в сортировочный цех
        movementService.moveToSorting(container, req.getActualContainerArrival());

        return containerEventRepo.save(actualDate);
    }

    @Transactional
    public ContainerActualDate recordContainerDeparture(ContainerDepartureRequest req) {
        ContainerActualDate ev = containerEventRepo.findByContainerContainerId(req.getContainerId())
                .orElseThrow(() -> new EntityNotFoundException("No arrival record for container"));

        ev.setActualDeparture(req.getActualContainerDeparture());

        // Устанавливаем время убытия из текущего местоположения
        if (ev.getContainer().getCurrentLocation() != null) {
            ev.getContainer().getCurrentLocation()
                    .setDepartureTime(req.getActualContainerDeparture());
        }

        return containerEventRepo.save(ev);
    }

    @Transactional(readOnly = true)
    public List<ShipActualDate> listShipEvents(Long shipId) {
        return shipEventRepo.findByShipShipId(shipId);
    }

    @Transactional(readOnly = true)
    public Optional<ContainerActualDate> getContainerEvent(Long containerId) {
        return containerEventRepo.findByContainerContainerId(containerId);
    }
}