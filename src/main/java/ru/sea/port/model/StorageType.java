package ru.sea.port.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "storage_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class StorageType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storage_type_id")
    private Integer storageTypeId;

    @Column(name = "storage_type_name", nullable = false, length = 100)
    private String storageTypeName;

    @JsonIgnore
    @OneToMany(mappedBy = "storageType", cascade = CascadeType.ALL)
    private List<Container> containers;
}
