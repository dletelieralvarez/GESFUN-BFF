package cl.gesfun.gesfun_bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CotizacionPersona(
        @Size(max = 36, message = "El uuid no puede superar los 36 caracteres")
        String uuid,

        @Size(max = 36, message = "El uuid del tercero no puede superar los 36 caracteres")
        String terceroUuid,

        @Pattern(regexp = "N|J", message = "Tipo persona debe ser N o J")
        String tipoPersona,

        @Size(max = 25, message = "El rol no puede superar los 25 caracteres")
        String rol,

        @NotNull(message = "El rut es obligatorio")
        @Min(value = 1, message = "El rut debe ser mayor a 0")
        Integer rut,

        @NotBlank(message = "El digito verificador es obligatorio")
        @Size(max = 1, message = "El digito verificador no puede superar 1 caracter")
        String dv,

        @Size(max = 200, message = "El nombre completo no puede superar los 200 caracteres")
        String nombreCompleto,

        @Size(max = 100, message = "Los nombres no pueden superar los 100 caracteres")
        String nombres,

        @Size(max = 100, message = "El apellido paterno no puede superar los 100 caracteres")
        String apellidoPaterno,

        @Size(max = 100, message = "El apellido materno no puede superar los 100 caracteres")
        String apellidoMaterno,

        LocalDate fechaNacimiento,

        @Size(max = 250, message = "La razon social no puede superar los 250 caracteres")
        String razonSocial,

        @Size(max = 250, message = "El nombre fantasia no puede superar los 250 caracteres")
        String nombreFantasia,

        @Email(message = "El email debe tener un formato valido")
        @Size(max = 250, message = "El email no puede superar los 250 caracteres")
        String email,

        @Size(max = 175, message = "El telefono no puede superar los 175 caracteres")
        String telefono,

        @NotBlank(message = "El uuid de la comuna es obligatorio")
        @Size(max = 36, message = "El uuid de la comuna no puede superar los 36 caracteres")
        String comunaUuid
) {
    public CotizacionPersona conRol(String rol) {
        return new CotizacionPersona(
                uuid,
                terceroUuid,
                tipoPersona,
                rol,
                rut,
                dv,
                nombreCompleto,
                nombres,
                apellidoPaterno,
                apellidoMaterno,
                fechaNacimiento,
                razonSocial,
                nombreFantasia,
                email,
                telefono,
                comunaUuid
        );
    }
}
