package ru.sea.port.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sea.port.dto.PierStatusDTO;
import ru.sea.port.model.Pier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PierRepository extends JpaRepository<Pier, Long> {

	@Query("""
		SELECT p FROM Pier p 
		WHERE p.maxShipLength.lengthValue >= :len 
		AND p.occupied = false
		AND NOT EXISTS (
			SELECT 1 FROM PierBooking b 
			WHERE b.pier = p 
			AND b.endTime > :start 
			AND b.startTime < :end
		)
		ORDER BY p.maxShipLength.lengthValue ASC
	""")
	List<Pier> findFreePiers(
			@Param("len") int shipLength,
			@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end,
			Pageable pageable
	);

	@Query("SELECT p FROM Pier p WHERE p.ship.shipId = :shipId")
	Optional<Pier> findByShipId(@Param("shipId") Long shipId);

	@Query("""
        SELECT new ru.sea.port.dto.PierStatusDTO(
            p.id,
            p.occupied,
            COALESCE(s.shipId, null),
            COALESCE(s.shipNumber, null),
            p.maxShipLength.lengthValue,
            COALESCE(sl.lengthValue, null)
        )
        FROM Pier p
        LEFT JOIN p.ship s
        LEFT JOIN s.shipLength sl
    """)
	List<PierStatusDTO> findAllPiersStatus();


	List<Pier> findAllByOccupiedTrue();

	@Lock(LockModeType.OPTIMISTIC)
	@Query("SELECT p FROM Pier p WHERE p.id = :id")
	Optional<Pier> findByIdWithLock(Long id);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT p FROM Pier p WHERE p.id = :id")
	Optional<Pier> findByIdForUpdate(@Param("id") Long id);
}