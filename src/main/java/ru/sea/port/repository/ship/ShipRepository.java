package ru.sea.port.repository.ship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sea.port.model.ship.Ship;

import java.util.Optional;

@Repository
public interface ShipRepository extends JpaRepository<Ship, Long> {

	Optional<Ship> findByShipId(Long shipId);
}
