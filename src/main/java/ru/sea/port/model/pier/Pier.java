package ru.sea.port.model.pier;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import ru.sea.port.model.ship.Ship;
import ru.sea.port.model.ship.ShipLength;


@Entity
@Table(name = "piers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Pier {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pier_id")
	private Long id;

	@Column(name = "occupied", nullable = false)
	private boolean occupied = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ship_id", foreignKey = @ForeignKey(name = "fk_piers_ships"))
	@JsonBackReference("ship-piers")
	private Ship ship;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "max_ship_length_id", foreignKey = @ForeignKey(name = "fk_piers_max_length"))
	private ShipLength maxShipLength;

	public void setShip(Ship ship) {
		this.ship = ship;
		this.occupied = (ship != null);
	}

	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
		if (!occupied) {
			this.ship = null;
		}
	}
}