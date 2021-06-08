package configuration;

import io.smallrye.config.ConfigMapping;


@ConfigMapping(prefix = "logo")
public interface OpenAPILogoExtensionConfig {

    String backgroundColor();

    String imgUrl();

}
