package ru.sea.port.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sea.port.model.Container;

import java.util.Optional;

@Repository
public interface ContainerRepository extends JpaRepository<Container, Long> {
	@Query("SELECT c FROM Container c " +
			"LEFT JOIN FETCH c.storageType " +
			"LEFT JOIN FETCH c.departureType " +
			"LEFT JOIN FETCH c.actualDate " +
			"WHERE c.containerId = :id")
	Optional<Container> findByIdWithRelations(@Param("id") Long id);
}
