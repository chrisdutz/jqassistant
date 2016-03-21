package com.buschmais.jqassistant.plugin.graphml.report.impl;

import com.buschmais.jqassistant.core.analysis.api.Result;
import com.buschmais.jqassistant.core.shared.reflection.ClassHelper;
import com.buschmais.jqassistant.plugin.graphml.report.api.GraphMLDecorator;
import com.buschmais.jqassistant.core.store.api.model.SubGraph;
import com.buschmais.xo.api.CompositeObject;
import com.sun.xml.txw2.output.IndentingXMLStreamWriter;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static com.buschmais.jqassistant.plugin.graphml.report.impl.MetaInformation.getLabelsString;

/**
 * @author mh
 * @since 21.01.14
 */
class XmlGraphMLWriter {

    private static final String GRAPHML_DECORATOR = "graphml.report.decorator";

    private static final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();

    private ClassHelper classHelper;

    private Class<? extends GraphMLDecorator> defaultDecoratorClass;

    private Map<String, Object> properties;

    /**
     * Constructor.
     *
     * @param classHelper           The class helper instance.
     * @param defaultDecoratorClass The class for the default decorator.
     * @param properties            The properties of the GraphML plugin.
     */
    XmlGraphMLWriter(ClassHelper classHelper, Class<? extends GraphMLDecorator> defaultDecoratorClass, Map<String, Object> properties) {
        this.classHelper = classHelper;
        this.defaultDecoratorClass = defaultDecoratorClass;
        this.properties = properties;
    }

