package ru.sea.port.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sea.port.dto.request.ContainerMoveRequest;
import ru.sea.port.dto.response.ContainerLocationResponse;
import ru.sea.port.service.ContainerLogisticsService;

@RestController
@RequestMapping("/api/stevedore/container")
@RequiredArgsConstructor
public class ContainerLogisticsController {

	private final ContainerLogisticsService logisticsService;

	@GetMapping("/{containerId}/location")
	public ResponseEntity<?> getCurrentLocation(@PathVariable Long containerId) {
		return logisticsService.getCurrentLocation(containerId)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping("/{containerId}/move")
	public ResponseEntity<Void> moveContainer(
			@PathVariable Long containerId,
			@RequestBody ContainerMoveRequest request) {
		logisticsService.moveContainer(containerId, request.getLocationType());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/process-movement")
	public ResponseEntity<Void> processContainerMovement() {
		logisticsService.processContainerMovement();
		return ResponseEntity.ok().build();
	}


}