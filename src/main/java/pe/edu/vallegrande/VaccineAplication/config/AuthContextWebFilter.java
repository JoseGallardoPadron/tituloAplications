package pe.edu.vallegrande.VaccineAplication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;

// Clase de configuración para el filtro de autorización
@Configuration
public class AuthContextWebFilter {

    // Método que define un filtro para la propagación del token JWT
    @Bean
    public WebFilter jwtTokenPropagationFilter() {
        return (exchange, chain) -> {
            // Obtiene el encabezado de autorización de la solicitud
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            
            // Verifica si el encabezado de autorización existe y comienza con "Bearer "
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // Extrae el token del encabezado
                String token = authHeader.substring(7);
                
                // Continúa con la cadena de filtros y agrega el token al contexto
                return chain.filter(exchange).contextWrite(ctx -> ctx.put("Authorization", token));
            }
            
            // Si no hay token, continúa con la cadena de filtros sin modificar el contexto
            return chain.filter(exchange);
        };
    }
}



