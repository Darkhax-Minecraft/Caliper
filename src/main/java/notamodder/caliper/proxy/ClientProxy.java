package notamodder.caliper.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import notamodder.caliper.Caliper;
import notamodder.caliper.data.CaliperItems;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit (FMLPreInitializationEvent event) {

        super.preInit(event);
        
        Caliper.helper.registerInventoryModel(CaliperItems.LOOT_SPAWNER);
    }

    @Override
    public void init (FMLInitializationEvent event) {

        super.init(event);
        
        Caliper.helper.registerColorHandler(CaliperItems.LOOT_SPAWNER, (stack, index) -> stack.getTagCompound().getString("LootTable").hashCode());
    }

    @Override
    public void postInit (FMLPostInitializationEvent event) {

        super.postInit(event);
    }
}
