package pe.edu.vallegrande.VaccineAplication.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pe.edu.vallegrande.VaccineAplication.dto.VaccineApplicationsDTO;
import pe.edu.vallegrande.VaccineAplication.services.VaccineApplicationsServiceImpl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VaccineApplicationsControllerTest {

    @InjectMocks
    private VaccineApplicationsController vaccineApplicationsController;

    @Mock
    private VaccineApplicationsServiceImpl vaccineApplicationsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateApplication() {
        VaccineApplicationsDTO dto = new VaccineApplicationsDTO();
        dto.setApplicationId(1L);
        dto.setEndDate(LocalDate.now().plusDays(30));
        dto.setHenId(101L);
        dto.setViaApplication("Injection");
        dto.setCycleLifeId(5L);
        dto.setAmount(100);
        dto.setCostApplication(BigDecimal.valueOf(1500));
        dto.setEmail("test@example.com");
        dto.setDateRegistration(LocalDate.now());
        dto.setQuantityBirds(50);
        dto.setTimesInWeeks("4");

        when(vaccineApplicationsService.save(any(VaccineApplicationsDTO.class)))
            .thenReturn(Mono.just(dto));

        ResponseEntity<VaccineApplicationsDTO> response = vaccineApplicationsController.createApplication(dto).block();

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testCreateApplicationError() {
        VaccineApplicationsDTO dto = new VaccineApplicationsDTO();

        when(vaccineApplicationsService.save(any(VaccineApplicationsDTO.class)))
            .thenReturn(Mono.error(new RuntimeException("Error")));

        ResponseEntity<VaccineApplicationsDTO> response = vaccineApplicationsController.createApplication(dto).block();

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetAllApplications() {
        VaccineApplicationsDTO dto1 = new VaccineApplicationsDTO();
        VaccineApplicationsDTO dto2 = new VaccineApplicationsDTO();

        when(vaccineApplicationsService.getAllApplications()).thenReturn(Flux.just(dto1, dto2));

        Flux<VaccineApplicationsDTO> response = vaccineApplicationsController.getAllApplications();

        assertNotNull(response);
        assertEquals(2, response.collectList().block().size());
    }

    @Test
    void testGetApplicationById() {
        VaccineApplicationsDTO dto = new VaccineApplicationsDTO();
        dto.setApplicationId(1L);
        dto.setEndDate(LocalDate.now().plusDays(30));
        dto.setHenId(101L);
        dto.setViaApplication("Injection");
        dto.setCycleLifeId(5L);
        dto.setAmount(100);
        dto.setCostApplication(BigDecimal.valueOf(1500));
        dto.setEmail("test@example.com");
        dto.setDateRegistration(LocalDate.now());
        dto.setQuantityBirds(50);
        dto.setTimesInWeeks("4");

        when(vaccineApplicationsService.getApplicationById(1L)).thenReturn(Mono.just(dto));

        ResponseEntity<VaccineApplicationsDTO> response = vaccineApplicationsController.getApplicationById(1L).block();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testGetApplicationByIdNotFound() {
        when(vaccineApplicationsService.getApplicationById(999L)).thenReturn(Mono.empty());

        ResponseEntity<VaccineApplicationsDTO> response = vaccineApplicationsController.getApplicationById(999L).block();

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateApplication() {
        VaccineApplicationsDTO dto = new VaccineApplicationsDTO();
        dto.setApplicationId(1L);
        dto.setEndDate(LocalDate.now().plusDays(30));
        dto.setHenId(101L);
        dto.setViaApplication("Injection");
        dto.setCycleLifeId(5L);
        dto.setAmount(100);
        dto.setCostApplication(BigDecimal.valueOf(1500));
        dto.setEmail("test@example.com");
        dto.setDateRegistration(LocalDate.now());
        dto.setQuantityBirds(50);
        dto.setTimesInWeeks("4");

        when(vaccineApplicationsService.updateApplication(any(Long.class), any(VaccineApplicationsDTO.class)))
            .thenReturn(Mono.just(dto));

        ResponseEntity<VaccineApplicationsDTO> response = vaccineApplicationsController.updateApplication(1L, dto).block();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testUpdateApplicationNotFound() {
        VaccineApplicationsDTO dto = new VaccineApplicationsDTO();
        dto.setApplicationId(1L);

        when(vaccineApplicationsService.updateApplication(any(Long.class), any(VaccineApplicationsDTO.class)))
            .thenReturn(Mono.empty());

        ResponseEntity<VaccineApplicationsDTO> response = vaccineApplicationsController.updateApplication(1L, dto).block();

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeactivateApplication() {
        VaccineApplicationsDTO dto = new VaccineApplicationsDTO();
        dto.setApplicationId(1L);
        dto.setEndDate(LocalDate.now().plusDays(30));
        dto.setHenId(101L);
        dto.setViaApplication("Injection");
        dto.setCycleLifeId(5L);
        dto.setAmount(100);
        dto.setCostApplication(BigDecimal.valueOf(1500));
        dto.setEmail("test@example.com");
        dto.setDateRegistration(LocalDate.now());
        dto.setQuantityBirds(50);
        dto.setTimesInWeeks("4");

        when(vaccineApplicationsService.deactivateApplication(1L)).thenReturn(Mono.just(dto));

        ResponseEntity<VaccineApplicationsDTO> response = vaccineApplicationsController.deactivateApplication(1L).block();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testDeactivateApplicationNotFound() {
        when(vaccineApplicationsService.deactivateApplication(999L)).thenReturn(Mono.empty());

        ResponseEntity<VaccineApplicationsDTO> response = vaccineApplicationsController.deactivateApplication(999L).block();

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testActivateApplication() {
        VaccineApplicationsDTO dto = new VaccineApplicationsDTO();
        dto.setApplicationId(1L);
        dto.setEndDate(LocalDate.now().plusDays(30));
        dto.setHenId(101L);
        dto.setViaApplication("Injection");
        dto.setCycleLifeId(5L);
        dto.setAmount(100);
        dto.setCostApplication(BigDecimal.valueOf(1500));
        dto.setEmail("test@example.com");
        dto.setDateRegistration(LocalDate.now());
        dto.setQuantityBirds(50);
        dto.setTimesInWeeks("4");

        when(vaccineApplicationsService.activateApplication(1L)).thenReturn(Mono.just(dto));

        ResponseEntity<VaccineApplicationsDTO> response = vaccineApplicationsController.activateApplication(1L).block();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testActivateApplicationNotFound() {
        when(vaccineApplicationsService.activateApplication(999L)).thenReturn(Mono.empty());

        ResponseEntity<VaccineApplicationsDTO> response = vaccineApplicationsController.activateApplication(999L).block();

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
