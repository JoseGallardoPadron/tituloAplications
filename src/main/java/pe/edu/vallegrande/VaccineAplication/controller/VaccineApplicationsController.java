package pe.edu.vallegrande.VaccineAplication.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pe.edu.vallegrande.VaccineAplication.dto.VaccineApplicationsDTO;
import pe.edu.vallegrande.VaccineAplication.services.VaccineApplicationsService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/vaccine-applications")
@RequiredArgsConstructor
public class VaccineApplicationsController {

    private final VaccineApplicationsService vaccineApplicationsService;

    /* ---------- CREATE ---------- */
    @PostMapping("/create")
    public Mono<ResponseEntity<VaccineApplicationsDTO>> createApplication(
            @RequestBody VaccineApplicationsDTO dto) {

        return vaccineApplicationsService.save(dto)
            .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved))
            .onErrorResume(e -> {
                log.error("Error creando la aplicación de vacuna: {}", e.getMessage());
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
            });
    }

    /* ---------- READ ALL ---------- */
    @GetMapping
    public Flux<VaccineApplicationsDTO> getAllApplications() {
        return vaccineApplicationsService.getAllApplications();
    }

    /* ---------- READ ONE ---------- */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<VaccineApplicationsDTO>> getApplicationById(@PathVariable Long id) {
        return vaccineApplicationsService.getApplicationById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(e -> {
                    log.error("Error obteniendo aplicación con ID {}: {}", id, e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
                });
    }

    /* ---------- READ BY CYCLE LIFE ID ---------- */
    @GetMapping("/cycle/{cycleLifeId}")
    public Mono<ResponseEntity<VaccineApplicationsDTO>> getApplicationByCycleLifeId(@PathVariable Long cycleLifeId) {
        return vaccineApplicationsService.getCycleLifeIdById(cycleLifeId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(e -> {
                    log.error("Error obteniendo aplicación con CycleLifeId {}: {}", cycleLifeId, e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
                });
    }

    /* ---------- UPDATE ---------- */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<VaccineApplicationsDTO>> updateApplication(
            @PathVariable Long id,
            @RequestBody VaccineApplicationsDTO dto) {

        dto.setApplicationId(id);          // asegurar consistencia
        return vaccineApplicationsService.updateApplication(id, dto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(e -> {
                    log.error("Error actualizando aplicación con ID {}: {}", id, e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
                });
    }

    /* ---------- SOFT‑DELETE ---------- */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<VaccineApplicationsDTO>> deactivateApplication(@PathVariable Long id) {
        return vaccineApplicationsService.deactivateApplication(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(e -> {
                    log.error("Error desactivando aplicación con ID {}: {}", id, e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
                });
    }

    /* ---------- RE‑ACTIVATE ---------- */
    @PutMapping("/activate/{id}")
    public Mono<ResponseEntity<VaccineApplicationsDTO>> activateApplication(@PathVariable Long id) {
        return vaccineApplicationsService.activateApplication(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(e -> {
                    log.error("Error activando aplicación con ID {}: {}", id, e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
                });
    }
}