package ru.ural.contracts.services;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ural.cargo.dto.CargoDto;
import ru.ural.cars.dto.CarDto;
import ru.ural.contracts.entities.Contract;
import ru.ural.contracts.enums.ContractStatus;
import ru.ural.contracts.senders.CargoSender;
import ru.ural.contracts.senders.CarsSender;
import ru.ural.exceptions.NotFoundException;
import ru.ural.contracts.mappers.ContractMapper;
import ru.ural.contracts.models.ContractModel;
import ru.ural.models.UserPrincipals;
import ru.ural.contracts.repositories.ContractRepository;
import ru.ural.utils.JwtUtils;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final CarsSender carsSender;

    private final CargoSender cargoSender;

    private final ContractRepository contractRepository;

    private final ContractMapper contractMapper;

    @NonNull
    public ContractModel getContractById(@NonNull Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Not found contract by id: %d", id
                )));

        ContractModel model = contractMapper.toModel(contract);
        CarDto carDto = carsSender.getCarById(contract.getCarId());
        CargoDto cargoDto = cargoSender.getCargoById(contract.getCargoId());

        model.setCar(carDto);
        model.setCargo(cargoDto);
        return model;
    }

    @Transactional
    public ContractModel create(@NonNull Contract contract) {
        String ownerUuid = getUserUuid();

        contract.setCreatedAt(ZonedDateTime.now());
        contract.setStatus(ContractStatus.AGREEMENT);
        contract.setOwnerUuid(ownerUuid);

        CarDto carDto = carsSender.getCarById(contract.getCarId());
        CargoDto cargoDto = cargoSender.getCargoById(contract.getCargoId());

        String relatedUserUuid = ownerUuid.equals(carDto.getUserUuid())
                ? cargoDto.getUserUuid()
                : carDto.getUserUuid();

        contract.setRelatedUserUuid(relatedUserUuid);

        Contract savedDb = contractRepository.save(contract);

        ContractModel model = contractMapper.toModel(savedDb);
        model.setCar(carDto);
        model.setCargo(cargoDto);

        return model;
    }

    private String getUserUuid() {
        Authentication authentication = JwtUtils.getAuthentication();
        UserPrincipals userPrincipals = JwtUtils.getUser(authentication);
        return userPrincipals.getUuid();
    }

}
