package ru.sea.port.model.pier;

import jakarta.persistence.*;
import lombok.*;
import ru.sea.port.model.ship.Ship;

import java.time.LocalDateTime;

@Entity
@Table(name = "pier_bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PierBooking {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "booking_id")
	private Long bookingId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pier_id", nullable = false)
	private Pier pier;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ship_id", nullable = false)
	private Ship ship;

	@Column(name = "start_time", nullable = false)
	private LocalDateTime startTime;

	@Column(name = "end_time", nullable = false)
	private LocalDateTime endTime;
}
