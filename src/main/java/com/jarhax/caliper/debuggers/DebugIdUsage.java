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

    private static final int MAX_BLOCK_ID = 4096;
    private static final int MAX_ITEM_ID = 32000;
    private static final int MAX_POTION_ID = 256;
    private static final int MAX_BIOME_ID = 256;
    private static final int MAX_ENCHANTMENT_ID = Short.MAX_VALUE;
    private static final int MAX_ENTITY_ID = Integer.MAX_VALUE >> 5;
    private static final int MAX_RECIPE_ID = Integer.MAX_VALUE >> 5;

    private static int usedBlockId = 0;
    private static int usedItemId = 0;
    private static int usedPotionId = 0;
    private static int usedBiomeId = 0;
    private static int usedEnchantmentId = 0;
    private static int usedEntityId = 0;
    private static int usedRecipeId = 0;

    public static void onLoadingComplete () {

        final TableBuilder<LoadInfo> table = makeTable(false);

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

        final TableBuilder<LoadInfo> global = makeTable(true);
        global.addEntry(LoadInfo.getMaximums());
        global.addEntry(LoadInfo.getAvailable());
        global.addEntry(LoadInfo.getUsed());

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
            writer.append(global.createString());
            writer.append(NEW_LINE + NEW_LINE);
            writer.append(table.createString());

            writer.close();
        }

        catch (final IOException e) {

        }
    }

    private static TableBuilder<LoadInfo> makeTable (boolean isGlobal) {

        final TableBuilder<LoadInfo> table = new TableBuilder<>();
        table.addColumn("Mod", (info) -> info.mod);
        table.addColumn("Blocks", (info) -> info.blockIds);
        table.addColumn("Items", (info) -> info.itemIds);
        table.addColumn("Entities", (info) -> info.entityIds);

        if (!isGlobal) {

            table.addColumn("File Name", (info) -> info.sourceFile);
        }

        return table;
    }

    private static class LoadInfo implements Comparable<LoadInfo> {

        public String mod;
        public String sourceFile;

        public int blockIds;
        public int itemIds;
        public int entityIds;

        public int total = -5000;

        public LoadInfo () {

        }

        public LoadInfo (ModContainer container) {

            this.mod = container.getModId();
            this.sourceFile = container.getSource().getName();
        }

        public int getTotal () {

            if (this.total < 0) {

                this.total = this.blockIds + this.itemIds + this.entityIds;

                usedBlockId += this.blockIds;
                usedItemId += this.itemIds;
                usedEntityId += this.entityIds;
            }

            return this.total;
        }

        @Override
        public int compareTo (LoadInfo o) {

            return Integer.valueOf(this.getTotal()).compareTo(o.getTotal());
        }

        public static LoadInfo getMaximums () {

            final LoadInfo info = new LoadInfo();

            info.mod = "Maximum";

            info.blockIds = MAX_BLOCK_ID;
            info.itemIds = MAX_ITEM_ID;
            info.entityIds = MAX_ENTITY_ID;

            return info;
        }

        public static LoadInfo getUsed () {

            final LoadInfo info = new LoadInfo();

            info.mod = "Used";

            info.blockIds = usedBlockId;
            info.itemIds = usedItemId;
            info.entityIds = usedEntityId;

            return info;
        }

        public static LoadInfo getAvailable () {

            final LoadInfo info = new LoadInfo();

            info.mod = "Available";

            info.blockIds = MAX_BLOCK_ID - usedBlockId;
            info.itemIds = MAX_ITEM_ID - usedItemId;
            info.entityIds = MAX_ENTITY_ID - usedEntityId;

            return info;
        }
    }
}
