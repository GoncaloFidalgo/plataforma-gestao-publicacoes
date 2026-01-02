package app.ws;

import app.dtos.AdministratorDTO;
import app.ejbs.AdministratorBean;
import app.exceptions.MyConstraintViolationException;
import app.exceptions.MyEntityExistsException;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("administrators")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdministratorService {
    @EJB
    private AdministratorBean administratorBean;

    //region GET
    @GET
    @Path("/")
    public List<AdministratorDTO> getAllAdministrators() {
        return AdministratorDTO.from(administratorBean.findAll());
    }

    @GET
    @Path("/{username}")
    public Response getAdministrator(@PathParam("username") String username) {
        try {
            var admin = administratorBean.find(username);
            return Response.ok(AdministratorDTO.from(admin)).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    //endregion
    //region POST
    @POST
    @Path("/")
    public Response createAdministrator(AdministratorDTO dto) throws MyEntityExistsException, MyConstraintViolationException {
        administratorBean.create(dto.getUsername(), dto.getPassword(), dto.getName(), dto.getEmail());
        return Response.status(Response.Status.CREATED).entity(dto).build();
    }
    //endregion
    //region PUT
    @PUT
    @Path("/{username}")
    public Response updateAdministrator(@PathParam("username") String username, AdministratorDTO dto) {
        try {
            administratorBean.update(username, dto.getPassword(), dto.getName(), dto.getEmail());
            return Response.ok(dto).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    //endregion
    //region DELETE
    @DELETE
    @Path("/{username}")
    public Response deleteAdministrator(@PathParam("username") String username) {
        try {
            administratorBean.delete(username);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    //endregion
}
