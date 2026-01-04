package app.providers;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@PreMatching // CRITICAL: Run this before matching any specific resource URL
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    /**
     * Request Filter: Handles the preflight (OPTIONS) request immediately.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // If it's an OPTIONS request, we abort here with a 200 OK.
        // This prevents the request from reaching your controllers,
        // avoiding 404s, 405s, or the 409 Conflict you were seeing.
        if (isPreflightRequest(requestContext)) {
            requestContext.abortWith(Response.ok().build());
        }
    }

    /**
     * Response Filter: Adds the necessary CORS headers to the response.
     * This runs for both the aborted OPTIONS request and your normal API responses.
     */
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        // 1. Get the Origin from the request
        String origin = requestContext.getHeaderString("Origin");
        if (origin == null) {
            // If no origin, standard wildcard (though credentials won't work with this)
            origin = "*";
        }

        // 2. Reflect the origin back. Required when Credentials = true.
        responseContext.getHeaders().add("Access-Control-Allow-Origin", origin);
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");

        // 3. Allow standard methods
        responseContext.getHeaders().add("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");

        // 4. dynamic headers or standard set
        String reqHead = requestContext.getHeaderString("Access-Control-Request-Headers");
        if (reqHead != null && !reqHead.isEmpty()) {
            responseContext.getHeaders().add("Access-Control-Allow-Headers", reqHead);
        } else {
            responseContext.getHeaders().add("Access-Control-Allow-Headers",
                    "Origin, Content-Type, Accept, Authorization, X-Requested-With");
        }
    }

    private boolean isPreflightRequest(ContainerRequestContext request) {
        return request.getRequest().getMethod().equalsIgnoreCase("OPTIONS");
    }
}