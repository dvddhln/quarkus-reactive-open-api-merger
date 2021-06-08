package control;


import io.smallrye.openapi.api.models.ComponentsImpl;
import io.smallrye.openapi.api.models.OpenAPIImpl;
import io.smallrye.openapi.api.models.PathItemImpl;
import io.smallrye.openapi.api.models.PathsImpl;
import io.smallrye.openapi.api.models.info.InfoImpl;
import io.smallrye.openapi.api.models.tags.TagImpl;
import io.smallrye.openapi.runtime.io.Format;
import io.smallrye.openapi.runtime.io.OpenApiParser;
import lombok.Getter;
import configuration.OpenAPIConfig;
import configuration.OpenAPIExtensionsConfig;
import configuration.OpenAPILogoExtensionConfig;
import configuration.OpenAPIServicesConfig;
import org.eclipse.microprofile.openapi.models.Components;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.Paths;
import org.eclipse.microprofile.openapi.models.callbacks.Callback;
import org.eclipse.microprofile.openapi.models.examples.Example;
import org.eclipse.microprofile.openapi.models.headers.Header;
import org.eclipse.microprofile.openapi.models.links.Link;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.eclipse.microprofile.openapi.models.parameters.RequestBody;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.eclipse.microprofile.openapi.models.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.models.security.SecurityScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


@ApplicationScoped
@Getter
public class OpenAPIService {

    private static Logger log = LoggerFactory.getLogger(OpenAPIService.class);

    OpenAPIImpl mergedOpenAPI;

    Map<OpenAPIServicesConfig.OpenAPIConfiguration, OpenAPI> openAPIServices;
    private static final String DISPLAY_NAME_KEY = "x-displayName";

    @Inject
    OpenAPIConfig openAPIConfig;

    @Inject
    OpenAPIExtensionsConfig openAPIExtensionsConfig;

    @Inject
    OpenAPILogoExtensionConfig openAPILogoExtensionConfig;

    public void addOpenAPI(OpenAPIServicesConfig.OpenAPIConfiguration openAPIConfiguration, String response) {
        if (openAPIServices == null) {
            openAPIServices = new TreeMap<>
                    (Comparator.comparing(OpenAPIServicesConfig.OpenAPIConfiguration::displayName));
        }

        try {
            openAPIServices.put(openAPIConfiguration, OpenApiParser.parse(new ByteArrayInputStream(response.getBytes()), Format.JSON));

        } catch (IOException e) {

        }

    }


