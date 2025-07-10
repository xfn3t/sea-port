package ru.sea.port.service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.sea.port.service.container.ContainerMovementService;

@Component
@RequiredArgsConstructor
public class ContainerMovementScheduler {

	private final ContainerMovementService movementService;

	@Scheduled(fixedRate = 3_600_000) // Каждый час
	public void moveContainers() {
		movementService.processContainerMovement();
	}
}