package pe.edu.vallegrande.VaccineAplication.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.VaccineAplication.model.VaccineApplicationsModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface VaccineApplicationsRepository extends ReactiveCrudRepository<VaccineApplicationsModel, Long> {
    
    // Método existente - encontrar por cycleLifeId
    Mono<VaccineApplicationsModel> findByCycleLifeId(Long cycleLifeId);
    
    // Buscar por email
    @Query("SELECT * FROM vaccine_applications WHERE email = :email")
    Flux<VaccineApplicationsModel> findByEmail(@Param("email") String email);
    
    // Buscar primer registro por email
    @Query("SELECT * FROM vaccine_applications WHERE email = :email LIMIT 1")
    Mono<VaccineApplicationsModel> findFirstByEmail(@Param("email") String email);
    
    // Buscar por henId
    @Query("SELECT * FROM vaccine_applications WHERE hen_id = :henId")
    Flux<VaccineApplicationsModel> findByHenId(@Param("henId") Long henId);
    
    // Buscar por estado activo
    @Query("SELECT * FROM vaccine_applications WHERE active = :active")
    Flux<VaccineApplicationsModel> findByActive(@Param("active") String active);
    
    // Buscar todas las aplicaciones activas
    @Query("SELECT * FROM vaccine_applications WHERE active = 'A' ORDER BY date_registration DESC")
    Flux<VaccineApplicationsModel> findAllActiveApplications();
    
    // Buscar por rango de fechas
    @Query("SELECT * FROM vaccine_applications WHERE date_registration BETWEEN :startDate AND :endDate")
    Flux<VaccineApplicationsModel> findByDateRegistrationBetween(@Param("startDate") LocalDate startDate, 
                                                                @Param("endDate") LocalDate endDate);
    
    // Buscar por vía de aplicación
    @Query("SELECT * FROM vaccine_applications WHERE via_application = :viaApplication")
    Flux<VaccineApplicationsModel> findByViaApplication(@Param("viaApplication") String viaApplication);
    
    // Contar registros activos
    @Query("SELECT COUNT(*) FROM vaccine_applications WHERE active = 'A'")
    Mono<Long> countActiveApplications();
    
    // Buscar los más recientes
    @Query("SELECT * FROM vaccine_applications ORDER BY date_registration DESC LIMIT :limit")
    Flux<VaccineApplicationsModel> findRecentApplications(@Param("limit") int limit);
    
    // Buscar por ID de aplicación y estado
    @Query("SELECT * FROM vaccine_applications WHERE application_id = :applicationId AND active = :active")
    Mono<VaccineApplicationsModel> findByApplicationIdAndActive(@Param("applicationId") Long applicationId, 
                                                               @Param("active") String active);
    
    // Buscar por henId y estado activo
    @Query("SELECT * FROM vaccine_applications WHERE hen_id = :henId AND active = 'A' ORDER BY date_registration DESC")
    Flux<VaccineApplicationsModel> findActiveApplicationsByHenId(@Param("henId") Long henId);
    
    // Verificar si existe una aplicación activa para un henId
    @Query("SELECT COUNT(*) > 0 FROM vaccine_applications WHERE hen_id = :henId AND active = 'A'")
    Mono<Boolean> existsActiveApplicationByHenId(@Param("henId") Long henId);
    
    // Buscar aplicaciones por estado y fecha
    @Query("SELECT * FROM vaccine_applications WHERE active = :active AND date_registration >= :fromDate ORDER BY date_registration DESC")
    Flux<VaccineApplicationsModel> findByActiveAndDateFrom(@Param("active") String active, 
                                                           @Param("fromDate") LocalDate fromDate);
    
    // Buscar por múltiples henIds
    @Query("SELECT * FROM vaccine_applications WHERE hen_id IN (:henIds) AND active = 'A'")
    Flux<VaccineApplicationsModel> findByHenIdInAndActive(@Param("henIds") Iterable<Long> henIds);
    
    // Buscar aplicaciones de hoy
    @Query("SELECT * FROM vaccine_applications WHERE DATE(date_registration) = CURRENT_DATE ORDER BY date_registration DESC")
    Flux<VaccineApplicationsModel> findTodayApplications();
    
    // Buscar aplicaciones por costo mínimo
    @Query("SELECT * FROM vaccine_applications WHERE cost_application >= :minCost")
    Flux<VaccineApplicationsModel> findByCostApplicationGreaterThanEqual(@Param("minCost") Double minCost);
    
    // Buscar aplicaciones por cantidad de aves
    @Query("SELECT * FROM vaccine_applications WHERE quantity_birds >= :minQuantity")
    Flux<VaccineApplicationsModel> findByQuantityBirdsGreaterThanEqual(@Param("minQuantity") Integer minQuantity);
    
    // Buscar aplicaciones próximas a vencer (basado en endDate)
    @Query("SELECT * FROM vaccine_applications WHERE end_date BETWEEN CURRENT_DATE AND :limitDate AND active = 'A'")
    Flux<VaccineApplicationsModel> findApplicationsExpiringBefore(@Param("limitDate") LocalDate limitDate);
}