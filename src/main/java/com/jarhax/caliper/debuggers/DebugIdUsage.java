package com.jarhax.caliper.debuggers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

import com.jarhax.caliper.TableBuilder;

import net.darkhax.bookshelf.util.MathsUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class DebugIdUsage {

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private static final Map<String, LoadInfo> MODS = new HashMap<>();

    private static final String NEW_LINE = System.lineSeparator();

    public static void onLoadingComplete () {

        final TableBuilder<LoadInfo> table = new TableBuilder<>();
        table.addColumn("Mod", (info) -> info.mod);
        table.addColumn("Blocks", (info) -> info.blockIds);
        table.addColumn("Items", (info) -> info.itemIds);
        table.addColumn("Entities", (info) -> info.entityIds);
        table.addColumn("File Name", (info) -> info.sourceFile);

        long time = System.currentTimeMillis();

        // Init data
        for (final ModContainer mod : Loader.instance().getIndexedModList().values()) {

            MODS.put(mod.getModId(), new LoadInfo(mod));
        }

        // Blocks
        for (final ResourceLocation id : ForgeRegistries.BLOCKS.getKeys()) {

            final LoadInfo info = MODS.get(id.getResourceDomain());

            if (info != null) {

                info.blockIds++;
            }
        }

        // Items
        for (final ResourceLocation id : ForgeRegistries.ITEMS.getKeys()) {

            final LoadInfo info = MODS.get(id.getResourceDomain());

            if (info != null) {

                info.itemIds++;
            }
        }

        // Entities
        for (final ResourceLocation id : ForgeRegistries.ENTITIES.getKeys()) {

            final LoadInfo info = MODS.get(id.getResourceDomain());

            if (info != null) {

                info.entityIds++;
            }
        }

        time = System.currentTimeMillis() - time;

        MODS.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).forEach( (info) -> table.addEntry(info.getValue()));
        MODS.clear();

        // Prints out info
        try {

            final File file = new File("logs/caliper/id-analysis.md");
            final FileWriter writer = new FileWriter(file, false);

            writer.append("# ID Analysis - " + TIME_FORMAT.format(new Date()));
            writer.append(NEW_LINE + NEW_LINE);
            writer.append(WordUtils.wrap("This file contains an analysis on how many things are currently registered, and who is registering them. This data is anonymous, and is not automatically submitted to any online service.", 80));
            writer.append(NEW_LINE + NEW_LINE);
            writer.append("Analysis took " + MathsUtils.round(time / 1000, 2) + " seconds.");
            writer.append(NEW_LINE + NEW_LINE);
            writer.append(table.createString());

            writer.close();
        }

        catch (final IOException e) {

        }
    }

    private static class LoadInfo implements Comparable<LoadInfo> {

        public final String mod;
        public String sourceFile;

        public int blockIds;
        public int itemIds;
        public int entityIds;

        public LoadInfo (ModContainer container) {

            this.mod = container.getModId();
            this.sourceFile = container.getSource().getName();
        }

        public int getTotal () {

            return this.blockIds + this.itemIds + this.entityIds;
        }

        @Override
        public int compareTo (LoadInfo o) {

            return Integer.valueOf(this.getTotal()).compareTo(o.getTotal());
        }
    }
}
