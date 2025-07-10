package ru.sea.port.repository.ship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.sea.port.dto.ShipQueueDTO;
import ru.sea.port.model.ship.ShipQueueEntry;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShipQueueRepository extends JpaRepository<ShipQueueEntry, Long> {

	@Modifying
	@Transactional
	@Query(value = """
            INSERT INTO ship_queue (ship_id, arrival_ts)
            VALUES (:shipId, :arrival)
            ON CONFLICT (ship_id) DO UPDATE
              SET arrival_ts = EXCLUDED.arrival_ts
            """, nativeQuery = true)
	void upsertQueue(
			@Param("shipId") Long shipId,
			@Param("arrival") LocalDateTime arrival
	);

	@Query(value = """
        SELECT sq.ship_id
          FROM ship_queue sq
          JOIN ships s ON s.ship_id = sq.ship_id
          JOIN ship_lengths sl ON sl.ship_length_id = s.ship_length_id
         WHERE sl.length_value <= :pierMax
         ORDER BY sq.arrival_ts
         LIMIT 1
        """, nativeQuery = true)
	Optional<Long> findNextShip(Long pierMax);

	@Modifying
	@Query("DELETE FROM ShipQueueEntry sq WHERE sq.shipId = :shipId")
	void remove(Long shipId);

	@Query("SELECT new ru.sea.port.dto.ShipQueueDTO(" +
			"sq.shipId, s.shipNumber, sq.arrivalTs, sl.lengthValue) " +
			"FROM ShipQueueEntry sq " +
			"JOIN Ship s ON sq.shipId = s.shipId " +
			"JOIN s.shipLength sl")
	List<ShipQueueDTO> findAllQueueEntries();

	@Query("""
        SELECT new ru.sea.port.dto.ShipQueueDTO(
            sq.shipId, s.shipNumber, sq.arrivalTs, sl.lengthValue
        )
        FROM ShipQueueEntry sq
        JOIN Ship s ON sq.shipId = s.shipId
        JOIN s.shipLength sl
        WHERE sq.arrivalTs <= :now
        AND NOT EXISTS (
            SELECT 1 FROM Pier p 
            WHERE p.ship.shipId = sq.shipId
        )
        ORDER BY sq.arrivalTs
    """)
	List<ShipQueueDTO> findActiveQueueEntries(@Param("now") LocalDateTime now);

}