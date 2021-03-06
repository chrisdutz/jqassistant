package com.buschmais.jqassistant.core.plugin.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.buschmais.jqassistant.core.plugin.api.PluginConfigurationReader;
import com.buschmais.jqassistant.core.plugin.api.PluginRepositoryException;
import com.buschmais.jqassistant.core.plugin.api.ReportPluginRepository;
import com.buschmais.jqassistant.core.plugin.schema.v1.JqassistantPlugin;
import com.buschmais.jqassistant.core.plugin.schema.v1.ReportType;
import com.buschmais.jqassistant.core.report.api.ReportException;
import com.buschmais.jqassistant.core.report.api.ReportPlugin;

/**
 * Report plugin repository implementation.
 */
public class ReportPluginRepositoryImpl extends AbstractPluginRepository implements ReportPluginRepository {

    private final List<ReportPlugin> reportPlugins;

    /**
     * Constructor.
     */
    public ReportPluginRepositoryImpl(PluginConfigurationReader pluginConfigurationReader) throws PluginRepositoryException {
        super(pluginConfigurationReader);
        List<JqassistantPlugin> plugins = pluginConfigurationReader.getPlugins();
        this.reportPlugins = getReportPlugins(plugins);
    }

    @Override
    public List<ReportPlugin> getReportPlugins(Map<String, Object> properties) throws PluginRepositoryException {
        for (ReportPlugin reportPlugin : reportPlugins) {
            try {
                reportPlugin.configure(properties);
            } catch (ReportException e) {
                throw new PluginRepositoryException("Cannot configure report plugin " + reportPlugin, e);
            }
        }
        return reportPlugins;
    }

    private List<ReportPlugin> getReportPlugins(List<JqassistantPlugin> plugins) throws PluginRepositoryException {
        List<ReportPlugin> reportPlugins = new ArrayList<>();
        for (JqassistantPlugin plugin : plugins) {
            ReportType reportType = plugin.getReport();
            if (reportType != null) {
                for (String reportPluginName : reportType.getClazz()) {
                    ReportPlugin reportPlugin = createInstance(reportPluginName);
                    if (reportPlugin != null) {
                        try {
                            reportPlugin.initialize();
                        } catch (ReportException e) {
                            throw new PluginRepositoryException("Cannot initialize report plugin " + reportPlugin, e);
                        }
                        reportPlugins.add(reportPlugin);
                    }
                }
            }
        }
        return reportPlugins;
    }

}
