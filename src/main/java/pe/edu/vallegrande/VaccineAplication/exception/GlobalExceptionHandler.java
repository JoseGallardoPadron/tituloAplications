package pe.edu.vallegrande.VaccineAplication.exception;

import org.springframework.http.ResponseEntity; // Importa la clase para construir respuestas HTTP
import org.springframework.web.bind.annotation.ExceptionHandler; // Importa la anotación para manejar excepciones
import org.springframework.web.bind.annotation.RestControllerAdvice; // Importa la anotación para asesorar controladores REST

@RestControllerAdvice // Indica que esta clase proporciona manejo de excepciones para controladores REST
public class GlobalExceptionHandler {

    // Método que maneja excepciones de tipo RuntimeException
    @ExceptionHandler(RuntimeException.class) 
    public ResponseEntity<String> handleRuntime(RuntimeException ex) {
        // Devuelve una respuesta con un código de estado 400 (Bad Request) y un mensaje de error
        return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
    }
}
