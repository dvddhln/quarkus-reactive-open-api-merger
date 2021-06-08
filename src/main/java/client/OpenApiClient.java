package client;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@RegisterRestClient
public interface OpenApiClient extends AutoCloseable {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    Uni<String> getOpenAPI();
}
