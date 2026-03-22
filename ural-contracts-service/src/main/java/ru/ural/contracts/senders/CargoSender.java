package ru.ural.contracts.senders;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.ural.cargo.dto.CargoDto;
import ru.ural.models.HttpResponse;
import ru.ural.contracts.properties.ContractIntegrationProperty;
import ru.ural.services.RestSender;
import ru.ural.utils.JwtUtils;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class CargoSender {

    private final RestSender restSender;

    private final ContractIntegrationProperty contractIntegrationProperty;

    private static final String PATTERN_GET_CAR_BY_ID = "%s/%s";

    public CargoDto getCargoById(Long id) {
        String baseUrl = String.format(
                PATTERN_GET_CAR_BY_ID,
                contractIntegrationProperty.getCargoUrl(),
                id
        );

        URI uri = RestSender.encodeUrl(baseUrl, null);
        HttpResponse<CargoDto> response = restSender.sendRequest(
                uri,
                HttpMethod.GET,
                new ParameterizedTypeReference<>() { },
                JwtUtils.getToken(JwtUtils.getAuthentication()).getTokenValue()
        );

        return response.body();
    }

}
