package pe.edu.vallegrande.VaccineAplication.config;


import lombok.Getter; // Importa la anotación para generar automáticamente getters
import org.springframework.security.authentication.AbstractAuthenticationToken; // Importa la clase base para autenticación
import org.springframework.security.core.GrantedAuthority; // Importa la interfaz para los roles o permisos
import org.springframework.security.oauth2.jwt.Jwt; // Importa la clase Jwt para manejar tokens JWT

import java.util.Collection; // Importa la colección para manejar múltiples roles

/**
 * CustomAuthenticationToken extiende AbstractAuthenticationToken
 * y representa al usuario autenticado a través de un JWT (token de Firebase en este caso).
 * Se usa para personalizar la información de autenticación y roles.
 */
@Getter // Genera automáticamente el método getter para el campo jwt
public class CustomAuthenticationToken extends AbstractAuthenticationToken {

    // Token JWT original con todos los claims (incluye sub, email, role, etc.)
    private final Jwt jwt;

    /**
     * 🔧 Constructor del token personalizado.
     *
     * @param jwt           El token JWT ya validado
     * @param authorities   Los roles o permisos extraídos del JWT
     */
    public CustomAuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities) {
        super(authorities); // Llama al constructor de la clase base con los roles
        this.jwt = jwt; // Asigna el token JWT
        setAuthenticated(true); // Marca el token como autenticado
    }

    /**
     * Devuelve el token como credenciales (aunque no se usa directamente)
     */
    @Override
    public Object getCredentials() {
        return jwt; // Retorna el token JWT como credenciales
    }

    /**
     * Devuelve el sujeto (usuario) como principal
     */
    @Override
    public Object getPrincipal() {
        return jwt.getSubject(); // Por lo general, el UID de Firebase
    }

    /**
     * Devuelve el nombre del usuario (por defecto usa el "sub" del JWT)
     */
    @Override
    public String getName() {
        return jwt.getSubject(); // Retorna el sujeto del JWT como nombre
    }
}


