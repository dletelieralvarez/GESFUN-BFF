package cl.gesfun.gesfun_bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Usuario(
        @Email(message = "El email debe tener un formato valido")
        @Size(max = 255, message = "El email no puede superar los 255 caracteres")
        String email,

        @Size(max = 255, message = "La password no puede superar los 255 caracteres")
        String password,

        @Size(max = 255, message = "El nombre no puede superar los 255 caracteres")
        String nombre,

        @Size(max = 255, message = "El apellido paterno no puede superar los 255 caracteres")
        String paterno,

        @Size(max = 255, message = "El apellido materno no puede superar los 255 caracteres")
        String materno,

        @Min(value = 0, message = "Activo debe ser 0 o 1")
        @Max(value = 1, message = "Activo debe ser 0 o 1")
        Integer activo,

        @Size(max = 75, message = "Roles no puede superar los 75 caracteres")
        String roles,

        @Size(max = 175, message = "Tipo usuario no puede superar los 175 caracteres")
        String tipoUsuario
) {
}
