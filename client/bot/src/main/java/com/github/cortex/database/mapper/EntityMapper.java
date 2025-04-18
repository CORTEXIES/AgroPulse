package com.github.cortex.database.mapper;

public abstract class EntityMapper<E, Dto> {
    public abstract E toEntity(Dto entityDto);
    public abstract Dto toDto (E entity);
}