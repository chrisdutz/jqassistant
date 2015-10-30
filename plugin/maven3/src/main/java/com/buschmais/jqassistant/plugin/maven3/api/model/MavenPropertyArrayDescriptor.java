package com.buschmais.jqassistant.plugin.maven3.api.model;

import java.util.List;

import com.buschmais.jqassistant.plugin.common.api.model.ValueDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

@Label("Array")
public interface MavenPropertyArrayDescriptor extends MavenDescriptor, ValueDescriptor<List<ValueDescriptor<?>>> {

    @Relation("CONTAINS")
    @Override
    List<ValueDescriptor<?>> getValue();

    @Override
    void setValue(List<ValueDescriptor<?>> value);
}
