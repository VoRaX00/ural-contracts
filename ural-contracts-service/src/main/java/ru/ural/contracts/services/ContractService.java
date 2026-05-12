package ru.ural.contracts.services;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ru.ural.cargo.dto.CargoDto;
import ru.ural.cars.dto.CarDto;
import ru.ural.contracts.dto.ContractDto;
import ru.ural.contracts.entities.Contract;
import ru.ural.contracts.enums.ContractStatus;
import ru.ural.contracts.mappers.PaginatedMapper;
import ru.ural.contracts.repositories.CustomContractRepository;
import ru.ural.contracts.senders.CargoSender;
import ru.ural.contracts.senders.CarsSender;
import ru.ural.contracts.senders.NotificationSender;
import ru.ural.dto.PageDto;
import ru.ural.dto.PaginatedParamsDto;
import ru.ural.exceptions.NotFoundException;
import ru.ural.contracts.mappers.ContractMapper;
import ru.ural.contracts.models.ContractModel;
import ru.ural.models.UserPrincipals;
import ru.ural.contracts.repositories.ContractRepository;
import ru.ural.notifications.dto.contract.NotificationContractRequest;
import ru.ural.utils.JwtUtils;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ContractService {

    private static final String NOTIFICATION_TITLE_AFTER_UPDATE_STATUS = "Изменен статус контракта";

    private static final String NOTIFICATION_BODY_AFTER_UPDATE_STATUS = "Статус контракта изменен на %s";

    private final CarsSender carsSender;

    private final CargoSender cargoSender;

    private final ContractRepository contractRepository;

    private final CustomContractRepository customContractRepository;

    private final ContractMapper contractMapper;

    private final PaginatedMapper paginatedMapper;

    private final NotificationSender notificationSender;

    private final TransactionTemplate transactionTemplate;

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

    public PageDto<ContractDto> getPageDto(PaginatedParamsDto paginatedParamsDto) {
        var paramsModel = paginatedMapper.toModel(paginatedParamsDto);
        var items = customContractRepository.getItems(paramsModel);
        int totalResultCount = customContractRepository.getTotalResultCount(paramsModel.getFilters());
        int totalPageCount = totalResultCount % paramsModel.getItemsOnPage() == 0
                ? totalResultCount / paramsModel.getItemsOnPage()
                : totalResultCount / paramsModel.getItemsOnPage() + 1;

        var pageDto = new PageDto<>(
                paramsModel.getCurrentPageNumber(), totalPageCount,
                totalResultCount,
                contractMapper.toDto(items),
                paramsModel.getItemsOnPage()
        );

        pageDto.getItems().forEach(item -> {
            var cargoId = item.getCargo().getId();
            var carId = item.getCar().getId();

            item.setCargo(cargoSender.getCargoById(cargoId));
            item.setCar(carsSender.getCarById(carId));
        });

        return pageDto;
    }

    public ContractDto changeStatus(Long id, Boolean isClose) {
        String currentUserUuid = getUserUuid();
        Contract contract = Objects.requireNonNull(transactionTemplate.execute(status -> updateContractStatus(id, isClose)));

        var cargo = cargoSender.getCargoById(contract.getCargoId());
        var car = carsSender.getCarById(contract.getCarId());
        String notificationReceiverUuid = getNotificationReceiverUuid(contract, currentUserUuid);

        notificationSender.sendContractNotification(buildStatusChangedNotification(
                contract.getId(), contract.getStatus(), notificationReceiverUuid
        ));

        return buildContractDto(contract, cargo, car);
    }

    private Contract updateContractStatus(Long id, Boolean isClose) {
        Contract contract = getContractEntityById(id);
        ContractStatus newStatus = resolveNewStatus(contract.getStatus(), isClose);
        contract.setStatus(newStatus);
        return contract;
    }

    private Contract getContractEntityById(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Контракт с id %d не найден".formatted(id)));
    }

    private ContractStatus resolveNewStatus(ContractStatus currentStatus, Boolean isClose) {
        return Boolean.TRUE.equals(isClose)
                ? ContractStatus.CLOSED
                : getNextStatus(currentStatus);
    }

    private NotificationContractRequest buildStatusChangedNotification(
            Long contractId,
            ContractStatus newStatus,
            String receiverUuid
    ) {
        return NotificationContractRequest.builder()
                .title(NOTIFICATION_TITLE_AFTER_UPDATE_STATUS)
                .body(NOTIFICATION_BODY_AFTER_UPDATE_STATUS.formatted(newStatus.getValue()))
                .contractId(contractId)
                .userUuids(List.of(receiverUuid))
                .build();
    }

    private String getNotificationReceiverUuid(Contract contract, String currentUserUuid) {
        return Objects.equals(contract.getOwnerUuid(), currentUserUuid)
                ? contract.getRelatedUserUuid()
                : contract.getOwnerUuid();
    }

    private ContractDto buildContractDto(Contract contract, CargoDto cargo, CarDto car) {
        ContractDto dto = contractMapper.toDto(contract);
        dto.setCargo(cargo);
        dto.setCar(car);

        return dto;
    }

    private String getUserUuid() {
        Authentication authentication = JwtUtils.getAuthentication();
        UserPrincipals userPrincipals = JwtUtils.getUser(authentication);
        return userPrincipals.getUuid();
    }

    private ContractStatus getNextStatus(ContractStatus current) {
        if (current == ContractStatus.FINISHED || current == ContractStatus.CLOSED) {
            return current;
        }

        int index = current.ordinal() + 1;
        return ContractStatus.values()[index];
    }

}
