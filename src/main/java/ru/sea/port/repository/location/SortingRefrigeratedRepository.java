package ru.sea.port.repository.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sea.port.model.location.sorting.SortingRefrigerated;

@Repository
public interface SortingRefrigeratedRepository extends JpaRepository<SortingRefrigerated, Long> {
	SortingRefrigerated findByContainerContainerId(Long containerId);
	void deleteByContainerContainerId(Long containerId);
}