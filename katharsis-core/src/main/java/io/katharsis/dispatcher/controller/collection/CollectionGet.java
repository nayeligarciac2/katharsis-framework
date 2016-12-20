package io.katharsis.dispatcher.controller.collection;

import java.io.Serializable;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.katharsis.dispatcher.controller.HttpMethod;
import io.katharsis.dispatcher.controller.Response;
import io.katharsis.dispatcher.controller.resource.ResourceIncludeField;
import io.katharsis.queryspec.internal.QueryAdapter;
import io.katharsis.repository.RepositoryMethodParameterProvider;
import io.katharsis.request.path.JsonPath;
import io.katharsis.request.path.ResourcePath;
import io.katharsis.resource.Document;
import io.katharsis.resource.exception.ResourceNotFoundException;
import io.katharsis.resource.include.IncludeLookupSetter;
import io.katharsis.resource.registry.RegistryEntry;
import io.katharsis.resource.registry.ResourceRegistry;
import io.katharsis.resource.registry.repository.adapter.ResourceRepositoryAdapter;
import io.katharsis.utils.parser.TypeParser;

public class CollectionGet extends ResourceIncludeField {

	public CollectionGet(ResourceRegistry resourceRegistry, ObjectMapper objectMapper, TypeParser typeParser, IncludeLookupSetter fieldSetter) {
        super(resourceRegistry, objectMapper, typeParser, fieldSetter);
    }

    /**
     * Check if it is a GET request for a collection of resources.
     */
    @Override
    public boolean isAcceptable(JsonPath jsonPath, String requestType) {
        return jsonPath.isCollection()
                && jsonPath instanceof ResourcePath
                && HttpMethod.GET.name().equals(requestType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response handle(JsonPath jsonPath, QueryAdapter queryAdapter, RepositoryMethodParameterProvider
        parameterProvider, Document requestBody) {
        String resourceName = jsonPath.getElementName();
        RegistryEntry registryEntry = resourceRegistry.getEntry(resourceName);
        if (registryEntry == null) {
            throw new ResourceNotFoundException(resourceName);
        }
        Document responseDocument;
        ResourceRepositoryAdapter resourceRepository = registryEntry.getResourceRepository(parameterProvider);
        if (jsonPath.getIds() == null || jsonPath.getIds().getIds().isEmpty()) {
            responseDocument = documentMapper.toDocument(resourceRepository.findAll(queryAdapter));
        } else {
            Class<? extends Serializable> idType = (Class<? extends Serializable>)registryEntry
                .getResourceInformation().getIdField().getType();
            Iterable<? extends Serializable> parsedIds = typeParser.parse((Iterable<String>) jsonPath.getIds().getIds(),
                idType);
            responseDocument = documentMapper.toDocument(resourceRepository.findAll(parsedIds, queryAdapter));
        }
        includeFieldSetter.setIncludedElements(resourceName, responseDocument, queryAdapter, parameterProvider);

        return new Response(responseDocument, 200);
    }
}
