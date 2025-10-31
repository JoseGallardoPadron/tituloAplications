package pe.edu.vallegrande.VaccineAplication.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import pe.edu.vallegrande.VaccineAplication.dto.VaccineApplicationsDTO;
import pe.edu.vallegrande.VaccineAplication.model.VaccineApplicationsModel;
import pe.edu.vallegrande.VaccineAplication.repository.VaccineApplicationsRepository;
import pe.edu.vallegrande.VaccineAplication.webclient.client.CycleLifeClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Slf4j                // Lombok para logging
@Service              // Bean de Spring
@RequiredArgsConstructor // Inyección por constructor (Lombok)
public class VaccineApplicationsServiceImpl implements VaccineApplicationsService {

    private final VaccineApplicationsRepository vaccineApplicationsRepository;
    private final CycleLifeClient cycleLifeClient;
    
    /* ---------------- CRUD + helpers ---------------- */

    @Override
    public Mono<VaccineApplicationsDTO> save(VaccineApplicationsDTO dto) {
        // Validar email antes de proceder
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El email es requerido"));
        }
        
        if (!isValidEmail(dto.getEmail())) {
            return Mono.error(new IllegalArgumentException("El formato del email no es válido"));
        }

        // Establecer fecha de registro si no está presente
        if (dto.getDateRegistration() == null) {
            dto.setDateRegistration(LocalDate.now());
        }

        VaccineApplicationsModel entity = toEntity(dto);
        
        return cycleLifeClient.getCycleLifeFromExternal(entity.getCycleLifeId())
                .flatMap(cycle -> {
                    if (cycle.getTimesInWeeks() == null) {
                        return Mono.error(
                                new IllegalArgumentException("timesInWeeks no puede ser null"));
                    }
                    entity.setEndDate(cycle.getEndDate());
                    entity.setTimesInWeeks(cycle.getTimesInWeeks());
                    return vaccineApplicationsRepository.save(entity);
                })
                .map(this::toDTO);
    }

    @Override
    public Mono<VaccineApplicationsDTO> getApplicationById(Long id) {
        return vaccineApplicationsRepository.findById(id)
                .flatMap(app -> cycleLifeClient.getCycleLifeFromExternal(app.getCycleLifeId())
                        .map(cycle -> {
                            app.setEndDate(cycle.getEndDate());
                            app.setTimesInWeeks(cycle.getTimesInWeeks());
                            return toDTO(app);
                        }));
    }

    @Override
    public Flux<VaccineApplicationsDTO> getAllApplications() {
        return vaccineApplicationsRepository.findAll()
                .map(this::toDTO);
    }

    @Override
    public Mono<VaccineApplicationsDTO> updateApplication(Long id, VaccineApplicationsDTO dto) {
        // Validar email si está presente
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            if (!isValidEmail(dto.getEmail())) {
                return Mono.error(new IllegalArgumentException("El formato del email no es válido"));
            }
        }

        VaccineApplicationsModel entity = toEntity(dto);
        entity.setApplicationId(id);

        return cycleLifeClient.getCycleLifeFromExternal(entity.getCycleLifeId())
                .flatMap(cycle -> {
                    entity.setEndDate(cycle.getEndDate());
                    entity.setTimesInWeeks(cycle.getTimesInWeeks());
                    return vaccineApplicationsRepository.save(entity);
                })
                .map(this::toDTO);
    }

    @Override
    public Mono<VaccineApplicationsDTO> deactivateApplication(Long id) {
        return vaccineApplicationsRepository.findById(id)
                .flatMap(existing -> {
                    existing.setActive("I");
                    return vaccineApplicationsRepository.save(existing);
                })
                .map(this::toDTO);
    }

    @Override
    public Mono<VaccineApplicationsDTO> activateApplication(Long id) {
        return vaccineApplicationsRepository.findById(id)
                .flatMap(existing -> {
                    existing.setActive("A");
                    return vaccineApplicationsRepository.save(existing);
                })
                .map(this::toDTO);
    }

    @Override
    public Mono<VaccineApplicationsDTO> getCycleLifeIdById(Long cycleLifeId) {
        return vaccineApplicationsRepository.findByCycleLifeId(cycleLifeId)
                .map(this::toDTO);
    }

    /* -------------- Métodos auxiliares -------------- */

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /* -------------- mapeo DTO ↔ Entidad -------------- */

    private VaccineApplicationsModel toEntity(VaccineApplicationsDTO dto) {
        if (dto == null) return null;
        return new VaccineApplicationsModel(
                dto.getApplicationId(),
                dto.getEndDate(),
                dto.getHenId(),
                dto.getViaApplication(),
                dto.getCycleLifeId(),
                dto.getAmount(),
                dto.getCostApplication(),
                dto.getEmail(),
                dto.getDateRegistration(),
                dto.getQuantityBirds(),
                dto.getActive(),
                dto.getTimesInWeeks()
        );
    }

    private VaccineApplicationsDTO toDTO(VaccineApplicationsModel model) {
        if (model == null) return null;
        return new VaccineApplicationsDTO(
                model.getApplicationId(),
                model.getEndDate(),
                model.getHenId(),
                model.getViaApplication(),
                model.getCycleLifeId(),
                model.getAmount(),
                model.getCostApplication(),
                model.getEmail(),
                model.getDateRegistration(),
                model.getQuantityBirds(),
                model.getActive(),
                model.getTimesInWeeks()
        );
    }
}