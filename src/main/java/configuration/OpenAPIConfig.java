package configuration;

import io.smallrye.config.ConfigMapping;


@ConfigMapping(prefix = "documentation")
public interface OpenAPIConfig {

    String description();

    String version();

    String title();

    String baseUrl();

    String host();

    String oasVersion();

    String group();
}
