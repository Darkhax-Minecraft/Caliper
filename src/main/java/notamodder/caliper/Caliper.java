package notamodder.caliper;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import notamodder.caliper.proxy.CommonProxy;
import notamodder.notalib.utils.RegistryHelper;

import org.apache.logging.log4j.Logger;

@Mod(modid = Caliper.MODID, name = Caliper.NAME, version = "@VERSION@", dependencies = "required-after:notalib@[@VERSION_NOTALIB@,)")
public class Caliper {

    public static final String MODID = "caliper";
    public static final String NAME = "Caliper";
    public static Logger log;
    public static RegistryHelper helper;

    @SidedProxy(clientSide = "notamodder.caliper.proxy.ClientProxy", serverSide = "notamodder.notalib.caliper.ServerProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit (FMLPreInitializationEvent event) {

        log = event.getModLog();
        helper = new RegistryHelper(MODID).setTab(new CreativeTabCaliper());
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init (FMLInitializationEvent event) {

        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit (FMLPostInitializationEvent event) {

        proxy.postInit(event);
    }
}