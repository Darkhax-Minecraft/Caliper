package com.jarhax.caliper;

import java.io.File;

import com.jarhax.caliper.commands.CommandCaliper;
import com.jarhax.caliper.debuggers.DebugEntitySpawns;
import com.jarhax.caliper.debuggers.DebugEventListeners;
import com.jarhax.caliper.debuggers.DebugIdUsage;
import com.jarhax.caliper.debuggers.DebugLoadtimes;
import com.jarhax.caliper.debuggers.DebugTextureMap;

import net.darkhax.bookshelf.BookshelfRegistry;
import net.darkhax.bookshelf.lib.LoggingHelper;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = "caliper", name = "Caliper", version = "@VERSION@", dependencies = "required-after:bookshelf@[2.2.462,)", certificateFingerprint = "@FINGERPRINT@")
public class Caliper {

    public static final LoggingHelper LOG = new LoggingHelper("Caliper");

    public Caliper () {

        // Apply a log4j filter to read load time messages.
        ((org.apache.logging.log4j.core.Logger) FMLLog.log).addFilter(new DebugLoadtimes());
    }

    @Mod.EventHandler
    public void preInit (FMLPreInitializationEvent event) {

        // Create the caliper log directory if it doesn't exist.
        new File("logs/caliper/").mkdirs();

        // Adds the caliper tree command.
        BookshelfRegistry.addCommand(new CommandCaliper());
    }

    @EventHandler
    public void init (FMLInitializationEvent event) {

    }

    @EventHandler
    public void postInit (FMLPostInitializationEvent event) {

        // Finds broken entity spawns
        // TODO replace with error detecting system.
        DebugEntitySpawns.debug();
    }

    @EventHandler
    public void onLoadComplete (FMLLoadCompleteEvent event) {

        // Prints load time info.
        DebugLoadtimes.onLoadingComplete();

        // Prints all current event listeners.
        DebugEventListeners.printAllListeners();

        // Prints registry id usage.
        DebugIdUsage.onLoadingComplete();
    }

    @EventHandler
    @SideOnly(Side.CLIENT)
    public void onClientLoadComplete (FMLLoadCompleteEvent event) {

        DebugTextureMap.run();
    }

    @EventHandler
    public void onFingerprintViolation (FMLFingerprintViolationEvent event) {

        LOG.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }
}