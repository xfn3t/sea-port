package ru.sea.port.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sea.port.model.StorageType;

@Repository
public interface StorageTypeRepository extends JpaRepository<StorageType, Integer> {

}
