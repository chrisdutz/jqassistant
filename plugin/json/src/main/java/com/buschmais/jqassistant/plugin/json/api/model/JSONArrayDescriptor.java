package com.buschmais.jqassistant.plugin.json.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

@Label("Array")
public interface JSONArrayDescriptor extends JSONContainer, JSONValueDescriptor<JSONArrayDescriptor> {

    @Relation("CONTAINS_VALUE")
    List<JSONValueDescriptor<?>> getValues();
}
