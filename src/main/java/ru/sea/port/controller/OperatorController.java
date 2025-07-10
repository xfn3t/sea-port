package ru.sea.port.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sea.port.dto.request.ArrivalRequest;
import ru.sea.port.dto.request.DepartureRequest;
import ru.sea.port.dto.request.ActualArrivalRequest;
import ru.sea.port.service.PierAssignmentService;

@RestController
@RequestMapping("/api/operator")
@AllArgsConstructor
public class OperatorController {

	private final PierAssignmentService service;

	@PostMapping("/arrival")
	public ResponseEntity<Long> handlePlannedArrival(@RequestBody ArrivalRequest req) {
		System.out.println("Received planned arrival request: " + req);
		return ResponseEntity.ok(service.handleArrival(req));
	}

	@PostMapping("/actualArrival")
	public ResponseEntity<Void> handleActualArrival(@RequestBody ActualArrivalRequest req) {
		service.handleActualArrival(req);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/departure")
	public ResponseEntity<Void> handleDeparture(@RequestBody DepartureRequest req) {
		service.handleDeparture(req);
		return ResponseEntity.ok().build();
	}
}
