package net.darkhax.caliper.profiling.profilers.errors.reporters;

import java.util.StringJoiner;

import net.darkhax.bookshelf.util.ModUtils;
import net.darkhax.caliper.FileHelper;
import net.darkhax.caliper.profiling.profilers.errors.ErrorReporter;
import net.darkhax.caliper.profiling.profilers.errors.Level;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ReportBadMobSpawns extends ErrorReporter {

    private final StringJoiner joiner = new StringJoiner(FileHelper.NEW_LINE);

    private int errorCount = 0;

    @Override
    public String getErrors () {

        return this.joiner.toString();
    }

    @Override
    public String getDescription () {

        return "Mob spawn entries have range values which are used to calculate how many mobs to spawn. It is possible for the maximum value to be larger than the minimum value. Those entries are invalid and will cause a crash if the game tries to spawn the mob!";
    }

    @Override
    public void collectErrors () {

        for (final Biome biome : ForgeRegistries.BIOMES) {

            for (final EnumCreatureType type : EnumCreatureType.values()) {

                for (final Biome.SpawnListEntry spawn : biome.getSpawnableList(type)) {

                    if (1 + spawn.maxGroupCount - spawn.minGroupCount <= 0) {

                        final EntityRegistration entityInfo = ModUtils.getRegistryInfo(spawn.entityClass);
                        this.joiner.add(String.format("Entity: %s Biome: %s Min: %d Max: %d", entityInfo.getRegistryName().toString(), biome.getRegistryName().toString(), spawn.minGroupCount, spawn.maxGroupCount));
                        this.errorCount++;
                    }
                }
            }
        }
    }

    @Override
    public Level getErrorLevel () {

        return Level.FATAL;
    }

    @Override
    public boolean foundErrors () {

        return this.errorCount > 0;
    }

    @Override
    public String getName () {

        return "Invalid Mob Spawn Ranges";
    }
}