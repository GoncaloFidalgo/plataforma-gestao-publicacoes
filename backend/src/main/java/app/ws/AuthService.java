package app.ws;

import app.dtos.AuthDTO;
import app.dtos.PasswordDTO;
import app.dtos.UserDTO;
import app.ejbs.UserBean;
import app.security.Authenticated;
import app.security.TokenIssuer;
import jakarta.ejb.EJB;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.Map;

@Path("auth")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class AuthService {
    @EJB
    private UserBean userBean;

    @Context
    private SecurityContext securityContext;

    //region GET
    @GET
    @Authenticated
    @Path("/user")
    public Response getAuthenticatedUser() {
        var username = securityContext.getUserPrincipal().getName();
        var user = userBean.find(username);
        return Response.ok(UserDTO.from(user)).build();
    }
    //endregion

    //region POST
    @POST
    @Path("/login")
    public Response authenticate(@Valid AuthDTO auth) {
        if (userBean.canLogin(auth.getUsername(), auth.getPassword())) {
            String token = TokenIssuer.issue(auth.getUsername());
            return Response.ok(token).build();
        }

        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @POST
    @Authenticated
    @Path("/refresh-token")
    public Response refreshToken() {
        var username = securityContext.getUserPrincipal().getName();
        var user = userBean.find(username);

        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        String newToken = TokenIssuer.issue(username);
        return Response.ok(newToken).build();
    }

    @POST
    @Path("/recover-password")
    public Response recoverPassword(Map<String, String> emailRequest) {
        String email = emailRequest.get("email");

        if (email == null || email.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"mensagem\": \"Email é obrigatório\"}")
                    .build();
        }

        try {
            userBean.recoverPassword(email);
            return Response.ok()
                    .entity("{\"mensagem\": \"E-mail de recuperação enviado com sucesso\"}")
                    .build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"mensagem\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }


    //endregion

    //region Patch
    @PATCH
    @Path("/set-password")
    @Authenticated
    public Response setPassword(@Context SecurityContext securityContext, @Valid PasswordDTO passwords) {
        var principal = securityContext.getUserPrincipal();
        if (principal == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        String username = principal.getName();

        if (passwords.getNewPassword() == null || !passwords.getNewPassword().equals(passwords.getConfirmPassword())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("New password and confirmation do not match").build();
        }

        try {
            userBean.changePassword(username, passwords.getOldPassword(), passwords.getNewPassword());
            return Response.ok("Palavra-passe redefinida com sucesso").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }
    //endregion
    //region PUT
    @PUT
    @Path("/reset-password")
    public Response resetPassword(Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("nova_password");

        if (token == null || token.isBlank() || newPassword == null || newPassword.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"mensagem\": \"Token e nova palavra-passe são obrigatórios\"}")
                    .build();
        }

        try {
            userBean.resetPassword(token, newPassword);
            return Response.ok()
                    .entity("{\"mensagem\": \"Palavra-passe redefinida com sucesso\"}")
                    .build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"mensagem\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    //endregion
}
