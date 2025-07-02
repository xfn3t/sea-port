package ru.sea.port.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sea.port.model.Ship;

@Repository
public interface ShipRepository extends JpaRepository<Ship, Long> {

}
