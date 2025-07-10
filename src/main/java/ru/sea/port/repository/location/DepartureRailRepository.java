package ru.sea.port.repository.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sea.port.model.location.departure.DepartureRail;

@Repository
public interface DepartureRailRepository extends JpaRepository<DepartureRail, Long> {
	DepartureRail findByContainerContainerId(Long containerId);
	void deleteByContainerContainerId(Long containerId);
}