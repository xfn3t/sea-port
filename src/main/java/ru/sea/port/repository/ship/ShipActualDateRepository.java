package ru.sea.port.repository.ship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sea.port.model.ship.ShipActualDate;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipActualDateRepository extends JpaRepository<ShipActualDate, Long> {
    // Получить все события (arrival/departure) для конкретного судна
    List<ShipActualDate> findByShipShipId(Long shipId);

    // Получить последнее по времени событие для конкретного судна
    Optional<ShipActualDate> findFirstByShipShipIdOrderByIdDesc(Long shipId);
}
