package ru.sea.port.service.ship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sea.port.model.ship.Ship;
import ru.sea.port.repository.ship.ShipRepository;

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
