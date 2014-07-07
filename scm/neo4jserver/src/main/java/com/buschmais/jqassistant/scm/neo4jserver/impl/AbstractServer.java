package com.buschmais.jqassistant.scm.neo4jserver.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServer;
import org.neo4j.server.database.InjectableProvider;
import org.neo4j.server.modules.ServerModule;

import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.jqassistant.core.store.impl.EmbeddedGraphStore;
import com.buschmais.jqassistant.scm.neo4jserver.api.Server;
import com.buschmais.jqassistant.scm.neo4jserver.impl.rest.AnalysisRestService;

/**
 * Abstract base class for the customized Neo4j server.
 * <p>
 * The class adds the {@link JQAServerModule}
 * </p>
 */
public abstract class AbstractServer extends WrappingNeoServer implements Server {
    protected final Store store;

    public AbstractServer(GraphDatabaseAPI db, EmbeddedGraphStore graphStore) {
        super(db);
        this.store = graphStore;
    }

    @Override
    protected Iterable<ServerModule> createServerModules() {
        List<String> extensionNames = new ArrayList<>();
        extensionNames.add(AnalysisRestService.class.getName());
        for (Class<?> extension : getExtensions()) {
            extensionNames.add(extension.getName());
        }
        List<ServerModule> serverModules = new ArrayList<>();
        serverModules.add(new JQAServerModule(webServer, extensionNames));
        for (ServerModule serverModule : super.createServerModules()) {
            serverModules.add(serverModule);
        }
        return serverModules;
    }

    @Override
    protected Collection<InjectableProvider<?>> createDefaultInjectables() {
        Collection<InjectableProvider<?>> defaultInjectables = super.createDefaultInjectables();
        defaultInjectables.add(new StoreProvider(store));
        return defaultInjectables;
    }

    /**
     * Return the extension classes that shall be included.
     * 
     * @return The extension classes.
     */
    protected abstract Iterable<? extends Class<?>> getExtensions();
}