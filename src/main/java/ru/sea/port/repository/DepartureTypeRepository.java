package ru.sea.port.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sea.port.model.DepartureType;
import ru.sea.port.model.DepartureTypeEntity;

import java.util.Optional;

@Repository
public interface DepartureTypeRepository extends JpaRepository<DepartureTypeEntity, Integer> {
	@Query("SELECT d FROM DepartureTypeEntity d WHERE d.name = :name")
	Optional<DepartureTypeEntity> findByName(@Param("name") DepartureType name);

	@Query(value = "SELECT * FROM departure_types WHERE departure_type_name = :name", nativeQuery = true)
	Optional<DepartureTypeEntity> findByStringName(@Param("name") String name);
}