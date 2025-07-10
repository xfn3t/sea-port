package ru.sea.port.model.container;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "storage_types")
@Getter
@Setter
public class StorageTypeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "storage_type_id")
	private Integer id;

	@Enumerated(EnumType.STRING)
	@Column(name = "storage_type_name", nullable = false, unique = true)
	private StorageType name;

	public String getNameAsString() {
		return name != null ? name.name() : null;
	}
}