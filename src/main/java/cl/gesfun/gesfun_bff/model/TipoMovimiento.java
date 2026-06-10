package cl.gesfun.gesfun_bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TipoMovimiento(
        String codigo,
        String nombre,
        Integer activo,
        String tipoMvto
) {
}
