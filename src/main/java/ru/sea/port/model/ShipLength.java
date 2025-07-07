package ru.sea.port.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ship_lengths")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ShipLength {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ship_length_id")
	private Long id;

	/**
	 * Значение длины судна (например, 150, 200 или 250)
	 */
	@Column(name = "length_value", nullable = false)
	private Integer lengthValue;
}