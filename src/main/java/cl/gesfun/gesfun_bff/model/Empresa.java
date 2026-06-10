package cl.gesfun.gesfun_bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Empresa(
        Integer rut,
        String dv,
        String razonSocial,
        Integer activo,
        String usuarioUuid,
        String comunaUuid,
        String direccion,
        String telefono
) {
}