    void write(Result<?> result, SubGraph graph, File file) throws IOException, XMLStreamException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file));
             GraphMLDecorator decorator = getGraphMLDecorator(result)) {
            XMLStreamWriter xmlWriter = new IndentingXMLStreamWriter(xmlOutputFactory.createXMLStreamWriter(writer));
            decorator.initialize(result, graph, xmlWriter, file, properties);
            GraphMLNamespaceContext context = new GraphMLNamespaceContext(decorator.getNamespaces(), decorator.getSchemaLocations());
            xmlWriter.setNamespaceContext(context);
            writeHeader(xmlWriter, context);
            writeKeyTypes(xmlWriter, graph);
            decorator.writeKeys();

            writeSubgraph(graph, xmlWriter, decorator);

            // filter and write edges
            Collection<CompositeObject> allCoNodes = graph.getNodes();
            Set<Long> allNodes = new HashSet<>();
            for (CompositeObject compositeObject : allCoNodes) {
                allNodes.add(((Node) compositeObject.getDelegate()).getId());
            }

            for (CompositeObject coRel : graph.getRelationships()) {
                Relationship rel = coRel.getDelegate();
                long startId = rel.getStartNode().getId();
                long endId = rel.getEndNode().getId();
                if (allNodes.contains(startId) && allNodes.contains(endId)) {
                    writeRelationship(xmlWriter, decorator, coRel);
                }
            }

            writeFooter(xmlWriter);

            decorator.close();
        }
    }

    /**
     * Creates an instance of the select {@link GraphMLDecorator}.
     *
     * @param result The rule result.
     * @return The {@link GraphMLDecorator}.
     */
    private GraphMLDecorator getGraphMLDecorator(Result<?> result) {
        String graphMLDecorator = result.getRule().getReport().getProperties().getProperty(GRAPHML_DECORATOR);
        Class<? extends GraphMLDecorator> decoratorClass;
        if (graphMLDecorator != null) {
            decoratorClass = classHelper.getType(graphMLDecorator);
        } else {
            decoratorClass = defaultDecoratorClass;
        }
        return classHelper.createInstance(decoratorClass);
    }

    private void writeSubgraph(SubGraph graph, XMLStreamWriter writer, GraphMLDecorator decorator) throws XMLStreamException, IOException {
        CompositeObject wrapperNode = graph.getParentNode();
        if (wrapperNode != null) {
            writeNode(writer, decorator, wrapperNode, false);
        }

        writer.writeStartElement("graph");
        writer.writeAttribute("id", "G" + graph.hashCode());
        writer.writeAttribute("edgedefault", "directed");
        newLine(writer);

        for (CompositeObject node : graph.getNodes()) {
            writeNode(writer, decorator, node, true);
        }

        for (SubGraph subgraph : graph.getSubGraphs()) {
            writeSubgraph(subgraph, writer, decorator);
        }

        endElement(writer);

        if (wrapperNode != null) {
            writer.writeEndElement();
        }
    }

    private void writeKeyTypes(XMLStreamWriter writer, SubGraph ops) throws IOException, XMLStreamException {
        Map<String, Class> keyTypes = new HashMap<>();
        keyTypes.put("labels", String.class);
        for (CompositeObject node : ops.getNodes()) {
            updateKeyTypes(keyTypes, node);
        }
        writeKeyTypes(writer, keyTypes, "node");
        keyTypes.clear();
        for (CompositeObject rel : ops.getRelationships()) {
            updateKeyTypes(keyTypes, rel);
        }
        writeKeyTypes(writer, keyTypes, "edge");
    }

    private void writeKeyTypes(XMLStreamWriter writer, Map<String, Class> keyTypes, String forType) throws IOException, XMLStreamException {
        for (Map.Entry<String, Class> entry : keyTypes.entrySet()) {
            String type = MetaInformation.typeFor(entry.getValue(), MetaInformation.GRAPHML_ALLOWED);
            if (type == null)
                continue;
            writer.writeEmptyElement("key");
            writer.writeAttribute("id", entry.getKey());
            writer.writeAttribute("for", forType);
            writer.writeAttribute("attr.name", entry.getKey());
            writer.writeAttribute("attr.type", type);
            newLine(writer);
        }
    }

    private void updateKeyTypes(Map<String, Class> keyTypes, CompositeObject composite) {
        PropertyContainer pc = composite.getDelegate();
        updateKeyTypes(keyTypes, pc);
    }

    private void updateKeyTypes(Map<String, Class> keyTypes, PropertyContainer pc) {
        for (String prop : pc.getPropertyKeys()) {
            Object value = pc.getProperty(prop);
            Class storedClass = keyTypes.get(prop);
            if (storedClass == null) {
                keyTypes.put(prop, value.getClass());
                continue;
            }
            if (storedClass == void.class || storedClass.equals(value.getClass()))
                continue;
            keyTypes.put(prop, void.class);
        }
    }

    private void writeNode(XMLStreamWriter writer, GraphMLDecorator decorator, CompositeObject composite, boolean withEnd) throws IOException,
            XMLStreamException {
        if (decorator.isWriteNode(composite)) {
            Node node = composite.getDelegate();
            writer.writeStartElement("node");
            writer.writeAttribute("id", id(node));
            decorator.writeNodeAttributes(composite);
            writeLabels(writer, node);
            writeLabelsAsData(writer, node);
            decorator.writeNodeElements(composite);
            writeProps(writer, node);
            if (withEnd)
                endElement(writer);
        }
    }

    private String id(Node node) {
        return "n" + node.getId();
    }

    private void writeLabels(XMLStreamWriter writer, Node node) throws IOException, XMLStreamException {
        String labelsString = getLabelsString(node);
        if (!labelsString.isEmpty())
            writer.writeAttribute("labels", labelsString);
    }

    private void writeLabelsAsData(XMLStreamWriter writer, Node node) throws IOException, XMLStreamException {
        String labelsString = getLabelsString(node);
        if (labelsString.isEmpty())
            return;
        writeData(writer, "labels", labelsString);
    }

    private void writeRelationship(XMLStreamWriter writer, GraphMLDecorator decorator, CompositeObject coRel) throws IOException,
            XMLStreamException {
        Relationship rel = coRel.getDelegate();
        if (decorator.isWriteRelationship(coRel)) {
            writer.writeStartElement("edge");
            writer.writeAttribute("id", id(rel));
            writer.writeAttribute("source", id(rel.getStartNode()));
            writer.writeAttribute("target", id(rel.getEndNode()));
            writer.writeAttribute("label", rel.getType().name());
            decorator.writeRelationshipAttributes(coRel);
            writeData(writer, "label", rel.getType().name());
            decorator.writeRelationshipElements(coRel);
            writeProps(writer, rel);
            endElement(writer);
        }
    }

    private String id(Relationship rel) {
        return "e" + rel.getId();
    }

    private void endElement(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();
        newLine(writer);
    }

    private void writeProps(XMLStreamWriter writer, PropertyContainer node) throws IOException, XMLStreamException {
        int count = 0;
        for (String prop : node.getPropertyKeys()) {
            Object value = node.getProperty(prop);
            writeData(writer, prop, value);
            count++;
        }
    }

    private void writeData(XMLStreamWriter writer, String prop, Object value) throws IOException, XMLStreamException {
        writer.writeStartElement("data");
        writer.writeAttribute("key", prop);
        if (value != null)
            writer.writeCharacters(value.toString());
        writer.writeEndElement();
    }

    private void writeFooter(XMLStreamWriter writer) throws IOException, XMLStreamException {
        endElement(writer);
        writer.writeEndDocument();
    }

    private void writeHeader(XMLStreamWriter writer, GraphMLNamespaceContext context) throws IOException, XMLStreamException {
        writer.writeStartDocument("UTF-8", "1.0");
        newLine(writer);
        writer.writeStartElement("graphml");
        writer.writeNamespace("xmlns", "http://graphml.graphdrawing.org/xmlns");
        for (Map.Entry<String, String> entry : context.getNamespaces().entrySet()) {
            writer.writeAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns", entry.getKey(), entry.getValue());
        }
        if (!context.getSchemaLocations().isEmpty()) {
            StringBuilder schemaLocations = new StringBuilder();
            for (Map.Entry<String, String> entry : context.getSchemaLocations().entrySet()) {
                schemaLocations.append(entry.getKey()).append(" ").append(entry.getValue());
            }
            writer.writeAttribute("xsi", "", "schemaLocation", schemaLocations.toString());
        }
        newLine(writer);
    }

    private void newLine(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeCharacters("\n");
    }

}
