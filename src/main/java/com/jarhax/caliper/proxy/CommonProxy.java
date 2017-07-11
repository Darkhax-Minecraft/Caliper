package com.jarhax.caliper.proxy;

import com.jarhax.caliper.Caliper;
import com.jarhax.caliper.data.CaliperItems;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public void preInit (FMLPreInitializationEvent event) {

        Caliper.helper.registerItem(CaliperItems.LOOT_SPAWNER, "loot_spawner");
    }

    public void init (FMLInitializationEvent event) {

    }

    public void postInit (FMLPostInitializationEvent event) {

    }
}
