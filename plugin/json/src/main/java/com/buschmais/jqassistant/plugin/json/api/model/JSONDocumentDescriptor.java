package com.buschmais.jqassistant.plugin.json.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

@Label("Document")
public interface JSONDocumentDescriptor extends JSONDescriptor {
    @Relation("CONTAINS")
    <T extends JSONContainer> T getContainer();
}
