package com.jarhax.caliper;

import java.io.File;

import com.jarhax.caliper.commands.CommandCaliper;
import com.jarhax.caliper.debuggers.DebugEntitySpawns;
import com.jarhax.caliper.debuggers.DebugEventListeners;
import com.jarhax.caliper.debuggers.DebugLoadtimes;
import com.jarhax.caliper.proxy.CommonProxy;

import net.darkhax.bookshelf.BookshelfRegistry;
import net.darkhax.bookshelf.lib.LoggingHelper;
import net.darkhax.bookshelf.registry.RegistryHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Caliper.MODID, name = Caliper.NAME, version = "@VERSION@", dependencies = "required-after:bookshelf@[2.1.450,)", certificateFingerprint = "@FINGERPRINT@")
public class Caliper {

    public static final String MODID = "caliper";
    public static final String NAME = "Caliper";
    public static final LoggingHelper LOG = new LoggingHelper("Caliper");
    public static RegistryHelper helper;

    @SidedProxy(clientSide = "com.jarhax.caliper.proxy.ClientProxy", serverSide = "com.jarhax.caliper.proxy.ServerProxy")
    public static CommonProxy proxy;

    public Caliper () {

        ((org.apache.logging.log4j.core.Logger) FMLLog.log).addFilter(new DebugLoadtimes());
    }

    @Mod.EventHandler
    public void preInit (FMLPreInitializationEvent event) {

        File logDir = new File("logs/caliper/");
        
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        
        helper = new RegistryHelper(MODID).setTab(new CreativeTabCaliper());
        BookshelfRegistry.addCommand(new CommandCaliper());
        proxy.preInit(event);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void init (FMLInitializationEvent event) {

        proxy.init(event);
    }

    @EventHandler
    public void postInit (FMLPostInitializationEvent event) {

        proxy.postInit(event);

        DebugEntitySpawns.debug();
    }

    @EventHandler
    public void onLoadComplete (FMLLoadCompleteEvent event) {

        DebugLoadtimes.onLoadingComplete();
        DebugEventListeners.printAllListeners();
    }

    @EventHandler
    public void onFingerprintViolation (FMLFingerprintViolationEvent event) {

        LOG.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }
}