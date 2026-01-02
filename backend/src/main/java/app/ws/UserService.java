package app.ws;

import app.dtos.UserDTO;
import app.ejbs.UserBean;
import app.exceptions.MyConstraintViolationException;
import app.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
@RolesAllowed({"Administrator"})
public class UserService {

    @EJB
    private UserBean userBean;

    //region POST
    @POST
    @Path("/")
    public Response createUser(@Valid UserDTO dto) {
        try {
            var user = userBean.createUser(
                    dto.getUsername(),
                    dto.getPassword(),
                    dto.getName(),
                    dto.getEmail(),
                    dto.getRole(),
                    dto.getActive() != null ? dto.getActive() : true
            );

            var responseDTO = UserDTO.from(user);
            return Response.status(Response.Status.CREATED).entity(responseDTO).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"mensagem\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (MyConstraintViolationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"mensagem\": \"Dados inválidos\"}")
                    .build();
        }
    }
    //endregion

    //region PUT
    @PUT
    @Path("/{username}")
    public Response updateUser(@PathParam("username") String username, @Valid UserDTO dto) {
        try {
            var user = userBean.find(username);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"mensagem\": \"Utilizador não encontrado\"}")
                        .build();
            }

            userBean.updateUser(
                    username,
                    dto.getUsername(),
                    dto.getEmail(),
                    dto.getActive()
            );

            var updatedUser = userBean.find(dto.getUsername() != null ? dto.getUsername() : username);
            var responseDTO = UserDTO.from(updatedUser);

            return Response.ok(responseDTO).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"mensagem\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"mensagem\": \"Erro ao atualizar utilizador\"}")
                    .build();
        }
    }
    //endregion

}
