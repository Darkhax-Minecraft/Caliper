package notamodder.caliper;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import notamodder.notalib.proxy.CommonProxy;
import notamodder.notalib.utils.RegistryHelper;

@Mod(modid = Caliper.MODID, name = Caliper.NAME, version = "@VERSION@")
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
        helper = new RegistryHelper(MODID);
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