package ru.sea.port.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sea.port.model.FixedSupply;

@Repository
public interface FixedSupplyRepository extends JpaRepository<FixedSupply, Long> {

}
