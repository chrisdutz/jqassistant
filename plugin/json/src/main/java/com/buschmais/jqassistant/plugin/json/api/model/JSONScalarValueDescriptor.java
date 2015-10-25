package com.buschmais.jqassistant.plugin.json.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;

@Label("Value")
public interface JSONScalarValueDescriptor extends JSONValueDescriptor<Object> {

    @Property("value")
    @Override
    void setValue(Object value);

    @Override
    Object getValue();
}
