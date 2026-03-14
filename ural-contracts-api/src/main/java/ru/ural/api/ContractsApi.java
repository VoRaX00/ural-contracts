package ru.ural.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.ural.dto.ContractDto;

@RequestMapping("/api/contracts")
@Tag(name = "Контроллер для работы с контрактами")
public interface ContractsApi {

    @GetMapping("/{id}")
    ResponseEntity<ContractDto> getById(@PathVariable Long id);

}
