package net.darkhax.caliper;

import java.io.File;

import net.darkhax.bookshelf.BookshelfRegistry;
import net.darkhax.bookshelf.lib.LoggingHelper;
import net.darkhax.caliper.commands.CommandCaliper;
import net.darkhax.caliper.profiling.Profiler;
import net.darkhax.caliper.profiling.ProfilerManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
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
    public static final ProfilerManager PROFILER_MANAGER = new ProfilerManager();
    public static final File LOG_DIR = new File("logs/caliper/");

    @EventHandler
    public void onConstruction (FMLConstructionEvent event) {

        // Create caliper logs folder if it doesn't already exist.
        if (!LOG_DIR.exists()) {

            LOG_DIR.mkdirs();
        }

        // Load all of the profilers from annotation table
        PROFILER_MANAGER.init(event.getASMHarvestedData());

        // Call all onConstructed profiler hooks.
        PROFILER_MANAGER.getFeatures().forEach(Profiler::onConstructed);
    }

    @Mod.EventHandler
    public void preInit (FMLPreInitializationEvent event) {

        // Call all onPreInit profiler hooks.
        PROFILER_MANAGER.getFeatures().forEach(Profiler::onPreInit);

        // Adds the caliper tree command.
        BookshelfRegistry.addCommand(new CommandCaliper());
    }

    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    public void preInitClient (FMLPreInitializationEvent event) {

        // Call all onPreInitClient profiler hooks.
        PROFILER_MANAGER.getFeatures().forEach(Profiler::onPreInitClient);
    }

    @EventHandler
    public void init (FMLInitializationEvent event) {

        // Call all onInit profiler hooks.
        PROFILER_MANAGER.getFeatures().forEach(Profiler::onInit);
    }

    @EventHandler
    public void postInit (FMLPostInitializationEvent event) {

        // Call all onPostInit profiler hooks.
        PROFILER_MANAGER.getFeatures().forEach(Profiler::onPostInit);
    }

    @EventHandler
    public void onLoadComplete (FMLLoadCompleteEvent event) {

        // Call all onLoadComplete profiler hooks.
        PROFILER_MANAGER.getFeatures().forEach(Profiler::onLoadComplete);
    }

    @EventHandler
    @SideOnly(Side.CLIENT)
    public void onClientLoadComplete (FMLLoadCompleteEvent event) {

        // Call all onClientLoadComplete profiler hooks.
        PROFILER_MANAGER.getFeatures().forEach(Profiler::onClientLoadComplete);
    }

    @EventHandler
    public void onFingerprintViolation (FMLFingerprintViolationEvent event) {

        LOG.warn("Invalid fingerprint detected! The file {} may have been tampered with. This version will NOT be supported by the author!", event.getSource().getName());
    }
}