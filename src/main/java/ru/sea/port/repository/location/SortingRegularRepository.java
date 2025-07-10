package ru.sea.port.repository.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sea.port.model.location.sorting.SortingRegular;

@Repository
public interface SortingRegularRepository extends JpaRepository<SortingRegular, Long> {
	SortingRegular findByContainerContainerId(Long containerId);
	void deleteByContainerContainerId(Long containerId);
}