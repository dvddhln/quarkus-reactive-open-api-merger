package configuration;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithParentName;

import java.util.List;


@ConfigMapping(prefix = "documentation.openapi.services")
public interface OpenAPIServicesConfig {

    @WithParentName
    List<OpenAPIConfiguration> configs();
    
    interface OpenAPIConfiguration {

        String displayName();

        String url();

        boolean hide();

    }

}