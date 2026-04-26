package ru.ural.contracts.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.ural.contracts.dto.ContractDto;
import ru.ural.contracts.dto.ContractRequest;
import ru.ural.contracts.entities.Contract;
import ru.ural.contracts.models.ContractModel;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ContractMapper {

    ContractDto toDto(ContractModel contract);

    ContractDto toDto(Contract contract);

    List<ContractDto> toDto(List<Contract> contract);

    @Mapping(target = "ownerUuid", ignore = true)
    @Mapping(target = "relatedUserUuid", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    Contract toEntity(ContractRequest contractRequest);

    @Mapping(target = "car", ignore = true)
    @Mapping(target = "cargo", ignore = true)
    @Mapping(target = "status", expression = "java(contract.getStatus().name())")
    ContractModel toModel(Contract contract);

}
