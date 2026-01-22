package app.ws;

import app.dtos.NotificationDTO;
import app.ejbs.NotificationBean;
import app.entities.Notification;
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

@Path("notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
@RolesAllowed({"Colaborador", "Responsavel", "Administrator"})
public class NotificationService {

    @EJB
    private NotificationBean notificationBean;

    @Context
    private SecurityContext securityContext;

    //region Get
    @GET
    public Response getAll(@QueryParam("unread") Boolean unreadOnly) {
        String username = securityContext.getUserPrincipal().getName();

        List<Notification> notifications;
        if (Boolean.TRUE.equals(unreadOnly)) {
            notifications = notificationBean.findUnreadByUser(username);
        } else {
            notifications = notificationBean.findAllByUser(username);
        }

        return Response.ok(NotificationDTO.from(notifications)).build();
    }

    @GET
    @Path("/count-unread")
    public Response countUnread() {
        String username = securityContext.getUserPrincipal().getName();
        long count = notificationBean.countUnread(username);

        return Response.ok()
                .entity("{\"unread\": " + count + "}")
                .build();
    }
    //endregion

    //region Patch
    @PATCH
    @Path("/{id}/read")
    public Response markAsRead(@PathParam("id") Long id) {
        try {
            String username = securityContext.getUserPrincipal().getName();
            notificationBean.markAsRead(id, username);

            return Response.ok()
                    .entity("{\"mensagem\": \"Notificação marcada como lida\"}")
                    .build();

        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"mensagem\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"mensagem\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @PATCH
    @Path("/read-all")
    public Response markAllAsRead() {
        String username = securityContext.getUserPrincipal().getName();
        notificationBean.markAllAsRead(username);

        return Response.ok()
                .entity("{\"mensagem\": \"Todas as notificações marcadas como lidas\"}")
                .build();
    }
    //endregion

    //region Delete
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            String username = securityContext.getUserPrincipal().getName();
            notificationBean.delete(id, username);

            return Response.ok()
                    .entity("{\"mensagem\": \"Notificação removida\"}")
                    .build();

        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"mensagem\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"mensagem\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    //endregion
}