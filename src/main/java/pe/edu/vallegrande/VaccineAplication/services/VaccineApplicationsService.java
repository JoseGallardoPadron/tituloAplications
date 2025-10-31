package pe.edu.vallegrande.VaccineAplication.services;

import pe.edu.vallegrande.VaccineAplication.dto.VaccineApplicationsDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VaccineApplicationsService {

    // Guardar aplicación
    Mono<VaccineApplicationsDTO> save(VaccineApplicationsDTO dto);

    // Obtener aplicación por ID
    Mono<VaccineApplicationsDTO> getApplicationById(Long id);

    // Obtener todas las aplicaciones
    Flux<VaccineApplicationsDTO> getAllApplications();

    // Actualizar aplicación
    Mono<VaccineApplicationsDTO> updateApplication(Long id, VaccineApplicationsDTO dto);

    // Desactivar aplicación
    Mono<VaccineApplicationsDTO> deactivateApplication(Long id);

    // Activar aplicación
    Mono<VaccineApplicationsDTO> activateApplication(Long id);

    // Obtener aplicación por cycleLifeId
    Mono<VaccineApplicationsDTO> getCycleLifeIdById(Long cycleLifeId);
}