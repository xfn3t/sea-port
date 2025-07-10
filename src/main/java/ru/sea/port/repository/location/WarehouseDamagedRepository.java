package ru.sea.port.repository.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sea.port.model.location.warehouse.WarehouseDamaged;

@Repository
public interface WarehouseDamagedRepository extends JpaRepository<WarehouseDamaged, Long> {
	WarehouseDamaged findByContainerContainerId(Long containerId);
	void deleteByContainerContainerId(Long containerId);
}