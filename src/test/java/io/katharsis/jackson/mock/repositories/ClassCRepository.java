package io.katharsis.jackson.mock.repositories;

import io.katharsis.jackson.mock.models.ClassC;
import io.katharsis.queryParams.RequestParams;
import io.katharsis.repository.ResourceRepository;

public class ClassCRepository implements ResourceRepository<ClassC, Long> {
    @Override
    public ClassC findOne(Long aLong, RequestParams requestParams) {
        return null;
    }

    @Override
    public Iterable<ClassC> findAll(RequestParams requestParams) {
        return null;
    }

    @Override
    public Iterable<ClassC> findAll(Iterable<Long> longs, RequestParams requestParams) {
        return null;
    }

    @Override
    public <S extends ClassC> S save(S entity) {
        return null;
    }

    @Override
    public void delete(Long aLong) {

    }
}
