package com.buschmais.jqassistant.scm.cli.task;

import org.apache.commons.cli.CommandLine;

import com.buschmais.jqassistant.core.store.api.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jn4, Kontext E GmbH, 24.01.14
 */
public class ResetTask extends AbstractTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResetTask.class);

    @Override
    protected void executeTask(final Store store) {
        LOGGER.info("Resetting store.");
        store.reset();
    }

    @Override
    public void withOptions(CommandLine options) {
    }
}
