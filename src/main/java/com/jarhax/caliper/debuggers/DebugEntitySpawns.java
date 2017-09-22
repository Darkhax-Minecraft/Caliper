package com.jarhax.caliper.debuggers;

import java.util.List;

import com.jarhax.caliper.MiscUtils;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.NonNullList;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class DebugEntitySpawns {

	public static void debug() {
				
        for (Biome biome : ForgeRegistries.BIOMES) {
        	
            for (EnumCreatureType type : EnumCreatureType.values()) {
                
                for (Biome.SpawnListEntry spawn : biome.getSpawnableList(type)) {
                	
                	if (1 + spawn.maxGroupCount - spawn.minGroupCount <= 0) {

                		final List<String> info = NonNullList.create();
                		
                		info.add("Caliper detected an error with a spawn entry. The max group spawn count must be larger than the min group count! This is an error with one of your mods, or pack configuration. Do NOT report it to the caliper devs.");
                		info.add("Biome: " + biome.getRegistryName().toString());
                		info.add("Spawn Groups - Min: " + spawn.minGroupCount + " Max: " + spawn.maxGroupCount);
                		
                		final EntityEntry entityInfo = MiscUtils.getEntityEntry(spawn.entityClass);
                		
                		if (entityInfo != null) {
                			
                			info.add("Entity ID: " + entityInfo.getRegistryName().toString());
                		}
                		
                		info.add("Entity Class: " + spawn.entityClass.getName());
                		
                		MiscUtils.bigWarning(false, info);
                	}
                }
            }
        }
	}
}