    public OpenAPIImpl merge() {

        log.info("Attempting to merge");

        mergedOpenAPI = new OpenAPIImpl();

        InfoImpl info = new InfoImpl();
        info.setVersion(openAPIConfig.version());
        info.setTitle(openAPIConfig.title());
        mergedOpenAPI.setInfo(info);

        List<String> apiGroupList = new ArrayList<>();
        addExtensions(mergedOpenAPI, mergedOpenAPI, apiGroupList);

        mergedOpenAPI.setOpenapi(openAPIConfig.oasVersion());

        if (openAPIServices == null) {
            return null;
        }

        for (Map.Entry<OpenAPIServicesConfig.OpenAPIConfiguration, OpenAPI> openAPI : openAPIServices.entrySet()) {


            TagImpl mainTag = new TagImpl();
            mainTag.addExtension(DISPLAY_NAME_KEY, openAPI.getKey().displayName());
            mainTag.setName(openAPI.getKey().displayName());
            mergedOpenAPI.addTag(mainTag);
            apiGroupList.add(openAPI.getKey().displayName());


            if (openAPI.getValue() != null) {


                if (openAPI.getValue().getSecurity() != null) {
                    for (SecurityRequirement securityRequirement : openAPI.getValue().getSecurity()) {
                        mergedOpenAPI.addSecurityRequirement(securityRequirement);
                    }
                }

                if (openAPI.getValue().getPaths() != null) {
                    Paths existingPaths;
                    if (mergedOpenAPI.getPaths() == null) {
                        existingPaths = new PathsImpl();
                    } else {
                        existingPaths = mergedOpenAPI.getPaths();
                    }

                    for (Map.Entry<String, PathItem> path : openAPI.getValue().getPaths().getPathItems().entrySet()) {
                        path.getValue().servers(openAPI.getValue().getServers());
                        PathItemImpl pathItem = new PathItemImpl();
                        pathItem.servers(openAPI.getValue().getServers());


                        for (Map.Entry<PathItem.HttpMethod, Operation> operation : path.getValue().getOperations().entrySet()) {
                            pathItem.setOperation(operation.getKey(), operation.getValue());
                            operation.getValue().addTag(openAPI.getKey().displayName());
                        }
                        existingPaths.addPathItem(path.getKey(), path.getValue());
                    }
                    if (mergedOpenAPI.getPaths() == null) {
                        mergedOpenAPI.setPaths(existingPaths);
                    }
                }
                if (openAPI.getValue().getComponents() != null) {

                    Components components;
                    if (mergedOpenAPI.getComponents() == null) {
                        components = new ComponentsImpl();
                    } else {
                        components = mergedOpenAPI.getComponents();
                    }

                    if (openAPI.getValue().getComponents().getCallbacks() != null) {
                        for (Map.Entry<String, Callback> callback : openAPI.getValue().getComponents().getCallbacks().entrySet()) {
                            components.addCallback(callback.getKey(), callback.getValue());
                        }
                    }
                    if (openAPI.getValue().getComponents().getExamples() != null) {
                        for (Map.Entry<String, Example> example : openAPI.getValue().getComponents().getExamples().entrySet()) {
                            components.addExample(example.getKey(), example.getValue());
                        }
                    }
                    if (openAPI.getValue().getComponents().getHeaders() != null) {
                        for (Map.Entry<String, Header> header : openAPI.getValue().getComponents().getHeaders().entrySet()) {
                            components.addHeader(header.getKey(), header.getValue());
                        }
                    }
                    if (openAPI.getValue().getComponents().getLinks() != null) {
                        for (Map.Entry<String, Link> link : openAPI.getValue().getComponents().getLinks().entrySet()) {
                            components.addLink(link.getKey(), link.getValue());
                        }
                    }
                    if (openAPI.getValue().getComponents().getParameters() != null) {
                        for (Map.Entry<String, Parameter> parameter : openAPI.getValue().getComponents().getParameters().entrySet()) {
                            components.addParameter(parameter.getKey(), parameter.getValue());
                        }
                    }
                    if (openAPI.getValue().getComponents().getRequestBodies() != null) {
                        for (Map.Entry<String, RequestBody> requestBody : openAPI.getValue().getComponents().getRequestBodies().entrySet()) {
                            components.addRequestBody(requestBody.getKey(), requestBody.getValue());
                        }
                    }
                    if (openAPI.getValue().getComponents().getResponses() != null) {
                        for (Map.Entry<String, APIResponse> response : openAPI.getValue().getComponents().getResponses().entrySet()) {
                            components.addResponse(response.getKey(), response.getValue());
                        }
                    }
                    if (openAPI.getValue().getComponents().getSchemas() != null) {
                        for (Map.Entry<String, Schema> schemas : openAPI.getValue().getComponents().getSchemas().entrySet()) {
                            components.addSchema(schemas.getKey(), schemas.getValue());
                        }
                    }
                    if (openAPI.getValue().getComponents().getSecuritySchemes() != null) {
                        for (Map.Entry<String, SecurityScheme> securitySchemas : openAPI.getValue().getComponents().getSecuritySchemes().entrySet()) {
                            components.addSecurityScheme(securitySchemas.getKey(), securitySchemas.getValue());
                        }
                    }
                    if (openAPI.getValue().getComponents().getExtensions() != null) {
                        for (Map.Entry<String, Object> extensions : openAPI.getValue().getComponents().getExtensions().entrySet()) {
                            components.addExtension(extensions.getKey(), extensions.getValue());
                        }
                    }

                    mergedOpenAPI.setComponents(components);

                }
            }

            if (openAPI.getValue().getExtensions() != null) {
                for (Map.Entry<String, Object> vendorExtension : openAPI.getValue().getExtensions().entrySet()) {
                    mergedOpenAPI.addExtension(vendorExtension.getKey(), vendorExtension.getValue());
                }
            }
        }
        log.info("Merged");
        return mergedOpenAPI;
    }


    private void addExtensions(OpenAPIImpl mergedOpenAPI, OpenAPI openAPI, List<String> apiGroupList) {

        //vendor extensions(mainly for redoc)

        Map<String, List<String>> groups = new HashMap<>();

        for (OpenAPIExtensionsConfig.OpenAPIConfigurationExtension extension : openAPIExtensionsConfig.extensions()) {
            TagImpl extensionTag = new TagImpl();
            extensionTag.setName(extension.displayName());
            extensionTag.addExtension(DISPLAY_NAME_KEY, extension.displayName());
            extensionTag.setDescription(extension.description());
            mergedOpenAPI.addTag(extensionTag);
            groups.computeIfAbsent(extension.group(), k -> new ArrayList<>()).add(extension.displayName());
        }

        List<Map<String, Object>> tags = new ArrayList<>();

        for (Map.Entry<String, List<String>> group : groups.entrySet()) {
            Map<String, Object> documentationGroups = new HashMap<>();
            documentationGroups.put("name", group.getKey());
            documentationGroups.put("tags", group.getValue());
            tags.add(documentationGroups);
        }

        Map<String, Object> apiGroup = new HashMap<>();
        apiGroup.put("name", openAPIConfig.group());
        apiGroup.put("tags", apiGroupList);
        tags.add(apiGroup);

        mergedOpenAPI.addExtension("x-tagGroups", tags);

        //set extension
        Map<String, String> map = new HashMap<>();
        map.put("url", openAPILogoExtensionConfig.imgUrl());
        map.put("backgroundColor", openAPILogoExtensionConfig.backgroundColor());
        openAPI.getInfo().addExtension("x-logo", map);

    }

}