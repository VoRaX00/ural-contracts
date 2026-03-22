package ru.ural.contracts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ural.cargo.dto.CargoDto;
import ru.ural.cars.dto.CarDto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dto контракта")
public class ContractDto {

    @Schema(description = "Id контракта")
    private Long id;

    @Schema(description = "Id машины")
    private CarDto car;

    @Schema(description = "Id груза")
    private CargoDto cargo;

    @Schema(description = "Стоимость")
    private BigDecimal price;

    @Schema(description = "Дата создания")
    private ZonedDateTime createdAt;

    @Schema(description = "Дата обновления")
    private ZonedDateTime updatedAt;

}
