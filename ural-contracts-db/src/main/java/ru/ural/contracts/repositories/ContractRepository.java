package ru.ural.contracts.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ural.contracts.entities.Contract;

public interface ContractRepository extends JpaRepository<Contract, Long> {
}
