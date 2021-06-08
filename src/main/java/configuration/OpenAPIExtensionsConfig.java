package configuration;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithParentName;

import java.util.List;


@ConfigMapping(prefix = "extensions")
public interface OpenAPIExtensionsConfig {

    @WithParentName
    List<OpenAPIConfigurationExtension> extensions();

    interface OpenAPIConfigurationExtension {

        String group();

        String displayName();

        boolean hide();

        String description();

    }

}