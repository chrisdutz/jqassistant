package com.buschmais.jqassistant.plugin.graphml.test;

import java.io.File;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.buschmais.jqassistant.core.analysis.api.Result;
import com.buschmais.jqassistant.plugin.graphml.report.api.GraphMLDecorator;
import com.buschmais.jqassistant.core.store.api.model.SubGraph;
import com.buschmais.jqassistant.plugin.graphml.report.decorator.YedGraphMLDecorator;
import com.buschmais.xo.api.CompositeObject;

public class CustomGraphMLDecorator implements GraphMLDecorator {

    private GraphMLDecorator delegate = new YedGraphMLDecorator();

    @Override
    public void initialize(Result<?> result, SubGraph subGraph, XMLStreamWriter xmlWriter, File file, Map<String, Object> properties) {
        delegate.initialize(result, subGraph, xmlWriter, file, properties);
    }

    @Override
    public Map<String, String> getNamespaces() {
        return delegate.getNamespaces();
    }

    @Override
    public Map<String, String> getSchemaLocations() {
        return delegate.getSchemaLocations();
    }

    @Override
    public void writeKeys() throws XMLStreamException {
        delegate.writeKeys();
    }

    @Override
    public boolean isWriteNode(CompositeObject node) {
        return true;
    }

    @Override
    public void writeNodeAttributes(CompositeObject node) throws XMLStreamException {
        delegate.writeNodeAttributes(node);
    }

    @Override
    public void writeNodeElements(CompositeObject node) throws XMLStreamException {
        delegate.writeNodeElements(node);
    }

    @Override
    public boolean isWriteRelationship(CompositeObject relationship) {
        return true;
    }

    @Override
    public void writeRelationshipAttributes(CompositeObject relationship) throws XMLStreamException {
        delegate.writeRelationshipAttributes(relationship);
    }

    @Override
    public void writeRelationshipElements(CompositeObject relationship) throws XMLStreamException {
        delegate.writeRelationshipElements(relationship);
    }

    @Override
    public void close() {
        delegate.close();
    }
}
