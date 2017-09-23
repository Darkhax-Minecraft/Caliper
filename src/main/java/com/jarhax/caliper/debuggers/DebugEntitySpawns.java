package com.jarhax.caliper.debuggers;

import java.util.List;

import com.jarhax.caliper.Caliper;

import net.darkhax.bookshelf.util.ModUtils;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.NonNullList;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;
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
                		
                		final EntityRegistration entityInfo = ModUtils.getRegistryInfo(spawn.entityClass);
                		
                		if (entityInfo != null) {
                			
                			info.add("Entity ID: " + entityInfo.getRegistryName().toString());
                		}
                		
                		info.add("Entity Class: " + spawn.entityClass.getName());
                		
                		Caliper.LOG.noticableWarning(false, info);
                	}
                }
            }
        }
	}
}
