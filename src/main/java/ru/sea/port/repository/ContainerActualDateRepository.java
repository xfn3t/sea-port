package ru.sea.port.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sea.port.model.ContainerActualDate;

import java.util.Optional;

@Repository
public interface ContainerActualDateRepository extends JpaRepository<ContainerActualDate, Long> {
    // Найти запись по контейнеру (1→1 связь)
    Optional<ContainerActualDate> findByContainerContainerId(Long containerId);
}
