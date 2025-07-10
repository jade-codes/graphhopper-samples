package com.graphhopper.samples.cli;

import com.graphhopper.samples.CustomImportRegistry;

import com.graphhopper.application.GraphHopperServerConfiguration;
import com.graphhopper.http.GraphHopperManaged;
import io.dropwizard.core.cli.ConfiguredCommand;
import io.dropwizard.core.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;

public class CustomImportCommand extends ConfiguredCommand<GraphHopperServerConfiguration> {

    public CustomImportCommand() {
        super("import", "creates the graphhopper files used for later (faster) starts");
    }

    @Override
    protected void run(Bootstrap<GraphHopperServerConfiguration> bootstrap, Namespace namespace, GraphHopperServerConfiguration configuration) {
        final GraphHopperManaged graphHopper = new GraphHopperManaged(configuration.getGraphHopperConfiguration());
        graphHopper.getGraphHopper().setImportRegistry(new CustomImportRegistry());
        graphHopper.getGraphHopper().importAndClose();
    }

}