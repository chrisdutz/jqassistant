package com.buschmais.jqassistant.plugin.json.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

@Label("Key")
public interface JSONKeyDescriptor extends JSONDescriptor {

    @Relation("CONTAINS_VALUE")
    JSONValueDescriptor<?> getValue();
}
