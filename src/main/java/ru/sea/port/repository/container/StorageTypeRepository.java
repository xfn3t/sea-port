package ru.sea.port.repository.container;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sea.port.model.container.StorageType;
import ru.sea.port.model.container.StorageTypeEntity;

import java.util.Optional;

@Repository
public interface StorageTypeRepository extends JpaRepository<StorageTypeEntity, Integer> {
	@Query("SELECT s FROM StorageTypeEntity s WHERE s.name = :name")
	Optional<StorageTypeEntity> findByName(@Param("name") StorageType name);

	@Query(value = "SELECT * FROM storage_types WHERE storage_type_name = :name", nativeQuery = true)
	Optional<StorageTypeEntity> findByStringName(@Param("name") String name);
}
