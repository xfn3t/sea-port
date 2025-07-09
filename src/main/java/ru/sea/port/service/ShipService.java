package ru.sea.port.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sea.port.model.Ship;
import ru.sea.port.repository.ShipRepository;

import java.util.List;

@Service
public class ShipService {
    private final ShipRepository shipRepo;

    public ShipService(ShipRepository shipRepo) {
        this.shipRepo = shipRepo;
    }

    @Transactional(readOnly = true)
    public List<Ship> getAllShips() {
        return shipRepo.findAll();
    }
}
