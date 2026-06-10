package cl.gesfun.gesfun_bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductoServicio(
        String tipoItem,
        String codigo,
        String nombre,
        String descripcion,
        Integer precio,
        Integer activo,
        Integer afecto,
        String unidadMedidaUuid,
        String empresaUuid
) {
}
