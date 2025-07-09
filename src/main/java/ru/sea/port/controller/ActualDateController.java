package ru.sea.port.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sea.port.dto.*;
import ru.sea.port.mapper.ActualDateMapper;
import ru.sea.port.model.ContainerActualDate;
import ru.sea.port.model.ShipActualDate;
import ru.sea.port.service.ActualDateService;

import java.util.List;

@RestController
@RequestMapping("/api/stevedore")
@RequiredArgsConstructor
public class ActualDateController {

    private final ActualDateService service;
    private final ActualDateMapper mapper;

    @PostMapping("/ship/arrival")
    public ResponseEntity<ShipActualDateResponse> shipArrival(
            @Valid @RequestBody ShipArrivalRequest req) {
        ShipActualDate ev = service.recordShipArrival(req);
        return ResponseEntity.ok(mapper.toShipResponse(ev));
    }

    @PostMapping("/ship/departure")
    public ResponseEntity<ShipActualDateResponse> shipDeparture(
            @Valid @RequestBody ShipDepartureRequest req) {
        ShipActualDate ev = service.recordShipDeparture(req);
        return ResponseEntity.ok(mapper.toShipResponse(ev));
    }

    @GetMapping("/ship/{shipId}/events")
    public ResponseEntity<List<ShipActualDateResponse>> shipEvents(
            @PathVariable Long shipId) {
        List<ShipActualDate> events = service.listShipEvents(shipId);
        return ResponseEntity.ok(mapper.toShipResponseList(events));
    }

    @PostMapping("/container/arrival")
    public ResponseEntity<ContainerActualDateResponse> containerArrival(
            @Valid @RequestBody ContainerArrivalRequest req) {
        ContainerActualDate ev = service.recordContainerArrival(req);
        return ResponseEntity.ok(mapper.toContainerResponse(ev));
    }

    @PostMapping("/container/departure")
    public ResponseEntity<ContainerActualDateResponse> containerDeparture(
            @Valid @RequestBody ContainerDepartureRequest req) {
        ContainerActualDate ev = service.recordContainerDeparture(req);
        return ResponseEntity.ok(mapper.toContainerResponse(ev));
    }

    @GetMapping("/container/{containerId}/event")
    public ResponseEntity<ContainerActualDateResponse> containerEvent(
            @PathVariable Long containerId) {
        return service.getContainerEvent(containerId)
                .map(mapper::toContainerResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
