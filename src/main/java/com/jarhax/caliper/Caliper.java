package com.jarhax.caliper;

import com.jarhax.caliper.commands.CommandCaliper;
import com.jarhax.caliper.proxy.CommonProxy;

import net.darkhax.bookshelf.lib.LoggingHelper;
import net.darkhax.bookshelf.registry.RegistryHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Caliper.MODID, name = Caliper.NAME, version = "@VERSION@", dependencies = "required-after:bookshelf@[2.1.427,)", acceptedMinecraftVersions = "[1.12,1.12.2)", acceptableRemoteVersions = "*",  certificateFingerprint = "@FINGERPRINT@")
public class Caliper {

    public static final String MODID = "caliper";
    public static final String NAME = "Caliper";
    public static final LoggingHelper LOG = new LoggingHelper("Caliper");
    public static RegistryHelper helper;

    @SidedProxy(clientSide = "com.jarhax.caliper.proxy.ClientProxy", serverSide = "com.jarhax.caliper.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit (FMLPreInitializationEvent event) {

        helper = new RegistryHelper(MODID).setTab(new CreativeTabCaliper());
        proxy.preInit(event);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void init (FMLInitializationEvent event) {

        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit (FMLPostInitializationEvent event) {

        proxy.postInit(event);
    }

    @EventHandler
    public void serverStarting (FMLServerStartingEvent event) {

        event.registerServerCommand(new CommandCaliper());
    }
    
    
    @EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
        
        LOG.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }
}