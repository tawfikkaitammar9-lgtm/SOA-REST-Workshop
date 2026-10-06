package webservices;

import business.ModuleService;
import business.UniteEnseignementService;
import entities.Module;
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

@Path("/modules")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ModuleResource {
    private final ModuleService moduleService = new ModuleService();
    private final UniteEnseignementService unitService = new UniteEnseignementService();

    @POST
    public Response create(Module module, @Context UriInfo uriInfo) {
        String error = validateAndLink(module);
        if (error != null) return UniteEnseignementResource.badRequest(error);
        if (!moduleService.create(module)) return Response.status(Response.Status.CONFLICT)
                .entity(UniteEnseignementResource.errorBody("A module with this matricule already exists.")).build();
        URI location = uriInfo.getAbsolutePathBuilder().path(module.getMatricule()).build();
        return Response.created(location).entity(module).build();
    }

    @GET
    public Response list() { return Response.ok(moduleService.findAll()).build(); }

    @GET
    @Path("/UE")
    public Response listByTeachingUnit(@QueryParam("codeUE") Integer codeUE) {
        if (codeUE == null || codeUE <= 0) return UniteEnseignementResource.badRequest("codeUE must be a positive integer.");
        if (unitService.findByCode(codeUE) == null) return UniteEnseignementResource.notFound("Teaching unit not found.");
        return Response.ok(moduleService.findByUniteEnseignementCode(codeUE)).build();
    }

    @GET
    @Path("/{matricule}")
    public Response find(@PathParam("matricule") String matricule) {
        Module module = moduleService.findByMatricule(matricule);
        return module == null ? UniteEnseignementResource.notFound("Module not found.") : Response.ok(module).build();
    }

    @PUT
    @Path("/{matricule}")
    public Response update(@PathParam("matricule") String matricule, Module module) {
        String error = validateAndLink(module);
        if (error != null) return UniteEnseignementResource.badRequest(error);
        return moduleService.update(matricule, module) ? Response.ok(moduleService.findByMatricule(matricule)).build()
                : UniteEnseignementResource.notFound("Module not found.");
    }

    @DELETE
    @Path("/{matricule}")
    public Response delete(@PathParam("matricule") String matricule) {
        return moduleService.delete(matricule) ? Response.noContent().build()
                : UniteEnseignementResource.notFound("Module not found.");
    }

    private String validateAndLink(Module module) {
        if (module == null || blank(module.getMatricule()) || blank(module.getNom())) return "matricule and nom are required.";
        if (module.getCoefficient() <= 0 || module.getVolumeHoraire() <= 0) return "coefficient and volumeHoraire must be positive integers.";
        if (module.getType() == null) return "type is required.";
        if (module.getUniteEnseignement() == null || module.getUniteEnseignement().getCode() <= 0) return "uniteEnseignement.code is required.";
        UniteEnseignement unit = unitService.findByCode(module.getUniteEnseignement().getCode());
        if (unit == null) return "The referenced teaching unit does not exist.";
        module.setUniteEnseignement(unit);
        return null;
    }

    private boolean blank(String value) { return value == null || value.trim().isEmpty(); }
}
