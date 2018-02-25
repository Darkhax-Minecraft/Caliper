package com.jarhax.caliper.profiling.profilers.crt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jarhax.caliper.Caliper;
import com.jarhax.caliper.FileHelper;
import com.jarhax.caliper.profiling.Profiler;
import com.jarhax.caliper.profiling.RegisterProfiler;
import com.jarhax.caliper.profiling.profilers.LoadTime;

import crafttweaker.CraftTweakerAPI;
import net.darkhax.bookshelf.lib.TableBuilder;

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

        final TableBuilder<LoadTime> builder = LoadTime.createDataTable();

        for (final Entry<String, Long> entry : this.start.entrySet()) {

            builder.addEntry(new LoadTime(entry.getKey(), entry.getValue(), this.stop.get(entry.getKey())));
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