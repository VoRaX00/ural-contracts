package ru.ural.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.ural.dto.ContractDto;
import ru.ural.entities.Contract;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ContractMapper {

    ContractDto toDto(Contract contract);

}
