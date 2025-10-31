package pe.edu.vallegrande.VaccineAplication.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import pe.edu.vallegrande.VaccineAplication.dto.CycleLifeDTO;
import pe.edu.vallegrande.VaccineAplication.webclient.client.CycleLifeClient;

import java.time.LocalDate;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("rawtypes")
@ExtendWith(MockitoExtension.class)
public class CycleLifeClientMockitoTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private CycleLifeClient cycleLifeClient;

   @BeforeEach
public void setUp() {
    cycleLifeClient = new CycleLifeClient(webClient);

    when(webClient.get()).thenReturn(requestHeadersUriSpec);

    // Cambiado a que acepte cualquier Long como argumento para el path variable
    when(requestHeadersUriSpec.uri(eq("/{id}"), anyLong())).thenReturn(requestHeadersSpec);

    when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
}


    @Test
    public void testGetCycleLifeFromExternal() {
        CycleLifeDTO expected = new CycleLifeDTO();
        expected.setCycleLifeId(1L);
        expected.setHenId(1L);
        expected.setEndDate(LocalDate.of(2025, 12, 31));
        expected.setTimesInWeeks("4");
        expected.setNameIto("Nombre de Ejemplo");

        when(responseSpec.bodyToMono(CycleLifeDTO.class)).thenReturn(Mono.just(expected));

        Mono<CycleLifeDTO> result = cycleLifeClient.getCycleLifeFromExternal(1L);

        StepVerifier.create(result)
            .assertNext(actual -> {
                assertEquals(expected.getCycleLifeId(), actual.getCycleLifeId());
                assertEquals(expected.getHenId(), actual.getHenId());
                assertEquals(expected.getEndDate(), actual.getEndDate());
                assertEquals(expected.getTimesInWeeks(), actual.getTimesInWeeks());
                assertEquals(expected.getNameIto(), actual.getNameIto());
            })
            .verifyComplete();
    }

    @Test
    public void testGetCycleLifeFromExternal_Error() {
        when(responseSpec.bodyToMono(CycleLifeDTO.class)).thenReturn(Mono.error(new RuntimeException("Error de red")));

        Mono<CycleLifeDTO> result = cycleLifeClient.getCycleLifeFromExternal(1L);

        StepVerifier.create(result)
            .expectErrorMatches(error -> error instanceof RuntimeException && error.getMessage().equals("Error de red"))
            .verify();
    }
}
