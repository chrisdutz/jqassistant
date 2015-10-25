package com.buschmais.jqassistant.plugin.json.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;

@Label("Value")
public interface JSONArrayValueDescriptor extends JSONValueDescriptor<JSONArrayDescriptor> {

    // @todo BUGREPORT an DIRK, da ich hier die Methoden überschreiben mußte!
    @Override
    void setValue(JSONArrayDescriptor value);

    @Override
    JSONArrayDescriptor getValue();
}
