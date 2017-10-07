package com.jarhax.caliper.debuggers;

import java.io.File;

import com.google.common.collect.ListMultimap;
import com.jarhax.caliper.TableBuilder;

import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class DebugRegistryDump {
    
    public static void onLoadingComplete () {
        
        TableBuilder<EntityRegistration> table = new TableBuilder<>();
        table.addColumn("Entity ID", (reg) -> reg.getRegistryName().toString());
        table.addColumn("Local ID", (reg) -> Integer.toString(reg.getModEntityId()));
        table.addColumn("Track Range", (reg) -> Integer.toString(reg.getTrackingRange()));
        table.addColumn("Update Freq", (reg) -> Integer.toString(reg.getUpdateFrequency()));
        table.addColumn("Velocity Updates", (reg) -> Boolean.toString(reg.sendsVelocityUpdates()));
        table.addColumn("Custom Spawning", (reg) -> Boolean.toString(reg.usesVanillaSpawning()));
        table.addColumn("Class", (reg) -> reg.getEntityClass().getName());
        table.addColumn("Source", (reg) -> getFileName(reg.getContainer().getSource()));
        
        ListMultimap<ModContainer, EntityRegistration> registry = ReflectionHelper.getPrivateValue(EntityRegistry.class, EntityRegistry.instance(), "entityRegistrations");
        
        for (EntityRegistration reg : registry.values()) {
            
            table.addEntry(reg);
        }
    }
    
    public static String getFileName(File file) {
        
        return file != null ? file.getName() : "unknown";
    }
}
