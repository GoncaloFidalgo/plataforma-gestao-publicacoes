package app.ws;

import app.dtos.tags.TagDTO;
import app.ejbs.TagBean;
import app.entities.Tag;
import app.exceptions.MyEntityExistsException;
import app.exceptions.MyEntityNotFoundException;
import app.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;

@Path("tags")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class TagService {

    @EJB
    private TagBean tagBean;

    @Context
    private SecurityContext securityContext;

    @GET
    public Response getAll(@QueryParam("hidden") Boolean hidden) {
        return Response.ok(tagBean.findAllWithCounts(hidden)).build();
    }


    @POST
    @RolesAllowed({"Administrator", "Responsavel"})
    public Response create(TagDTO dto) {
        try {
            tagBean.create(dto.getName(), dto.getDescription(), dto.getScientific_area());
            // Return what we received (assuming success)
            return Response.status(Response.Status.CREATED).entity(dto).build();
        } catch (MyEntityExistsException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
    }

    // Identify by Name now
    @PUT
    @Path("{name}")
    @RolesAllowed({"Administrator", "Responsavel"})
    public Response update(@PathParam("name") String name, TagDTO dto) {
        try {
            tagBean.update(name, dto.getDescription(), dto.getScientific_area(), dto.getHidden());

            Tag updatedTag = tagBean.find(name);
            return Response.ok(TagDTO.from(updatedTag)).build();
        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("{name}")
    @RolesAllowed({"Administrator", "Responsavel"})
    public Response delete(@PathParam("name") String name) {
        try {
            tagBean.delete(name);
            return Response.ok("{\"mensagem\": \"Tag removida do sistema com sucesso. Todas as associações foram removidas.\"}").build();
        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }


}