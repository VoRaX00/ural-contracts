package ru.ural.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ural.enums.StatusContract;
import ural.ru.entities.BaseEntity;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contracts")
public class Contract extends BaseEntity {

    @Column(nullable = false)
    private Long carId;

    @Column(nullable = false)
    private Long cargoId;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private StatusContract status;

    @Column
    private BigDecimal price;

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    @Column
    private ZonedDateTime updatedAt;

}
