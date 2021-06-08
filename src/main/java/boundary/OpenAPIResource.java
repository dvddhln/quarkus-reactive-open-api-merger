package boundary;

import client.OpenAPIServiceConfigDTO;
import configuration.OpenAPIServicesConfig;
import control.OpenAPIService;
import io.smallrye.mutiny.Uni;
import io.smallrye.openapi.runtime.io.Format;
import io.smallrye.openapi.runtime.io.OpenApiSerializer;
import io.vertx.ext.web.Router;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Path("/v1/services")
public class OpenAPIResource {

    @Inject
    OpenAPIService openAPIService;

    @Inject
    OpenAPIServicesConfig openAPIServicesConfig;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<String> getServices() throws IOException {

        if (openAPIService.getMergedOpenAPI() == null) {
            return Uni.createFrom().item("");
        }
        return Uni.createFrom().item(OpenApiSerializer.serialize(openAPIService.getMergedOpenAPI(), Format.JSON));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list")
    public Uni<List<OpenAPIServiceConfigDTO.OpenAPIService>> getOpenAPIConfigurations() {
        OpenAPIServiceConfigDTO.OpenAPIService.OpenAPIServiceBuilder builder = OpenAPIServiceConfigDTO.OpenAPIService.builder();
        List<OpenAPIServiceConfigDTO.OpenAPIService> something = openAPIServicesConfig.configs().stream().map(openAPIService -> {
            builder.displayName(openAPIService.displayName());
            builder.hide(openAPIService.hide());
            builder.url(openAPIService.url());
            return builder.build();
        }).collect(Collectors.toList());
        return Uni.createFrom().item(something);

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list/{serviceId}")
    public Uni<String> getOpenAPI(@PathParam("serviceId") String name) throws IOException {

        return Uni.createFrom().item(OpenApiSerializer.serialize(openAPIService.getOpenAPIServices().entrySet().stream()
                .filter(swaggerService -> swaggerService.getKey().displayName()
                        .equals(name))
                .findFirst()
                .get().getValue(), Format.JSON));
    }

    public void init(@Observes Router router) {
        router.route("/redoc").handler(routingContext -> routingContext.reroute("/redoc.html"));
    }
}
