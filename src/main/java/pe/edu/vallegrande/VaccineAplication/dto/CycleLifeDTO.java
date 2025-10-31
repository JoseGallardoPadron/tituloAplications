package pe.edu.vallegrande.VaccineAplication.dto;

import java.time.LocalDate;

public class CycleLifeDTO {
    private Long cycleLifeId;
    private Long henId;
    private LocalDate endDate;
    private String timesInWeeks;
    private String nameIto;  // Nuevo campo agregado

    // Getter y Setter para cycleLifeId
    public Long getCycleLifeId() {
        return cycleLifeId;
    }

    public void setCycleLifeId(Long cycleLifeId) {
        this.cycleLifeId = cycleLifeId;
    }

    // Getter y Setter para henId
    public Long getHenId() {
        return henId;
    }

    public void setHenId(Long henId) {
        this.henId = henId;
    }

    // Getter y Setter para endDate
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    // Getter y Setter para timesInWeeks
    public String getTimesInWeeks() {
        return timesInWeeks;
    }

    public void setTimesInWeeks(String timesInWeeks) {
        this.timesInWeeks = timesInWeeks;
    }

    // Getter y Setter para nameIto
    public String getNameIto() {
        return nameIto;
    }

    public void setNameIto(String nameIto) {
        this.nameIto = nameIto;
    }

    // Método toString para depuración
    @Override
    public String toString() {
        return "CycleLifeDTO{" +
               "cycleLifeId=" + cycleLifeId +
               ", henId=" + henId +
               ", endDate=" + endDate +
               ", timesInWeeks='" + timesInWeeks + '\'' +
               ", nameIto='" + nameIto + '\'' +
               '}';
    }
}
