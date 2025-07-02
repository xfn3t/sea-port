package ru.sea.port.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sea.port.model.Container;

@Repository
public interface ContainerRepository extends JpaRepository<Container, Long> {

}
