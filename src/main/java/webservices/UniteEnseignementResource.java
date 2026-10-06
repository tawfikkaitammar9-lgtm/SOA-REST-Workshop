package webservices;

import business.UniteEnseignementService;
import entities.UniteEnseignement;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

@Path("/UE")
@Produces(MediaType.APPLICATION_JSON)
public class UniteEnseignementResource {
    private final UniteEnseignementService service = new UniteEnseignementService();

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response create(UniteEnseignement unit, @Context UriInfo uriInfo) {
        String error = validate(unit);
        if (error != null) return badRequest(error);
        if (!service.create(unit)) return Response.status(Response.Status.CONFLICT)
                .entity(errorBody("A teaching unit with this code already exists.")).build();
        URI location = uriInfo.getAbsolutePathBuilder().queryParam("code", unit.getCode()).build();
        return Response.created(location).entity(unit).build();
    }

    @GET
    public Response list(@QueryParam("semestre") Integer semester, @QueryParam("code") Integer code) {
        if (semester != null && code != null) return badRequest("Use either semestre or code, not both.");
        if (semester != null) return Response.ok(service.findBySemester(semester)).build();
        if (code != null) {
            UniteEnseignement unit = service.findByCode(code);
            return unit == null ? notFound("Teaching unit not found.") : Response.ok(unit).build();
        }
        return Response.ok(service.findAll()).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_XML)
    public Response update(@PathParam("id") int id, UniteEnseignement unit) {
        String error = validate(unit);
        if (error != null) return badRequest(error);
        return service.update(id, unit) ? Response.ok(service.findByCode(id)).build()
                : notFound("Teaching unit not found.");
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") int id) {
        return service.delete(id) ? Response.noContent().build() : notFound("Teaching unit not found.");
    }

    private String validate(UniteEnseignement unit) {
        if (unit == null || unit.getCode() <= 0) return "code must be a positive integer.";
        if (blank(unit.getDomaine()) || blank(unit.getResponsable())) return "domaine and responsable are required.";
        if (unit.getCredits() <= 0 || unit.getSemestre() <= 0) return "credits and semestre must be positive integers.";
        return null;
    }

    private boolean blank(String value) { return value == null || value.trim().isEmpty(); }
    static Response badRequest(String message) { return Response.status(Response.Status.BAD_REQUEST).entity(errorBody(message)).build(); }
    static Response notFound(String message) { return Response.status(Response.Status.NOT_FOUND).entity(errorBody(message)).build(); }
    static Map<String, String> errorBody(String message) { return Collections.singletonMap("error", message); }
}
