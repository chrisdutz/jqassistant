package com.buschmais.jqassistant.plugin.json.impl.scanner;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import com.buschmais.jqassistant.plugin.json.api.model.JSONDocumentDescriptor;
import com.buschmais.jqassistant.plugin.json.api.model.JSONFileDescriptor;
import com.buschmais.jqassistant.plugin.json.api.model.JSONKeyDescriptor;
import com.buschmais.jqassistant.plugin.json.api.model.JSONObjectDescriptor;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsEmptyCollection.empty;

public class JSONFileScannerPluginIT extends AbstractPluginIT {

    @Before
    public void startTransaction() {
        store.beginTransaction();
    }

    @After
    public void commitTransaction() {
        store.commitTransaction();
    }


    @Test
    public void scanReturnsFileDescriptorWithCorrectFileName() {
        File jsonFile = new File(getClassesDirectory(JSONFileScannerPluginIT.class),
                                 "/probes/valid/true-false-null.json");

        Scanner scanner = getScanner();
        JSONFileDescriptor file = scanner.scan(jsonFile, jsonFile.getAbsolutePath(), null);

        assertThat("Scanner must be able to scan the resource and to return a descriptor.",
                   file, notNullValue());

        assertThat(file.getFileName(), Matchers.notNullValue());
        assertThat(file.getFileName(), endsWith("probes/valid/true-false-null.json"));
    }

    @Test
    public void scanReturnsObjectWithOneKeyValuePair() {
        File jsonFile = new File(getClassesDirectory(JSONFileScannerPluginIT.class),
                                 "/probes/valid/object-one-key-value-pair.json");

        Scanner scanner = getScanner();
        JSONFileDescriptor file = scanner.scan(jsonFile, jsonFile.getAbsolutePath(), null);

        assertThat("Scanner must be able to scan the resource and to return a descriptor.",
                   file, notNullValue());

        assertThat(file.getFileName(), Matchers.notNullValue());
        assertThat(file.getFileName(), endsWith("probes/valid/object-one-key-value-pair.json"));

        assertThat(file.getDocument(), Matchers.notNullValue());

        JSONDocumentDescriptor document = file.getDocument();

        assertThat(document.getContainer(), Matchers.notNullValue());

        JSONObjectDescriptor jsonObject = (JSONObjectDescriptor) document.getContainer();

        assertThat(jsonObject.getKeys(), hasSize(1));

        JSONKeyDescriptor keyDescriptor = jsonObject.getKeys().get(0);

        assertThat(keyDescriptor.getName(), CoreMatchers.equalTo("A"));
        assertThat(keyDescriptor.getValue(), notNullValue());
        assertThat(keyDescriptor.getValue().getValue(), IsEqual.<Object>equalTo("B"));
    }

    @Test
    public void scanReturnsObjectWithTwoKeyValuePairs() {
        File jsonFile = new File(getClassesDirectory(JSONFileScannerPluginIT.class),
                                 "/probes/valid/object-two-key-value-pairs.json");

        Scanner scanner = getScanner();
        JSONFileDescriptor file = scanner.scan(jsonFile, jsonFile.getAbsolutePath(), null);

        assertThat("Scanner must be able to scan the resource and to return a descriptor.",
                   file, notNullValue());

        assertThat(file.getFileName(), Matchers.notNullValue());
        assertThat(file.getFileName(), endsWith("probes/valid/object-two-key-value-pairs.json"));

        assertThat(file.getDocument(), Matchers.notNullValue());

        JSONDocumentDescriptor document = file.getDocument();

        assertThat(document.getContainer(), Matchers.notNullValue());

        JSONObjectDescriptor jsonObject = (JSONObjectDescriptor) document.getContainer();

        assertThat(jsonObject.getKeys(), hasSize(2));

        JSONKeyDescriptor keyDescriptorA = findKeyInDocument(jsonObject.getKeys(), "A");
        JSONKeyDescriptor keyDescriptorB = findKeyInDocument(jsonObject.getKeys(), "C");

        assertThat(keyDescriptorA.getName(), CoreMatchers.equalTo("A"));
        assertThat(keyDescriptorA.getValue(), Matchers.notNullValue());
        assertThat(keyDescriptorA.getValue().getValue(), Matchers.<Object>equalTo("B"));
        assertThat(keyDescriptorB.getName(), CoreMatchers.equalTo("C"));
        assertThat(keyDescriptorB.getValue().getValue(), Matchers.<Object>equalTo("D"));
    }

    @Test
    public void scanReturnsObjectWithTrueFalseAndNullValue() {
        File jsonFile = new File(getClassesDirectory(JSONFileScannerPluginIT.class),
                                 "/probes/valid/true-false-null.json");

        Scanner scanner = getScanner();
        JSONFileDescriptor file = scanner.scan(jsonFile, jsonFile.getAbsolutePath(), null);

        assertThat("Scanner must be able to scan the resource and to return a descriptor.",
                   file, notNullValue());

        assertThat(file.getFileName(), Matchers.notNullValue());
        assertThat(file.getFileName(), endsWith("probes/valid/true-false-null.json"));

        assertThat(file.getDocument(), Matchers.notNullValue());

        JSONDocumentDescriptor document = file.getDocument();

        assertThat(document.getContainer(), Matchers.notNullValue());

        JSONObjectDescriptor jsonObject = (JSONObjectDescriptor) document.getContainer();

        assertThat(jsonObject.getKeys(), hasSize(3));

        JSONKeyDescriptor keyDescriptorA = findKeyInDocument(jsonObject.getKeys(), "A");
        JSONKeyDescriptor keyDescriptorB = findKeyInDocument(jsonObject.getKeys(), "B");
        JSONKeyDescriptor keyDescriptorC = findKeyInDocument(jsonObject.getKeys(), "C");

        assertThat(keyDescriptorA.getName(), CoreMatchers.equalTo("A"));
        assertThat(keyDescriptorA.getValue(), Matchers.notNullValue());
        assertThat(keyDescriptorA.getValue().getValue(), Matchers.<Object>equalTo(Boolean.TRUE));

        assertThat(keyDescriptorB.getName(), CoreMatchers.equalTo("B"));
        assertThat(keyDescriptorB.getValue(), Matchers.notNullValue());
        assertThat(keyDescriptorB.getValue().getValue(), Matchers.<Object>equalTo(Boolean.FALSE));

        assertThat(keyDescriptorC.getName(), CoreMatchers.equalTo("C"));
        assertThat(keyDescriptorC.getValue(), Matchers.nullValue());
    }

    private JSONKeyDescriptor findKeyInDocument(List<JSONKeyDescriptor> keys, String name) {
        JSONKeyDescriptor result = null;

        for (JSONKeyDescriptor key : keys) {
            if (key.getName().equals(name)) {
                result = key;
                break;
            }
        }

        return result;
    }
}