package app.ws;

import app.dtos.PublicacaoDTO;
import app.dtos.UserDTO;
import app.ejbs.PublicacaoBean;
import app.ejbs.UserBean;
import app.entities.Publicacao;
import app.exceptions.MyConstraintViolationException;
import app.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;


@Path("users/me")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
@RolesAllowed({"Colaborador", "Responsavel", "Administrator"})
public class MeService {
    @EJB
    private UserBean userBean;

    @EJB
    private PublicacaoBean publicationBean;

    @Context
    private SecurityContext securityContext;


    @GET
    @Authenticated
    @Path("/")
    public Response getAuthenticatedUser(@Context SecurityContext securityContext) {
        var username = securityContext.getUserPrincipal().getName();
        var user = userBean.find(username);
        return Response.ok(UserDTO.from(user)).build();
    }

    @PUT
    @Path("/")
    @Authenticated
    public Response updateMe(@Context SecurityContext securityContext, @Valid UserDTO dto) {
        try {
            var username = securityContext.getUserPrincipal().getName();

            var principal = securityContext.getUserPrincipal();
            System.out.println("PUT /users/me principal=" + (principal != null ? principal.getName() : "null"));
            var user = userBean.find(username);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"mensagem\": \"Utilizador não encontrado\"}")
                        .build();
            }

            // IMPORTANTE: aqui normalmente NÃO deixas alterar "active"
            // e muito menos "role". Só dados pessoais.
            userBean.updatePersonalData(username, dto.getName(), dto.getEmail());

            var updatedUser = userBean.find(username);
            return Response.ok(UserDTO.from(updatedUser)).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"mensagem\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"mensagem\": \"Erro ao atualizar dados pessoais\"}")
                    .build();
        }
    }

    @GET
    @Path("/publications")
    @Authenticated
    public Response getMyPublications() {
        try {
            String username = securityContext.getUserPrincipal().getName();

            // Vai buscar as publicações onde o user é creator
            List<Publicacao> pubs = publicationBean.findPublicationsForUser(username);

            List<PublicacaoDTO> dto = pubs.stream()
                    .map(PublicacaoDTO::from)
                    .toList();

            return Response.ok(dto).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"mensagem\":\"Erro ao obter publicações pessoais\"}")
                    .build();
        }
    }
}
