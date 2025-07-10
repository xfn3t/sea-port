package ru.sea.port.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sea.port.model.PierBooking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PierBookingRepository extends JpaRepository<PierBooking, Long> {

	@Query("""
      SELECT b FROM PierBooking b
       WHERE b.ship.shipId = :shipId
         AND b.startTime <= CURRENT_TIMESTAMP
         AND b.endTime   >  CURRENT_TIMESTAMP
    """)
	Optional<PierBooking> findActiveBookingByShipId(Long shipId);

	@Query("""
		SELECT b FROM PierBooking b
		WHERE b.pier.id = :pierId
		AND (
			(b.startTime BETWEEN :start AND :end)
			OR (b.endTime BETWEEN :start AND :end)
			OR (:start BETWEEN b.startTime AND b.endTime)
			OR (:end BETWEEN b.startTime AND b.endTime)
		)
	""")
	List<PierBooking> findOverlappingBookings(Long pierId, LocalDateTime start, LocalDateTime end);

	@Query("""
        SELECT b FROM PierBooking b
        WHERE b.pier.id = :pierId
        AND b.startTime >= :fromTime
    """)
	List<PierBooking> findFutureBookings(Long pierId, LocalDateTime fromTime);

	void deleteByShip_ShipId(Long shipId);

	@Query("SELECT b FROM PierBooking b WHERE b.ship.shipId = :shipId")
	List<PierBooking> findByShip_ShipId(@Param("shipId") Long shipId);

	List<PierBooking> findFutureBookingsByPier_IdAndStartTimeGreaterThanEqualOrderByStartTime(
			Long pierId, LocalDateTime from);

	/**
	 * Возвращает всю историю бронирований (даже уже освобождённых),
	 * упорядоченную по времени старта.
	 */
	List<PierBooking> findAllByOrderByStartTimeAsc();

	@Query("SELECT b FROM PierBooking b WHERE b.startTime <= :at AND b.endTime > :at")
	List<PierBooking> findActiveBookings(@Param("at") LocalDateTime at);

	// Истекшие бронирования
	@Query("SELECT b FROM PierBooking b WHERE b.endTime <= :at")
	List<PierBooking> findExpiredBookings(@Param("at") LocalDateTime at);

	@Query("SELECT b FROM PierBooking b WHERE b.pier.id = :pierId AND b.startTime <= :at AND b.endTime > :at")
	Optional<PierBooking> findActiveBookingForPier(@Param("pierId") Long pierId, @Param("at") LocalDateTime at);
}
