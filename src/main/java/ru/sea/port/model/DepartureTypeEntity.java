package ru.sea.port.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "departure_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartureTypeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "departure_type_id")
	private Integer id;

	@Enumerated(EnumType.STRING)
	@Column(name = "departure_type_name", nullable = false, unique = true)
	private DepartureType name;
}