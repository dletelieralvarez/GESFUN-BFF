package cl.gesfun.gesfun_bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Sucursal(
        String codigo,
        String nombre,
        String direccion,
        String telefono,
        Integer activo,
        String empresaUuid,
        String comunaUuid
) {
}
