package pe.edu.vallegrande.VaccineAplication.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("vaccine_applications") // Asegúrate de que coincide con el nombre en la BD
public class VaccineApplicationsModel {

    @Id
    private Long applicationId; // Identificador único para cada aplicación

    @Column("end_date") // Fecha de finalización de la aplicación de la vacuna
    private LocalDate endDate;

    @Column("hen_id") // Identificador del gallinero
    private Long henId; // Identificador del gallinero

    @Column("via_application") // Método de aplicación de la vacuna
    private String viaApplication; // Método de aplicación (solo letras)

    @Column("cycle_life_id") // Relación con la tabla cycle_life
    private Long cycleLifeId; // Relación con el ciclo de vida (ya no es foreign key)

    @Column("amount") // Cantidad de dosis aplicadas
    private Integer amount; // Debe ser mayor que cero

    @Column("cost_application") // Costo de la aplicación de la vacuna
    private BigDecimal costApplication; // Debe ser mayor que cero

    @Column("email") // Email del responsable o registrado
    private String email; // Debe ser un correo válido

    @Column("date_registration") // Fecha en la que se registró el dato
    private LocalDate dateRegistration; // Fecha de registro

    @Column("quantity_birds") // Número de aves vacunadas
    private Integer quantityBirds; // Número de aves vacunadas

    @Column("active") // Estado: 'A' = Activo, 'I' = Inactivo
    private String active = "A";  // Activo por defecto

    @Column("times_in_weeks") // Número de veces que se aplica la vacuna
    private String timesInWeeks; // Frecuencia de aplicación
}
