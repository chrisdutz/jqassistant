package com.buschmais.jqassistant.plugin.java.api.model;

import java.util.List;

import com.buschmais.jqassistant.plugin.common.api.model.ValueDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

/**
 * Represents an Java array value.
 */
@Label("Array")
public interface ArrayValueDescriptor<V> extends JavaDescriptor, ValueDescriptor<List<ValueDescriptor<V>>> {

    @Relation("CONTAINS")
    @Override
    List<ValueDescriptor<V>> getValue();

    @Override
    void setValue(List<ValueDescriptor<V>> value);

}
