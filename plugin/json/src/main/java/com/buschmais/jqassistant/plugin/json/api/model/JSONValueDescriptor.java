package com.buschmais.jqassistant.plugin.json.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.ValueDescriptor;
import com.buschmais.xo.api.annotation.Abstract;
import com.buschmais.xo.neo4j.api.annotation.Indexed;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;

@Abstract
public interface JSONValueDescriptor<V> extends JSONDescriptor {

    /**
     * Set the value.
     *
     * @param value The value.
     */
    void setValue(V value);

    /**
     * Return the value.
     *
     * @return The value.
     */
    V getValue();
}

