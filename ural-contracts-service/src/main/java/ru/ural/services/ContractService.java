package ru.ural.services;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import ru.ural.entities.Contract;
import ru.ural.repositories.ContractRepository;
import ural.ru.exceptions.NotFoundException;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;

    @NonNull
    public Contract getContractById(@NonNull Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Not found contract by id: %d", id
                )));
    }

}
