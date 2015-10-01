package com.buschmais.jqassistant.plugin.json.impl.scanner;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import com.buschmais.jqassistant.plugin.json.api.model.JSONFileDescriptor;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
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


//    @Test
//    public void scanSimpleKeyValuePairYAML() {
//        File yamlFile = new File(getClassesDirectory(YAMLFileScannerPluginValidFileSetIT.class),
//                                 "/probes/valid/simple-key-value-pair.yaml");
//
//        getScanner().scan(yamlFile, yamlFile.getAbsolutePath(), null);
//
//        List<YAMLFileDescriptor> fileDescriptors =
//             query("MATCH (f:YAML:File) WHERE f.fileName=~'.*/probes/valid/simple-key-value-pair.yaml' RETURN f")
//                .getColumn("f");
//
//        assertThat(fileDescriptors, hasSize(1));
//
//        YAMLFileDescriptor file = fileDescriptors.get(0);
//        assertThat(file.getDocuments(), hasSize(1));
//
//        YAMLDocumentDescriptor document = file.getDocuments().get(0);
//
//        assertThat(document.getValues(), hasSize(0));
//        assertThat(document.getKeys(), hasSize(1));
//
//        YAMLKeyDescriptor key = findKeyByName(document.getKeys(), "key");
//
//        assertThat(key.getName(), equalTo("key"));
//        assertThat(key.getFullQualifiedName(), equalTo("key"));
//        assertThat(key.getValues(), hasSize(1));
//        assertThat(key.getPosition(), equalTo(0));
//
//        YAMLValueDescriptor value = findValueByValue(key.getValues(), "value");
//
//        assertThat(value.getValue(), equalTo("value"));
//        assertThat(value.getPosition(), equalTo(0));
//    }


}