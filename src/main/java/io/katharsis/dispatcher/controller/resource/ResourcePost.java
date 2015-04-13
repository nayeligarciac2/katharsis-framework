package io.katharsis.dispatcher.controller.resource;

import io.katharsis.dispatcher.controller.BaseController;
import io.katharsis.path.JsonPath;
import io.katharsis.path.PathBuilder;
import io.katharsis.path.PathIds;
import io.katharsis.queryParams.RequestParams;
import io.katharsis.resource.exception.ResourceNotFoundException;
import io.katharsis.resource.registry.RegistryEntry;
import io.katharsis.resource.registry.ResourceRegistry;
import io.katharsis.response.BaseResponse;
import io.katharsis.response.Container;
import io.katharsis.response.ResourceResponse;
import io.katharsis.response.TopLevelLinks;

import java.io.Serializable;
import java.util.Collections;

public class ResourcePost implements BaseController {

    private ResourceRegistry resourceRegistry;
    private PathBuilder pathBuilder;

    public ResourcePost(ResourceRegistry resourceRegistry, PathBuilder pathBuilder) {
        this.resourceRegistry = resourceRegistry;
        this.pathBuilder = pathBuilder;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Check if it is a POST request for a resource.
     */
    @Override
    public boolean isAcceptable(JsonPath jsonPath, String requestType) {
        return !jsonPath.isCollection() && "POST".equals(requestType);
    }

    @Override
    public BaseResponse<?> handle(JsonPath jsonPath, RequestParams requestParams) {
        String resourceName = jsonPath.getResourceName();
        PathIds resourceIds = jsonPath.getIds();
        RegistryEntry registryEntry = resourceRegistry.getEntry(resourceName);
        if (registryEntry == null) {
            throw new ResourceNotFoundException("Resource of type not found: " + resourceName);
        }
        String id = resourceIds.getIds().get(0);

        Class<?> idType = registryEntry.getResourceInformation().getIdField().getType();
        Serializable castedId = castIdValue(id, idType);
        Object entity = registryEntry.getResourceRepository().save(castedId);

        jsonPath.setIds(new PathIds(Collections.singletonList(castedId.toString())));
        TopLevelLinks topLevelLinks = new TopLevelLinks(pathBuilder.buildPath(jsonPath));

        return new ResourceResponse(new Container(entity), topLevelLinks);
    }

    // @TODO add more customized casting of ids
    private Serializable castIdValue(String id, Class<?> idType) {
        if (Long.class == idType) {
            return Long.valueOf(id);
        }
        return id;
    }
}
