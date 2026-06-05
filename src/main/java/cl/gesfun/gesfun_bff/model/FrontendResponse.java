package cl.gesfun.gesfun_bff.model;

public final class FrontendResponse<T> {

    /*
        Modelo simple de respuesta para el frontend.
        Contiene:
        success: si la petición fue exitosa.
        payload: datos devueltos.
        message: texto de error o información adicional.
        Facilita que Angular reciba siempre una estructura consistente.
    */

    private final boolean success;
    private final T payload;
    private final String message;

    private FrontendResponse(boolean success, T payload, String message) {
        this.success = success;
        this.payload = payload;
        this.message = message;
    }

    public static <T> FrontendResponse<T> success(T payload) {
        return new FrontendResponse<>(true, payload, null);
    }

    public static <T> FrontendResponse<T> failure(String message) {
        return new FrontendResponse<>(false, null, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public T getPayload() {
        return payload;
    }

    public String getMessage() {
        return message;
    }
}
