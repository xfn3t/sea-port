package ru.sea.port.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sea.port.model.Ship;
import ru.sea.port.repository.ShipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipService {

    private final ShipRepository shipRepo;

    @Transactional(readOnly = true)
    public List<Ship> getAllShips() {
        return shipRepo.findAll();
    }

	public void deleteAll() {
		shipRepo.deleteAll();
	}
}
