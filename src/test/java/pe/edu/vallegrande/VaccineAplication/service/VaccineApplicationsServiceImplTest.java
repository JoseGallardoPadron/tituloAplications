package pe.edu.vallegrande.VaccineAplication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pe.edu.vallegrande.VaccineAplication.dto.CycleLifeDTO;
import pe.edu.vallegrande.VaccineAplication.dto.VaccineApplicationsDTO;
import pe.edu.vallegrande.VaccineAplication.model.VaccineApplicationsModel;
import pe.edu.vallegrande.VaccineAplication.repository.VaccineApplicationsRepository;
import pe.edu.vallegrande.VaccineAplication.services.VaccineApplicationsServiceImpl;
import pe.edu.vallegrande.VaccineAplication.webclient.client.CycleLifeClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VaccineApplicationsServiceImplTest {

    @InjectMocks
    private VaccineApplicationsServiceImpl vaccineApplicationsService;

    @Mock
    private VaccineApplicationsRepository vaccineApplicationsRepository;

    @Mock
    private CycleLifeClient cycleLifeClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

@Test
void testSaveApplicationSuccess() {
    // Configuración del DTO
    VaccineApplicationsDTO dto = new VaccineApplicationsDTO();
    dto.setCycleLifeId(1L);
    dto.setEndDate(LocalDate.now().plusDays(30));
    dto.setTimesInWeeks("4");

    // Configuración del DTO del ciclo de vida
    CycleLifeDTO cycleLifeDTO = new CycleLifeDTO();
    cycleLifeDTO.setCycleLifeId(1L);
    cycleLifeDTO.setTimesInWeeks("4");
    cycleLifeDTO.setEndDate(LocalDate.now().plusDays(30));
    cycleLifeDTO.setNameIto("Nombre del Ciclo");

    // Configuración del modelo
    VaccineApplicationsModel model = new VaccineApplicationsModel();
    model.setCycleLifeId(dto.getCycleLifeId());
    model.setEndDate(dto.getEndDate());
    model.setTimesInWeeks(dto.getTimesInWeeks());
    model.setApplicationId(1L); // Simulando que se genera un ID al guardar

    // Simulación de comportamiento del cliente y repositorio
    when(cycleLifeClient.getCycleLifeFromExternal(dto.getCycleLifeId()))
            .thenReturn(Mono.just(cycleLifeDTO));
    when(vaccineApplicationsRepository.save(any(VaccineApplicationsModel.class)))
            .thenReturn(Mono.just(model));

    // Llamada al servicio
    Mono<VaccineApplicationsDTO> savedApplicationMono = vaccineApplicationsService.save(dto);
    VaccineApplicationsDTO savedApplication = savedApplicationMono.block(); // Descomponer el Mono

    // Verificaciones
    assertNotNull(savedApplication);
    assertEquals(model.getApplicationId(), savedApplication.getApplicationId()); // Verificar el ID generado
    assertEquals(dto.getCycleLifeId(), savedApplication.getCycleLifeId());
    assertEquals(dto.getTimesInWeeks(), savedApplication.getTimesInWeeks()); // Verificación aquí
    assertEquals(dto.getEndDate(), savedApplication.getEndDate()); // Verificar la fecha de finalización
}



    @Test
void testSaveApplicationCycleLifeTimesInWeeksNull() {
    VaccineApplicationsDTO dto = new VaccineApplicationsDTO();
    dto.setCycleLifeId(1L);

    CycleLifeDTO cycleLifeDTO = new CycleLifeDTO();
    cycleLifeDTO.setCycleLifeId(1L);
    cycleLifeDTO.setEndDate(LocalDate.now().plusDays(30));
    cycleLifeDTO.setNameIto("Nombre del Ciclo");
    // No se establece timesInWeeks

    when(cycleLifeClient.getCycleLifeFromExternal(dto.getCycleLifeId()))
            .thenReturn(Mono.just(cycleLifeDTO)); // Simula que no devuelve timesInWeeks

    Mono<VaccineApplicationsDTO> savedApplicationMono = vaccineApplicationsService.save(dto);
    
    // Cambiar a onErrorResume y verificar la excepción
    savedApplicationMono.onErrorResume(e -> {
        assertTrue(e instanceof IllegalArgumentException);
        assertEquals("timesInWeeks no puede ser null", e.getMessage());
        return Mono.empty();
    }).block(); // Asegúrate de que el resultado sea nulo
}


    @Test
    void testGetApplicationByIdSuccess() {
        VaccineApplicationsModel model = new VaccineApplicationsModel();
        model.setApplicationId(1L);
        model.setCycleLifeId(1L);
        model.setEndDate(LocalDate.now().plusDays(30));
        model.setTimesInWeeks("4");

        CycleLifeDTO cycleLifeDTO = new CycleLifeDTO();
        cycleLifeDTO.setCycleLifeId(1L);
        cycleLifeDTO.setEndDate(LocalDate.now().plusDays(30));
        cycleLifeDTO.setTimesInWeeks("4");
        cycleLifeDTO.setNameIto("Nombre del Ciclo");

        when(vaccineApplicationsRepository.findById(1L)).thenReturn(Mono.just(model));
        when(cycleLifeClient.getCycleLifeFromExternal(model.getCycleLifeId()))
                .thenReturn(Mono.just(cycleLifeDTO));

        Mono<VaccineApplicationsDTO> applicationMono = vaccineApplicationsService.getApplicationById(1L);
        VaccineApplicationsDTO application = applicationMono.block(); // Descomponer el Mono

        assertNotNull(application);
        assertEquals(model.getApplicationId(), application.getApplicationId());
    }

    @Test
    void testGetApplicationByIdNotFound() {
        when(vaccineApplicationsRepository.findById(999L)).thenReturn(Mono.empty());

        Mono<VaccineApplicationsDTO> applicationMono = vaccineApplicationsService.getApplicationById(999L);
        VaccineApplicationsDTO application = applicationMono.block(); // Descomponer el Mono

        assertNull(application);
    }

    @Test
    void testGetAllApplications() {
        VaccineApplicationsModel model1 = new VaccineApplicationsModel();
        model1.setApplicationId(1L);
        VaccineApplicationsModel model2 = new VaccineApplicationsModel();
        model2.setApplicationId(2L);

        when(vaccineApplicationsRepository.findAll()).thenReturn(Flux.just(model1, model2));

        Flux<VaccineApplicationsDTO> applicationsFlux = vaccineApplicationsService.getAllApplications();
        assertNotNull(applicationsFlux);
        assertEquals(2, applicationsFlux.collectList().block().size());
    }

    @Test
    void testUpdateApplicationSuccess() {
        VaccineApplicationsDTO dto = new VaccineApplicationsDTO();
        dto.setCycleLifeId(1L);
        dto.setApplicationId(1L);

        CycleLifeDTO cycleLifeDTO = new CycleLifeDTO();
        cycleLifeDTO.setCycleLifeId(1L);
        cycleLifeDTO.setEndDate(LocalDate.now().plusDays(30));
        cycleLifeDTO.setTimesInWeeks("4");
        cycleLifeDTO.setNameIto("Nombre del Ciclo");

        VaccineApplicationsModel model = new VaccineApplicationsModel();
        model.setApplicationId(dto.getApplicationId());
        model.setCycleLifeId(dto.getCycleLifeId());

        when(cycleLifeClient.getCycleLifeFromExternal(dto.getCycleLifeId()))
                .thenReturn(Mono.just(cycleLifeDTO));
        when(vaccineApplicationsRepository.save(any(VaccineApplicationsModel.class)))
                .thenReturn(Mono.just(model));

        Mono<VaccineApplicationsDTO> updatedApplicationMono = vaccineApplicationsService.updateApplication(1L, dto);
        VaccineApplicationsDTO updatedApplication = updatedApplicationMono.block(); // Descomponer el Mono

        assertNotNull(updatedApplication);
        assertEquals(dto.getApplicationId(), updatedApplication.getApplicationId());
    }

    @Test
    void testDeactivateApplicationSuccess() {
        VaccineApplicationsModel model = new VaccineApplicationsModel();
        model.setApplicationId(1L);
        model.setActive("A");

        when(vaccineApplicationsRepository.findById(1L)).thenReturn(Mono.just(model));
        when(vaccineApplicationsRepository.save(any(VaccineApplicationsModel.class)))
                .thenReturn(Mono.just(model));

        Mono<VaccineApplicationsDTO> deactivatedApplicationMono = vaccineApplicationsService.deactivateApplication(1L);
        VaccineApplicationsDTO deactivatedApplication = deactivatedApplicationMono.block(); // Descomponer el Mono

        assertNotNull(deactivatedApplication);
        assertEquals("I", deactivatedApplication.getActive());
    }

    @Test
    void testActivateApplicationSuccess() {
        VaccineApplicationsModel model = new VaccineApplicationsModel();
        model.setApplicationId(1L);
        model.setActive("I");

        when(vaccineApplicationsRepository.findById(1L)).thenReturn(Mono.just(model));
        when(vaccineApplicationsRepository.save(any(VaccineApplicationsModel.class)))
                .thenReturn(Mono.just(model));

        Mono<VaccineApplicationsDTO> activatedApplicationMono = vaccineApplicationsService.activateApplication(1L);
        VaccineApplicationsDTO activatedApplication = activatedApplicationMono.block(); // Descomponer el Mono

        assertNotNull(activatedApplication);
        assertEquals("A", activatedApplication.getActive());
    }

    @Test
    void testGetCycleLifeIdByIdSuccess() {
        VaccineApplicationsModel model = new VaccineApplicationsModel();
        model.setCycleLifeId(1L);

        when(vaccineApplicationsRepository.findByCycleLifeId(1L)).thenReturn(Mono.just(model));

        Mono<VaccineApplicationsDTO> cycleLifeApplicationMono = vaccineApplicationsService.getCycleLifeIdById(1L);
        VaccineApplicationsDTO cycleLifeApplication = cycleLifeApplicationMono.block(); // Descomponer el Mono

        assertNotNull(cycleLifeApplication);
        assertEquals(model.getCycleLifeId(), cycleLifeApplication.getCycleLifeId());
    }
}
