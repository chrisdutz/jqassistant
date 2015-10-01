package com.buschmais.jqassistant.plugin.json.api.model;

import com.buschmais.xo.neo4j.api.annotation.Indexed;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;

@Label("Value")
public interface JSONValueDescriptor<V> extends JSONDescriptor {
    @Indexed
    @Property("value")
    V getValue();

    void setValue(V value);
}
