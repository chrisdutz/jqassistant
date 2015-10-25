package com.buschmais.jqassistant.plugin.json.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("Value")
public interface JSONObjectValueDescriptor extends JSONValueDescriptor<JSONObjectDescriptor> {

    @Override
    void setValue(JSONObjectDescriptor value);

    @Override
    JSONObjectDescriptor getValue();
}
