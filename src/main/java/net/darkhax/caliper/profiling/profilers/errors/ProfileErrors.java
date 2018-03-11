package net.darkhax.caliper.profiling.profilers.errors;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.darkhax.caliper.Caliper;
import net.darkhax.caliper.FileHelper;
import net.darkhax.caliper.profiling.Profiler;
import net.darkhax.caliper.profiling.RegisterProfiler;
import net.darkhax.caliper.profiling.profilers.errors.reporters.ReportAirRecipe;
import net.darkhax.caliper.profiling.profilers.errors.reporters.ReportBadMobSpawns;
import net.darkhax.caliper.profiling.profilers.errors.reporters.ReportBadVersion;
import net.darkhax.caliper.profiling.profilers.errors.reporters.ReportExampleMod;
import net.darkhax.caliper.profiling.profilers.errors.reporters.ReportMissingEnchantmentDescriptions;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@RegisterProfiler(name = "Errors", description = "Finds errors in the mods for the packs that should be fixed.")
public class ProfileErrors extends Profiler {

    private final File file = new File(Caliper.LOG_DIR, "errors.md");
    private final List<ErrorReporter> errorReporters = new ArrayList<>();

    @Override
    public void onPreInit () {

        this.errorReporters.add(new ReportBadMobSpawns());
        this.errorReporters.add(new ReportAirRecipe());
        this.errorReporters.add(new ReportBadVersion());
        this.errorReporters.add(new ReportExampleMod());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onPreInitClient () {

        this.errorReporters.add(new ReportMissingEnchantmentDescriptions());
    }

    @Override
    public void onLoadComplete () {

        this.errorReporters.forEach(ErrorReporter::collectErrors);
        Collections.sort(this.errorReporters);

        try (final FileWriter writer = new FileWriter(this.file)) {

            FileHelper.writeInfoBlock(writer, 0, "Error Reports", "This file contains errors that were found with the pack. If there are no errors listed below, none were detected.", true);

            for (final ErrorReporter reporter : this.errorReporters) {

                if (reporter.foundErrors()) {

                    FileHelper.writeInfoBlock(writer, 1, reporter.getName(), reporter.getDescription());
                    writer.write(reporter.getErrors());
                }
            }
        }

        catch (final IOException e) {

            Caliper.LOG.catching(e);
        }
    }
}