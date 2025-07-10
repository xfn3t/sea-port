package ru.sea.port.repository.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sea.port.model.location.departure.DepartureTruck;

@Repository
public interface DepartureTruckRepository extends JpaRepository<DepartureTruck, Long> {
	DepartureTruck findByContainerContainerId(Long containerId);
	void deleteByContainerContainerId(Long containerId);
}