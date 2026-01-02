package app.exceptions.mappers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.logging.Logger;

@Provider
public class ThrowableMapper implements ExceptionMapper<Throwable> {
    private static final Logger logger =
            Logger.getLogger(Throwable.class.getCanonicalName());

    @Override
    public Response toResponse(Throwable e) {
        String errorMsg = e.getMessage();
        logger.warning("ERROR: " + errorMsg);
        return Response.status(Response.Status.CONFLICT)
                .entity(errorMsg)
                .build();
    }

}
