package net.darkhax.caliper.profiling.profilers.crt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import crafttweaker.CraftTweakerAPI;
import net.darkhax.bookshelf.lib.TableBuilder;
import net.darkhax.caliper.Caliper;
import net.darkhax.caliper.FileHelper;
import net.darkhax.caliper.profiling.Profiler;
import net.darkhax.caliper.profiling.RegisterProfiler;
import net.darkhax.caliper.profiling.profilers.InfoPair;

@RegisterProfiler(name = "CraftTweaker Analysis", description = "Profiles CraftTweaker and scripts that use it.")
public class ProfileCraftTweaker extends Profiler {

    private final Map<String, Long> start = new HashMap<>();
    private final Map<String, Long> stop = new HashMap<>();

    @Override
    public void onPreInit () {

        CraftTweakerAPI.tweaker.registerScriptLoadPreEvent(e -> this.preScript(e.getFileName()));
        CraftTweakerAPI.tweaker.registerScriptLoadPostEvent(e -> this.postScript(e.getFileName()));
    }

    private void preScript (String name) {

        this.start.put(name, System.currentTimeMillis());
    }

    private void postScript (String name) {

        this.stop.put(name, System.currentTimeMillis());
    }

    @Override
    public void onLoadComplete () {

        final TableBuilder<InfoPair> builder = InfoPair.createDataTable("Script Name", "Time", "ms");

        for (final Entry<String, Long> entry : this.start.entrySet()) {

            builder.addEntry(new InfoPair(entry.getKey(), entry.getValue() - this.stop.get(entry.getKey())));
        }

        Collections.sort(builder.getEntries(), Collections.reverseOrder());

        final File file = new File(Caliper.LOG_DIR, "crafttweaker.md");

        try (final FileWriter writer = new FileWriter(file, false)) {

            FileHelper.writeInfoBlock(writer, 1, "CraftTweaker Analysis", "This file contains info about the various CraftTweaker scripts that are loaded. While this information can be used to find things that can be optimized, it should never used used for a witch hunt.", true);
            writer.append(builder.createString());
        }

        catch (final IOException e) {

            Caliper.LOG.catching(e);
        }
    }
}