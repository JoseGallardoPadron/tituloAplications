package pe.edu.vallegrande.VaccineAplication.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaccineApplicationsDTO {

    @JsonProperty("applicationId")
    private Long applicationId;

    @JsonProperty("endDate")
    private LocalDate endDate;

    @JsonProperty("henId")
    private Long henId;

    @JsonProperty("viaApplication")
    private String viaApplication;

    @JsonProperty("cycleLifeId")
    private Long cycleLifeId;

    @JsonProperty("amount")
    private Integer amount;

    @JsonProperty("costApplication")
    private BigDecimal costApplication;

    @JsonProperty("email")
    private String email;

    @JsonProperty("dateRegistration")
    private LocalDate dateRegistration;

    @JsonProperty("quantityBirds")
    private Integer quantityBirds;

    @JsonProperty("active")
    private String active = "A";

    @JsonProperty("timesInWeeks")
    private String timesInWeeks;
}
