package app.ws;

import app.dtos.ActivityDTO;
import app.dtos.UserDTO;
import app.dtos.publication.PublicacaoDTO;
import app.dtos.rating.UserRatingsResponseDTO;
import app.dtos.tags.TagDTO;
import app.dtos.tags.TagSubscriptionDTO;
import app.ejbs.*;
import app.entities.Publicacao;
import app.entities.Rating;
import app.exceptions.MyConstraintViolationException;
import app.exceptions.MyEntityExistsException;
import app.exceptions.MyEntityNotFoundException;
import app.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import app.entities.Comment;
import app.dtos.comments.CommentDTO;


import java.util.List;
import java.util.stream.Collectors;


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

    @EJB
    private CommentBean commentBean;

    @EJB
    private TagBean tagBean;

    @EJB
    private RatingBean ratingBean;

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


    @GET
    @Path("/activity")
    @RolesAllowed({"Administrator", "Responsavel", "Colaborador"})
    public Response getMyActivity(@Context SecurityContext securityContext) {
        try {
            String username = securityContext.getUserPrincipal().getName();
            List<ActivityDTO> activities = userBean.getUserActivity(username);
            return Response.ok(activities).build();
        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"mensagem\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"mensagem\": \"Erro ao obter atividade pessoal\"}")
                    .build();
        }
    }
    //endregion

    //region Put
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


    @GET
    @Path("/comments")
    @Authenticated
    public Response getMyComments(@Context SecurityContext securityContext) {
        try {
            String username = securityContext.getUserPrincipal().getName();

            List<Comment> comments = commentBean.getCommentsByUsername(username);

            return Response.ok(CommentDTO.from(comments)).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"mensagem\": \"Erro ao obter comentários pessoais\"}")
                    .build();
        }
    }

    @POST
    @Path("/subscribed-tags")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    public Response subscribeTags(@Valid TagSubscriptionDTO dto) {
        String username = securityContext.getUserPrincipal().getName();

        try {
            tagBean.subscribeTags(username, dto.getTag_id());
            return Response.ok("{\"mensagem\":\"Tags subscritas com sucesso.\"}").build();
        } catch (MyEntityNotFoundException e) {
            e.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (MyEntityExistsException e) {
            e.printStackTrace();
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"mensagem\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @DELETE
    @Path("/subscribed-tags/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unsubscribeTag(@PathParam("name") String name) {
        String username = securityContext.getUserPrincipal().getName();

        try {
            tagBean.unsubscribeTags(username, List.of(name));
            return Response.ok("{\"mensagem\":\"Subscrição removida com sucesso.\"}").build();
        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/subscribed-tags")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMySubscribedTags() {
        String username = securityContext.getUserPrincipal().getName();

        try {
            var dtos = tagBean.getMySubscribedTags(username);
            return Response.ok(dtos).build();
        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/ratings")
    @Authenticated
    public Response getMyRatings(@Context SecurityContext securityContext) {
        try {
            String username = securityContext.getUserPrincipal().getName();

            List<Rating> ratings = ratingBean.findRatingsWithPublicationsByUser(username);

            UserRatingsResponseDTO response = UserRatingsResponseDTO.from(ratings);

            return Response.ok(response).build();

        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"mensagem\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"mensagem\": \"Erro ao obter ratings pessoais\"}")
                    .build();
        }
    }


}
