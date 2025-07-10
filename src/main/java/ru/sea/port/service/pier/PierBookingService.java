package ru.sea.port.service.pier;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sea.port.repository.pier.PierBookingRepository;

@Service
@RequiredArgsConstructor
public class PierBookingService {

	private final PierBookingRepository bookingRepo;

	public void deleteAll() {
		bookingRepo.deleteAll();
	}
}
