package ru.sea.port.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sea.port.model.ShipLength;

import java.util.Optional;

@Repository
public interface ShipLengthRepository extends JpaRepository<ShipLength, Long> {
	Optional<ShipLength> findFirstByLengthValue(int lengthValue);
}
