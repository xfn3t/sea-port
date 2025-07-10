package ru.sea.port.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import ru.sea.port.model.location.Location;
import ru.sea.port.model.location.departure.DepartureRail;
import ru.sea.port.model.location.departure.DepartureShip;
import ru.sea.port.model.location.departure.DepartureTruck;
import ru.sea.port.model.location.sorting.SortingRefrigerated;
import ru.sea.port.model.location.sorting.SortingRegular;
import ru.sea.port.model.location.warehouse.WarehouseDamaged;
import ru.sea.port.model.location.warehouse.WarehouseRefrigerated;
import ru.sea.port.model.location.warehouse.WarehouseRegular;

import java.time.LocalDateTime;

@Entity
@Table(name = "containers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Container {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "container_id")
    private Long containerId;

    @Column(name = "damage_status", nullable = false)
    private Boolean damageStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_type_id", nullable = false)
    @JsonIgnore
    private StorageTypeEntity storageType;

    @Column(name = "scheduled_arrival_date", nullable = false)
    private LocalDateTime scheduledArrivalDate;

    @Column(name = "scheduled_departure_date", nullable = false)
    private LocalDateTime scheduledDepartureDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_type_id", nullable = false)
    @JsonIgnore
    private DepartureTypeEntity departureType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ship_id", nullable = false)
    @JsonIgnore
    private Ship ship;

    @OneToOne(mappedBy = "container", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private ContainerActualDate actualDate;

    // Связи с конкретными местоположениями
    @OneToOne(mappedBy = "container", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private SortingRegular sortingRegular;

    @OneToOne(mappedBy = "container", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private SortingRefrigerated sortingRefrigerated;

    @OneToOne(mappedBy = "container", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private WarehouseRegular warehouseRegular;

    @OneToOne(mappedBy = "container", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private WarehouseRefrigerated warehouseRefrigerated;

    @OneToOne(mappedBy = "container", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private WarehouseDamaged warehouseDamaged;

    @OneToOne(mappedBy = "container", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private DepartureRail departureRail;

    @OneToOne(mappedBy = "container", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private DepartureTruck departureTruck;

    @OneToOne(mappedBy = "container", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private DepartureShip departureShip;

    // Метод для получения текущего местоположения
    public Location getCurrentLocation() {
        if (sortingRegular != null) return sortingRegular;
        if (sortingRefrigerated != null) return sortingRefrigerated;
        if (warehouseRegular != null) return warehouseRegular;
        if (warehouseRefrigerated != null) return warehouseRefrigerated;
        if (warehouseDamaged != null) return warehouseDamaged;
        if (departureRail != null) return departureRail;
        if (departureTruck != null) return departureTruck;
        if (departureShip != null) return departureShip;
        return null;
    }

    // Вспомогательный метод для установки местоположения
    public void setCurrentLocation(Location location) {
        clearAllLocations();

        if (location instanceof SortingRegular) {
            this.sortingRegular = (SortingRegular) location;
            this.sortingRegular.setContainer(this);
        } else if (location instanceof SortingRefrigerated) {
            this.sortingRefrigerated = (SortingRefrigerated) location;
            this.sortingRefrigerated.setContainer(this);
        } else if (location instanceof WarehouseRegular) {
            this.warehouseRegular = (WarehouseRegular) location;
            this.warehouseRegular.setContainer(this);
        } else if (location instanceof WarehouseRefrigerated) {
            this.warehouseRefrigerated = (WarehouseRefrigerated) location;
            this.warehouseRefrigerated.setContainer(this);
        } else if (location instanceof WarehouseDamaged) {
            this.warehouseDamaged = (WarehouseDamaged) location;
            this.warehouseDamaged.setContainer(this);
        } else if (location instanceof DepartureRail) {
            this.departureRail = (DepartureRail) location;
            this.departureRail.setContainer(this);
        } else if (location instanceof DepartureTruck) {
            this.departureTruck = (DepartureTruck) location;
            this.departureTruck.setContainer(this);
        } else if (location instanceof DepartureShip) {
            this.departureShip = (DepartureShip) location;
            this.departureShip.setContainer(this);
        }
    }

    private void clearAllLocations() {
        sortingRegular = null;
        sortingRefrigerated = null;
        warehouseRegular = null;
        warehouseRefrigerated = null;
        warehouseDamaged = null;
        departureRail = null;
        departureTruck = null;
        departureShip = null;
    }
}