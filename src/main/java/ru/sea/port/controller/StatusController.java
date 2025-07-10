package ru.sea.port.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.sea.port.dto.HistoryEntryDTO;
import ru.sea.port.dto.PierStatusDTO;
import ru.sea.port.dto.ShipQueueDTO;
import ru.sea.port.service.pier.PierAssignmentService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('TERMINAL_OPERATOR','TALLYMAN')")
public class StatusController {

	private final PierAssignmentService service;

	@GetMapping("/piers")
	public ResponseEntity<List<PierStatusDTO>> getPiers(
			@RequestParam(value = "at", required = false) String atParam
	) {
		LocalDateTime at = parseDateTime(atParam);
		return ResponseEntity.ok(service.getPiersStatusAt(at));
	}

	@GetMapping("/queue")
	public ResponseEntity<List<ShipQueueDTO>> getQueue(
			@RequestParam(value = "at", required = false) String atParam
	) {
		LocalDateTime at = parseDateTime(atParam);
		return ResponseEntity.ok(service.getQueueAt(at));
	}

	@GetMapping("/history")
	public ResponseEntity<List<HistoryEntryDTO>> getHistory() {
		return ResponseEntity.ok(service.getHistory());
	}

	private LocalDateTime parseDateTime(String datetime) {
		if (datetime == null || datetime.isEmpty()) {
			return LocalDateTime.now();
		}

		// Удаляем возможные кавычки и пробелы
		datetime = datetime.replace("\"", "").trim();

		// Если время указано без даты, добавляем текущую дату
		if (datetime.contains("T") && datetime.indexOf("T") + 1 == datetime.length()) {
			datetime = datetime.substring(0, datetime.indexOf("T"));
		}

		try {
			// Пробуем парсить полный формат
			return LocalDateTime.parse(datetime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		} catch (DateTimeParseException e1) {
			try {
				// Пробуем парсить без наносекунд
				return LocalDateTime.parse(datetime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
			} catch (DateTimeParseException e2) {
				try {
					// Пробуем парсить только время
					String datePart = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
					return LocalDateTime.parse(datePart + "T" + datetime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
				} catch (DateTimeParseException e3) {
					return LocalDateTime.now();
				}
			}
		}
	}
}