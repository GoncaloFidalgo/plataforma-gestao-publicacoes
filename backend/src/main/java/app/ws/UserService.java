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

    //region GET
    @GET
    @Path("/")
    public Response getAllUsers() {
        try {
            var users = userBean.findAll();
            var userDTOs = UserDTO.from(users);
            return Response.ok(userDTOs).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"mensagem\": \"Erro ao obter utilizadores\"}")
                    .build();
        }
    }

    @GET
    @Path("/{username}")
    public Response getUser(@PathParam("username") String username) {
        try {
            var user = userBean.find(username);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"mensagem\": \"Utilizador não encontrado\"}")
                        .build();
            }

            var userDTO = UserDTO.from(user);
            return Response.ok(userDTO).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"mensagem\": \"Erro ao obter utilizador\"}")
                    .build();
        }
    }
    //endregion

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
                    dto.getName(),
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

    //region DELETE
    @DELETE
    @Path("/{username}")
    public Response deleteUser(@PathParam("username") String username) {
        try {
            userBean.deleteUser(username);
            return Response.ok()
                    .entity("{\"mensagem\": \"Utilizador removido com sucesso\"}")
                    .build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"mensagem\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    //endregion

    //region PATCH
    @PATCH
    @Path("/{username}/status")
    public Response updateUserStatus(@PathParam("username") String username, java.util.Map<String, Object> statusUpdate) {
        try {
            Boolean active = (Boolean) statusUpdate.get("active");
            String motive = (String) statusUpdate.get("motive");

            if (active == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"mensagem\": \"Campo 'active' é obrigatório\"}")
                        .build();
            }

            userBean.updateUserStatus(username, active, motive);

            String message = active ? "Utilizador ativado com sucesso" : "Utilizador suspenso com sucesso";
            return Response.ok()
                    .entity("{\"mensagem\": \"" + message + "\"}")
                    .build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"mensagem\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @PATCH
    @Path("/{username}/role")
    public Response updateUserRole(@PathParam("username") String username, java.util.Map<String, Object> roleUpdate) {
        try {
            Integer role = null;
            Object roleObj = roleUpdate.get("role");

            if (roleObj instanceof Integer) {
                role = (Integer) roleObj;
            } else if (roleObj instanceof Double) {
                role = ((Double) roleObj).intValue();
            }

            if (role == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"mensagem\": \"Campo 'role' é obrigatório\"}")
                        .build();
            }

            userBean.updateUserRole(username, role);

            return Response.ok()
                    .entity("{\"mensagem\": \"Role atualizada com sucesso\"}")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"mensagem\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"mensagem\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    //endregion

}
