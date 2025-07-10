package ru.sea.port.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sea.port.dto.request.ArrivalRequest;
import ru.sea.port.dto.request.ActualArrivalRequest;
import ru.sea.port.dto.request.DepartureRequest;
import ru.sea.port.dto.HistoryEntryDTO;
import ru.sea.port.dto.PierStatusDTO;
import ru.sea.port.dto.ShipQueueDTO;
import ru.sea.port.exception.ShipNotFoundException;
import ru.sea.port.model.*;
import ru.sea.port.repository.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PierAssignmentService {

	private final PierRepository pierRepo;
	private final ShipRepository shipRepo;
	private final ShipQueueRepository queueRepo;
	private final PierBookingRepository bookingRepo;
	private final ShipLengthRepository shipLengthRepository;
	private final ShipActualDateRepository shipActualDateRepo;

	@Retryable(
			value = ObjectOptimisticLockingFailureException.class,
			maxAttempts = 3,
			backoff = @Backoff(delay = 200)
	)
	@Transactional
	public Long handleArrival(ArrivalRequest req) {
		log.info("handleArrival ➔ {}", req);

		Ship ship;
		if (req.getShipId() == null) {
			ship = new Ship();
		} else {
			ship = shipRepo.findById(req.getShipId())
					.orElseThrow(() -> new ShipNotFoundException(req.getShipId()));
		}

		ship.setShipNumber(req.getShipNumber());
		ship.setScheduledArrivalDate(req.getArrival());
		ship.setScheduledDepartureDate(req.getDeparture());

		ShipLength sl = shipLengthRepository
				.findFirstByLengthValue(req.getShipLength())
				.orElseGet(() -> {
					ShipLength nl = new ShipLength();
					nl.setLengthValue(req.getShipLength());
					return shipLengthRepository.save(nl);
				});
		ship.setShipLength(sl);

		if (ship.getContainerCount() == null) {
			ship.setContainerCount(0);
		}

		ship = shipRepo.save(ship);

		ShipActualDate supply = ShipActualDate.builder()
				.ship(ship)
				.actualArrival(req.getArrival())
				.actualDeparture(req.getDeparture())
				.build();
		shipActualDateRepo.save(supply);

		queueRepo.upsertQueue(ship.getShipId(), req.getArrival());
		processQueue();
		return ship.getShipId();
	}

	@Retryable(value = ObjectOptimisticLockingFailureException.class)
	@Transactional
	public void handleActualArrival(ActualArrivalRequest req) {
		log.info("handleActualArrival ➔ shipId={}, actualArrival={}",
				req.getShipId(), req.getActualArrival());

		cancelExistingBookings(req.getShipId());
		cleanupExpiredBerths(req.getActualArrival());

		Ship ship = shipRepo.findById(req.getShipId())
				.orElseThrow(() -> new ShipNotFoundException(req.getShipId()));

		LocalDateTime actualArr = req.getActualArrival();

		ShipActualDate lastEvent = shipActualDateRepo
				.findFirstByShipShipIdOrderByIdDesc(ship.getShipId())
				.orElseThrow(() -> new ShipNotFoundException(req.getShipId()));

		lastEvent.setActualArrival(actualArr);
		shipActualDateRepo.save(lastEvent);

		processQueue();
		cleanupExpiredBerths(actualArr);

		log.info("handleActualArrival complete for shipId={}", req.getShipId());
	}

	@Retryable(
			value = ObjectOptimisticLockingFailureException.class,
			maxAttempts = 3,
			backoff = @Backoff(delay = 200)
	)
	@Transactional
	public void handleDeparture(DepartureRequest req) {
		log.info("handleDeparture ➔ shipId={}, departure={}",
				req.getShipId(), req.getDeparture());

		Ship ship = shipRepo.findById(req.getShipId())
				.orElseThrow(() -> new ShipNotFoundException(req.getShipId()));

		LocalDateTime actualDep = req.getDeparture();
		LocalDateTime scheduledDep = ship.getScheduledDepartureDate();
		Duration delay = Duration.between(scheduledDep, actualDep);

		ship.setScheduledDepartureDate(actualDep);
		shipRepo.save(ship);

		ShipActualDate lastEvent = shipActualDateRepo
				.findFirstByShipShipIdOrderByIdDesc(ship.getShipId())
				.orElseThrow(() -> new ShipNotFoundException(req.getShipId()));
		lastEvent.setActualDeparture(actualDep);
		shipActualDateRepo.save(lastEvent);

		pierRepo.findByShipId(ship.getShipId()).ifPresent(pier -> {
			bookingRepo.deleteByShip_ShipId(ship.getShipId());
			pier.setOccupied(false);
			pier.setShip(null);
			pierRepo.save(pier);

			bookingRepo.findFutureBookings(pier.getId(), actualDep)
					.forEach(b -> {
						b.setStartTime(b.getStartTime().plus(delay));
						b.setEndTime(b.getEndTime().plus(delay));
						bookingRepo.save(b);
					});
		});

		processQueue();
		cleanupExpiredBerths(actualDep);

		log.info("handleDeparture complete for shipId={}", req.getShipId());
	}


	@Transactional
	public void processQueue() {
		List<ShipQueueDTO> queue = queueRepo.findAllQueueEntries().stream()
				.sorted(Comparator.comparing(ShipQueueDTO::getArrivalTs))
				.toList();

		for (ShipQueueDTO sq : queue) {
			Optional<Pier> availablePier = findAvailablePierForShip(sq);
			availablePier.ifPresent(pier -> assignShipToPier(sq.getShipId(), pier, sq));
		}
	}

	private Optional<Pier> findAvailablePierForShip(ShipQueueDTO sq) {

		Ship ship = shipRepo.findByShipId(sq.getShipId())
				.orElse(null);

		if (ship == null) return Optional.empty();

		return pierRepo.findFreePiers(
				sq.getShipLength(),
				sq.getArrivalTs(),
				ship.getScheduledDepartureDate(),
				PageRequest.of(0, 1)
		).stream().findFirst();
	}

	private void assignShipToPier(Long shipId, Pier freePier, ShipQueueDTO sq) {
		// вместо обычного чтения — захватываем row-level lock
		Pier pier = pierRepo.findByIdForUpdate(freePier.getId())
				.orElseThrow(() -> new ShipNotFoundException(shipId));

		// теперь безопасно создаём бронирование и помечаем причал занятым
		Ship ship = shipRepo.findById(shipId)
				.orElseThrow(() -> new ShipNotFoundException(shipId));
		assign(
				ship,
				pier,
				sq.getArrivalTs(),
				ship.getScheduledDepartureDate()
		);
		queueRepo.remove(shipId);
	}


	private void assign(Ship ship, Pier pier, LocalDateTime start, LocalDateTime end) {
		bookingRepo.deleteByShip_ShipId(ship.getShipId());
		PierBooking b = PierBooking.builder()
				.pier(pier)
				.ship(ship)
				.startTime(start)
				.endTime(end)
				.build();

		bookingRepo.save(b);

		pier.setShip(ship);
		pier.setOccupied(true);
	}

	private void cancelExistingBookings(Long shipId) {
		bookingRepo.deleteByShip_ShipId(shipId);
		pierRepo.findByShipId(shipId).ifPresent(pier -> {
			pier.setShip(null);
			pier.setOccupied(false);
			pierRepo.save(pier);
		});
	}

	@Transactional(readOnly = true)
	public List<PierStatusDTO> getPiersStatusAt(LocalDateTime at) {
		// Получаем все причалы
		List<Pier> allPiers = pierRepo.findAll();

		return allPiers.stream().map(pier -> {
			// Проверяем, есть ли активное бронирование для этого причала
			Optional<PierBooking> activeBooking = bookingRepo.findActiveBookingForPier(pier.getId(), at);

			if (activeBooking.isPresent()) {
				// Причал занят
				PierBooking booking = activeBooking.get();
				Ship ship = booking.getShip();
				return new PierStatusDTO(
						pier.getId(),
						true,
						ship.getShipId(),
						ship.getShipNumber(),
						pier.getMaxShipLength().getLengthValue(),
						ship.getShipLength().getLengthValue()
				);
			} else {
				// Причал свободен
				return new PierStatusDTO(
						pier.getId(),
						false,
						null,
						null,
						pier.getMaxShipLength().getLengthValue(),
						null
				);
			}
		}).collect(Collectors.toList());
	}

	@Transactional
	public void cleanupExpiredBerths(LocalDateTime at) {
		List<PierBooking> expiredBookings = bookingRepo.findExpiredBookings(at);
		for (PierBooking booking : expiredBookings) {
			Pier pier = booking.getPier();
			Ship ship = booking.getShip();

			log.info("Freeing pier {} from ship {} at {}", pier.getId(), ship.getShipId(), at);

			// Освобождаем причал
			pier.setOccupied(false);
			pier.setShip(null);
			pierRepo.save(pier);

			// Удаляем бронирование
			bookingRepo.delete(booking);
		}
		processQueue();
	}


	@Transactional(readOnly = true)
	public List<PierStatusDTO> getPiersStatus() {
		return pierRepo.findAll().stream()
				.map(pier -> {
					Long shipId = null; String num = null; Integer curr = null;
					if (pier.getShip() != null) {
						shipId = pier.getShip().getShipId();
						num    = pier.getShip().getShipNumber();
						curr   = pier.getShip().getShipLength().getLengthValue();
					}
					return new PierStatusDTO(
							pier.getId(), pier.isOccupied(),
							shipId, num,
							pier.getMaxShipLength().getLengthValue(),
							curr
					);
				})
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<ShipQueueDTO> getQueueAt(LocalDateTime at) {
		return queueRepo.findAllQueueEntries().stream()
				.filter(q -> !q.getArrivalTs().isAfter(at))
				.sorted(Comparator.comparing(ShipQueueDTO::getArrivalTs))
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<HistoryEntryDTO> getHistory() {
		return bookingRepo.findAllByOrderByStartTimeAsc().stream()
				.map(pb -> new HistoryEntryDTO(
						pb.getShip().getShipId(),
						pb.getShip().getShipNumber(),
						pb.getPier().getId(),
						pb.getStartTime(),
						pb.getEndTime()
				))
				.collect(Collectors.toList());
	}
}
