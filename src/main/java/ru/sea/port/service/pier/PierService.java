package ru.sea.port.service.pier;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sea.port.model.pier.Pier;
import ru.sea.port.repository.pier.PierRepository;

@Service
@RequiredArgsConstructor
public class PierService {

	private final PierRepository pierRepo;


	public void deleteAll() {
		pierRepo.deleteAll();
	}

	public Pier save(Pier pier) {
		return pierRepo.save(pier);
	}
}
