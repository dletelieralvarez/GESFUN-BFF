package cl.gesfun.gesfun_bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MotivoFallecimiento(
        String codigo,
        String nombre,
        String descripcion,
        Integer activo
) {
}
