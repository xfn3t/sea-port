package ru.sea.port.repository.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sea.port.model.location.warehouse.WarehouseRegular;

@Repository
public interface WarehouseRegularRepository extends JpaRepository<WarehouseRegular, Long> {
	WarehouseRegular findByContainerContainerId(Long containerId);
	void deleteByContainerContainerId(Long containerId);
}