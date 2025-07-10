package ru.sea.port.repository.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sea.port.model.location.departure.DepartureShip;

@Repository
public interface DepartureShipRepository extends JpaRepository<DepartureShip, Long> {
	DepartureShip findByContainerContainerId(Long containerId);
	void deleteByContainerContainerId(Long containerId);
}