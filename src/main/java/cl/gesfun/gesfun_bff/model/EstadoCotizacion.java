package cl.gesfun.gesfun_bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EstadoCotizacion(
        String codigo,
        String nombre,
        Integer activo
) {
}
