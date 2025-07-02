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
public class DepartureType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "departure_type_id")
    private Integer departureTypeId;

    @Column(name = "departure_type_name", nullable = false, length = 100)
    private String departureTypeName;

    @OneToMany(mappedBy = "departureType", cascade = CascadeType.ALL)
    private List<Container> containers;
}
