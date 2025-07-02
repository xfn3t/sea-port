package ru.sea.port.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sea.port.model.DepartureType;

@Repository
public interface DepartureTypeRepository extends JpaRepository<DepartureType, Integer> {

}
