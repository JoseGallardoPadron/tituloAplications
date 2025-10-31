package pe.edu.vallegrande.VaccineAplication.webclient.client;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.VaccineAplication.dto.CycleLifeDTO;
import reactor.core.publisher.Mono;

@Component
public class CycleLifeClient {

    private final WebClient webClient;

    // Inyección por constructor
    public CycleLifeClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<CycleLifeDTO> getCycleLifeFromExternal(Long cycleLifeId) {
        return webClient.get()
            .uri("/{id}", cycleLifeId)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(CycleLifeDTO.class);
    }
}
