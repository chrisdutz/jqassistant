package com.buschmais.jqassistant.plugin.json.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

import java.util.List;

@Label("Object")
public interface JSONObjectDescriptor extends JSONContainer, JSONValueDescriptor<JSONObjectDescriptor> {

    List<JSONKeyDescriptor> getKeys();
}
