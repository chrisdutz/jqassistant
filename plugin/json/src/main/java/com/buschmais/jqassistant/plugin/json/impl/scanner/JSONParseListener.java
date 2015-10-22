package com.buschmais.jqassistant.plugin.json.impl.scanner;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.plugin.json.api.model.JSONArrayDescriptor;
import com.buschmais.jqassistant.plugin.json.api.model.JSONContainer;
import com.buschmais.jqassistant.plugin.json.api.model.JSONDescriptor;
import com.buschmais.jqassistant.plugin.json.api.model.JSONDocumentDescriptor;
import com.buschmais.jqassistant.plugin.json.api.model.JSONFileDescriptor;
import com.buschmais.jqassistant.plugin.json.api.model.JSONKeyDescriptor;
import com.buschmais.jqassistant.plugin.json.api.model.JSONObjectDescriptor;
import com.buschmais.jqassistant.plugin.json.api.model.JSONValueDescriptor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang.NotImplementedException;
import org.jqassistant.plugin.json.parser.JSONBaseListener;

import java.util.Stack;

public class JSONParseListener extends JSONBaseListener {

    private final Scanner scanner;
    private final Stack<JSONDescriptor> descriptorStack = new Stack<>();

    public JSONParseListener(JSONFileDescriptor fd, Scanner sc) {
        scanner = sc;

        stack().push(fd);
    }

    protected Stack<JSONDescriptor> stack() {
        return descriptorStack;
    }

    @Override
    public void enterJsonDocument(org.jqassistant.plugin.json.parser.JSONParser.JsonDocumentContext ctx) {
        JSONDocumentDescriptor descriptor = scanner.getContext().getStore()
                                                   .create(JSONDocumentDescriptor.class);

        stack().push(descriptor);
    }

    @Override
    public void exitJsonDocument(org.jqassistant.plugin.json.parser.JSONParser.JsonDocumentContext ctx) {
        JSONDocumentDescriptor documentDescriptor = stack().pop().as(JSONDocumentDescriptor.class);
        JSONFileDescriptor fileDescriptor = stack().pop().as(JSONFileDescriptor.class);

        fileDescriptor.setDocument(documentDescriptor);
    }

    @Override
    public void enterJsonObject(org.jqassistant.plugin.json.parser.JSONParser.JsonObjectContext ctx) {
        JSONObjectDescriptor jsonObjectDescriptor = scanner.getContext()
                                                           .getStore()
                                                           .create(JSONObjectDescriptor.class);

        JSONDocumentDescriptor documentDescriptor = stack().peek().as(JSONDocumentDescriptor.class);
        documentDescriptor.setContainer(jsonObjectDescriptor);
        stack().push(jsonObjectDescriptor);
    }

    @Override
    public void exitJsonObject(org.jqassistant.plugin.json.parser.JSONParser.JsonObjectContext ctx) {
        stack().pop();
    }

    @Override
    public void enterKeyValuePair(org.jqassistant.plugin.json.parser.JSONParser.KeyValuePairContext ctx) {
        JSONKeyDescriptor keyDescriptor = scanner.getContext()
                                                 .getStore()
                                                 .create(JSONKeyDescriptor.class);

        JSONObjectDescriptor jsonContainer = stack().peek().as(JSONObjectDescriptor.class);
        jsonContainer.getKeys().add(keyDescriptor);
        stack().push(keyDescriptor);
    }

    @Override
    public void exitKeyValuePair(org.jqassistant.plugin.json.parser.JSONParser.KeyValuePairContext ctx) {
        String keyName = ctx.STRING().getText();

        JSONKeyDescriptor keyDescriptor = stack().pop().as(JSONKeyDescriptor.class);
        keyDescriptor.setName(keyName);
    }

    @Override
    public void enterJsonArray(org.jqassistant.plugin.json.parser.JSONParser.JsonArrayContext ctx) {
        JSONArrayDescriptor jsonArrayDescriptor = scanner.getContext()
                                                         .getStore()
                                                         .create(JSONArrayDescriptor.class);

        JSONDocumentDescriptor documentDescriptor = stack().peek().as(JSONDocumentDescriptor.class);
        documentDescriptor.setContainer(jsonArrayDescriptor);
        stack().push(jsonArrayDescriptor);
    }

    @Override
    public void exitJsonArray(org.jqassistant.plugin.json.parser.JSONParser.JsonArrayContext ctx) {
        stack().pop();
    }

    @Override
    public void enterValue(org.jqassistant.plugin.json.parser.JSONParser.ValueContext ctx) {
        JSONValueDescriptor valueDescriptor = scanner.getContext().getStore().create(JSONValueDescriptor.class);

        JSONDescriptor keyDescriptor = stack().peek().as(JSONDescriptor.class);

        if (keyDescriptor instanceof JSONKeyDescriptor) {
            ((JSONKeyDescriptor)keyDescriptor).setValue(valueDescriptor);
        } else {
            ((JSONArrayDescriptor)keyDescriptor).getValues().add(valueDescriptor);
        }

        stack().push(valueDescriptor);
    }

    @Override
    public void exitValue(org.jqassistant.plugin.json.parser.JSONParser.ValueContext ctx) {

        // There might be a better way to figure out what kind of value we have.
        // Feel free to improved it! Oliver B. Fischer, 2015-10-22
        JSONValueDescriptor valueDescriptor = stack().pop().as(JSONValueDescriptor.class);

        Object s1 = ctx.getStart().getText();
        TerminalNode stringNode = ctx.STRING();
        TerminalNode numberValue = ctx.NUMBER();
        TerminalNode booleanNode = ctx.BOOLEAN();
        TerminalNode nullNode = ctx.NULL();

        if (stringNode != null) {
            String stringValue = stringNode.getText();
            valueDescriptor.setValue(stringValue);
        } else if (booleanNode != null) {
            valueDescriptor.setValue(Boolean.parseBoolean(booleanNode.getSymbol().getText()));
        } else if (nullNode != null) {
            // If there is no value we do not need a value descriptor at all
            JSONKeyDescriptor keyDescriptor = stack().peek().as(JSONKeyDescriptor.class);
            keyDescriptor.setValue(null);
        } else {
            throw new IllegalStateException("Unable to handle the value assigned to a JSON key.");
        }
    }
}
