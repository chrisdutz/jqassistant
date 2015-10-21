package com.buschmais.jqassistant.plugin.json.impl.scanner;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.plugin.json.api.model.JSONDocumentDescriptor;
import com.buschmais.jqassistant.plugin.json.api.model.JSONFileDescriptor;
import org.jqassistant.plugin.json.parser.JSONBaseListener;

public class JSONParseListener extends JSONBaseListener {

    private final JSONFileDescriptor fileDescriptor;
    private final Scanner scanner;

    public JSONParseListener(JSONFileDescriptor fd, Scanner sc) {
        fileDescriptor = fd;
        scanner = sc;
    }

    @Override
    public void enterJsonDocument(org.jqassistant.plugin.json.parser.JSONParser.JsonDocumentContext ctx) {
        JSONDocumentDescriptor documentDescriptor = scanner.getContext()
                                                           .getStore()
                                                           .create(JSONDocumentDescriptor.class);

    }
}
