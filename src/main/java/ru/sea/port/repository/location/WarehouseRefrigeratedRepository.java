package ru.sea.port.repository.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sea.port.model.location.warehouse.WarehouseRefrigerated;

@Repository
public interface WarehouseRefrigeratedRepository extends JpaRepository<WarehouseRefrigerated, Long> {
	WarehouseRefrigerated findByContainerContainerId(Long containerId);
	void deleteByContainerContainerId(Long containerId);
}