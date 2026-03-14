package ru.ural.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ural.entities.Contract;

public interface ContractRepository extends JpaRepository<Contract, Long> {
}
