package ru.ural.contracts.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ural.contracts.dto.ContractDto;
import ru.ural.contracts.dto.ContractRequest;

@RequestMapping("/api/contracts")
@Tag(name = "Контроллер для работы с контрактами")
public interface ContractsApi {

    @GetMapping("/{id}")
    ResponseEntity<ContractDto> getById(@PathVariable Long id);

    @PostMapping
    ResponseEntity<ContractDto> create(@RequestBody ContractRequest request);

}
