package net.darkhax.caliper.profiling;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Profiler {

    public void onConstructed () {

    }

    public void onPreInit () {

    }

    public void onInit () {

    }

    public void onPostInit () {

    }

    public void onLoadComplete () {

    }

    @SideOnly(Side.CLIENT)
    public void onClientLoadComplete () {

    }

    public boolean hasEvents () {

        return false;
    }

    public boolean isEnabled () {

        return true;
    }
}