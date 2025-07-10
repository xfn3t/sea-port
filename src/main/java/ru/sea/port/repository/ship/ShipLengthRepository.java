package ru.sea.port.repository.ship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sea.port.model.ship.ShipLength;

import java.util.Optional;

@Repository
public interface ShipLengthRepository extends JpaRepository<ShipLength, Long> {
	Optional<ShipLength> findFirstByLengthValue(int lengthValue);
}
