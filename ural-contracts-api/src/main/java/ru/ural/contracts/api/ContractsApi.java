package ru.ural.contracts.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ural.contracts.dto.ContractDto;
import ru.ural.contracts.dto.ContractRequest;
import ru.ural.dto.PageDto;
import ru.ural.dto.PaginatedParamsDto;

@RequestMapping("/api/contracts")
@Tag(name = "Контроллер для работы с контрактами")
public interface ContractsApi {

    @Operation(summary = "Получить контракт по id")
    @GetMapping("/{id}")
    ResponseEntity<ContractDto> getById(@PathVariable Long id);

    @Operation(summary = "Создать контракт")
    @PostMapping
    ResponseEntity<ContractDto> create(@RequestBody ContractRequest request);

    @Operation(summary = "Изменить контракт")
    @PutMapping("/{id}")
    ResponseEntity<ContractDto> edit(@PathVariable Long id, @RequestBody ContractRequest request);

    @Operation(summary = "Получить список контрактов")
    @GetMapping
    ResponseEntity<PageDto<ContractDto>> getPage(PaginatedParamsDto paginatedParamsDto);

    @PatchMapping("/{id}/status")
    ResponseEntity<ContractDto> changeStatus(@PathVariable Long id, @RequestParam(required = false) Boolean isClose);

}
