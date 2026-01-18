package app.ws;

import app.dtos.ReferenceDTO;
import app.ejbs.ReferenceBean;
import app.exceptions.MyEntityExistsException;
import app.exceptions.MyEntityNotFoundException;
import app.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("references")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class ReferenceService {

    @EJB
    private ReferenceBean referenceBean;

    // --- TYPES ---

    @GET
    @Path("types")
    public Response getAllTypes() {
        return Response.ok(ReferenceDTO.fromTypes(referenceBean.findAllTypes())).build();
    }

    @POST
    @Path("types")
    @RolesAllowed({"Administrator", "Responsavel"})
    public Response createType(ReferenceDTO dto) {
        try {
            referenceBean.createType(dto.getName());
            return Response.status(Response.Status.CREATED).entity(dto).build();
        } catch (MyEntityExistsException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("types/{id}")
    @RolesAllowed({"Administrator", "Responsavel"})
    public Response deleteType(@PathParam("id") Long id) {
        try {
            referenceBean.deleteType(id);
            return Response.ok().build();
        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // --- AREAS ---

    @GET
    @Path("areas")
    public Response getAllAreas() {
        return Response.ok(ReferenceDTO.fromAreas(referenceBean.findAllAreas())).build();
    }

    @POST
    @Path("areas")
    @RolesAllowed({"Administrator", "Responsavel"})
    public Response createArea(ReferenceDTO dto) {
        try {
            referenceBean.createArea(dto.getName());
            return Response.status(Response.Status.CREATED).entity(dto).build();
        } catch (MyEntityExistsException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("areas/{id}")
    @RolesAllowed({"Administrator", "Responsavel"})
    public Response deleteArea(@PathParam("id") Long id) {
        try {
            referenceBean.deleteArea(id);
            return Response.ok().build();
        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}