package ru.ural.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Long carId;

    @Schema(description = "Id груза")
    private Long cargoId;

    @Schema(description = "Стоимость")
    private BigDecimal price;

    @Schema(description = "Дата создания")
    private ZonedDateTime createdAt;

    @Schema(description = "Дата обновления")
    private ZonedDateTime updatedAt;

}
