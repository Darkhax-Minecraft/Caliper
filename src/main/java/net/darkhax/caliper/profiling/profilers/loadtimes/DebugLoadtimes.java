package net.darkhax.caliper.profiling.profilers.loadtimes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.darkhax.bookshelf.lib.TableBuilder;
import net.darkhax.bookshelf.util.MathsUtils;
import net.darkhax.caliper.Caliper;
import net.darkhax.caliper.FileHelper;
import net.darkhax.caliper.profiling.Profiler;
import net.darkhax.caliper.profiling.RegisterProfiler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

@RegisterProfiler(name = "Load Times", description = "Digs through registered forge events to dump usage info.")
public class DebugLoadtimes extends Profiler {

    private static final Map<String, LoadInfo> MODS = new HashMap<>();

    public static int signedMods = 0;

    @Override
    public void onConstructed () {

        ((org.apache.logging.log4j.core.Logger) FMLLog.log).addFilter(new LogListener());
    }

    @Override
    public void onLoadComplete () {

        final TableBuilder<LoadInfo> table = LoadInfo.createDataTable();

        // Adds info from Markdown tables
        for (final ModContainer mod : Loader.instance().getIndexedModList().values()) {

            getLoadInfo(mod.getName()).setContainerInfo(mod);
        }

        // Sorts all the entries, and adds them to the table builder.
        MODS.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).forEach(info -> table.addEntry(info.getValue()));

        final File file = new File(Caliper.LOG_DIR, "load-times.md");

        // Prints out info
        try (final FileWriter writer = new FileWriter(file, false)) {

            FileHelper.writeInfoBlock(writer, 0, "Load Time Info", "This file contains an analysis of mod load times. If you are using the vanilla (or twitch) launcher, this file may show 0.0s for all the mods due to a bug in that launcher.", true);
            writer.append(FileHelper.NEW_PARAGRAPH);
            writer.append("Signed Mods: " + signedMods + " (" + MathsUtils.round((double) signedMods / (double) Loader.instance().getActiveModList().size(), 2) * 100d + "%)");
            writer.append(FileHelper.NEW_PARAGRAPH);
            writer.append(table.createString());
        }

        catch (final IOException e) {

            Caliper.LOG.catching(e);
        }
    }

    protected static LoadInfo getLoadInfo (String mod) {

        final LoadInfo info = MODS.getOrDefault(mod, new LoadInfo(mod));
        MODS.put(mod, info);
        return info;
    }
}
