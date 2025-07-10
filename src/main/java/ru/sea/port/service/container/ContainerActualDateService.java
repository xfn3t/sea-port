package ru.sea.port.service.container;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sea.port.repository.container.ContainerActualDateRepository;

@Service
@RequiredArgsConstructor
public class ContainerActualDateService {

	private final ContainerActualDateRepository containerActualDateRepository;

	public void deleteAll() {
		containerActualDateRepository.deleteAll();
	}

}
