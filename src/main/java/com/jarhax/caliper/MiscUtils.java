package com.jarhax.caliper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.entity.Entity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class MiscUtils {

    public static String getWorldName (WorldServer world) {

        String result = "Undefined";

        // TODO add more fallback options
        if (world.provider != null) {

            result = world.provider.getDimensionType().getName();
        }

        return result;
    }

    public static int getChunkCount (WorldServer world) {

        return world.getChunkProvider() != null ? world.getChunkProvider().getLoadedChunkCount() : -1;
    }

    public static int getDimId (WorldServer world) {

        return world.provider != null ? world.provider.getDimension() : 0;
    }
    
    public static void bigWarning(boolean trace, List<String> lines) {
    	
        Caliper.LOG.error("************************************************************");

        for (String line : lines) {
        	
        	for (String subline : wrapString(line, 58, false, new ArrayList<String>())) {
        		
        		Caliper.LOG.error("* " + subline);
        	}
        }
        
    	if (trace) {
    		
    		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    		
            for (int i = 2; i < 8 && i < stackTrace.length; i++)
            {
                Caliper.LOG.warn("*  at {}{}", stackTrace[i].toString(), i == 7 ? "..." : "");
            }
    	}
    	
        Caliper.LOG.error("************************************************************");
    }
    
    public static EntityEntry getEntityEntry(Class<? extends Entity> clazz) {
    	
    	for (EntityEntry entry : ForgeRegistries.ENTITIES) {
    		
    		if (entry.getEntityClass() == clazz) {
    			
    			return entry;
    		}
    	}
    	
    	return null;
    }
    
    public static List<String> wrapString (String string, int lnLength, boolean wrapLongWords, List<String> list) {
        
        String lines[] = WordUtils.wrap(string, lnLength, null, wrapLongWords).split(SystemUtils.LINE_SEPARATOR);
        
        for (String line : lines)
            list.add(line);
            
        return list;
    }
}
