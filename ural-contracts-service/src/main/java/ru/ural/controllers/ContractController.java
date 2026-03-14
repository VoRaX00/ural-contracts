package ru.ural.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.ural.api.ContractsApi;
import ru.ural.dto.ContractDto;
import ru.ural.mappers.ContractMapper;
import ru.ural.services.ContractService;

@RestController
@RequiredArgsConstructor
public class ContractController implements ContractsApi {

    private final ContractService contractService;

    private final ContractMapper contractMapper;

    @Override
    public ResponseEntity<ContractDto> getById(Long id) {
        var contract = contractService.getContractById(id);
        return ResponseEntity.ok(contractMapper.toDto(contract));
    }
}
