package net.darkhax.caliper.profiling.profilers.registry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;

import com.google.common.collect.Multimap;

import net.darkhax.bookshelf.lib.TableBuilder;
import net.darkhax.bookshelf.util.ModUtils;
import net.darkhax.caliper.Caliper;
import net.darkhax.caliper.FileHelper;
import net.darkhax.caliper.profiling.Profiler;
import net.darkhax.caliper.profiling.RegisterProfiler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

@RegisterProfiler(name = "Registry Analysis", description = "Collects info about the various registries.")
public class ProfileRegistries extends Profiler {

    private final File registryDir = new File(Caliper.LOG_DIR, "registry");

    @Override
    public void onPreInit () {

        // Creates registry directory if it doesn't exist.
        if (!this.registryDir.exists()) {

            this.registryDir.mkdirs();
        }
    }

    @Override
    public void onLoadComplete () {

        this.profileRegistry(ForgeRegistries.BLOCKS, "block", 4096);
        this.profileRegistry(ForgeRegistries.ITEMS, "item", 32000);
        this.profileRegistry(ForgeRegistries.POTIONS, "potion", 256);
        this.profileRegistry(ForgeRegistries.BIOMES, "biome", 256);
        this.profileRegistry(ForgeRegistries.RECIPES, "recipes", Integer.MAX_VALUE >> 5);
        this.profileRegistry(ForgeRegistries.ENTITIES, "entity", Integer.MAX_VALUE >> 5);
        this.profileRegistry(ForgeRegistries.ENCHANTMENTS, "enchantment", Short.MAX_VALUE - 1);
    }

    private void profileRegistry (IForgeRegistry<?> registry, String name, int max) {

        int remaining = max;
        try (final FileWriter writer = new FileWriter(new File(this.registryDir, name + ".md"), false)) {

            final TableBuilder<RegistryInfo> table = RegistryInfo.createDataTable();
            final Multimap<String, ?> sortedRegistry = ModUtils.getSortedEntries(registry);

            for (final String modId : sortedRegistry.keySet()) {

                final int count = sortedRegistry.get(modId).size();
                remaining -= count;
                table.addEntry(new RegistryInfo(modId, count, (float) count / max * 100f));
            }

            Collections.sort(table.getEntries(), Collections.reverseOrder());

            FileHelper.writeInfoBlock(writer, 1, name + " Registry Analysis", String.format("This file contains information about how mods are using the %s registry. %d out of %d ids available. %.3f%% of this registry is still available.", name, remaining, max, (float) remaining / max * 100f), true);

            writer.append(FileHelper.NEW_LINE);
            writer.append(table.createString());
        }

        catch (final IOException e) {

            Caliper.LOG.catching(e);
        }
    }
}