package notamodder.caliper.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import notamodder.caliper.Caliper;
import notamodder.caliper.data.CaliperItems;

public class CommonProxy {

    public void preInit (FMLPreInitializationEvent event) {

        Caliper.helper.registerItem(CaliperItems.LOOT_SPAWNER, "loot_spawner");
    }

    public void init (FMLInitializationEvent event) {

    }

    public void postInit (FMLPostInitializationEvent event) {

    }
}
