package com.jarhax.caliper;

import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.server.command.ForgeCommand;

public class MiscUtils {
    
    public static String getWorldName(WorldServer world) {
        
        String result = "Undefined";
        
        //TODO add more fallback options
        if (world.provider != null) {
            
            result = world.provider.getDimensionType().getName();
        }
        
        return result;
    }
    
    public static int getChunkCount(WorldServer world) {
       
        return (world.getChunkProvider() != null) ? world.getChunkProvider().getLoadedChunkCount() : -1;
    }
    
    public static int getDimId(WorldServer world) {
        
        return (world.provider != null) ? world.provider.getDimension() : 0;
    }
}
