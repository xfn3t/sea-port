package ru.sea.port.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sea.port.repository.ContainerActualDateRepository;

@Service
@RequiredArgsConstructor
public class ContainerActualDateService {

	private final ContainerActualDateRepository containerActualDateRepository;

	public void deleteAll() {
		containerActualDateRepository.deleteAll();
	}

}
