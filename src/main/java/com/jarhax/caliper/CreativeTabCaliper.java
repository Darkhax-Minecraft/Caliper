package com.jarhax.caliper;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class CreativeTabCaliper extends CreativeTabs {

    public CreativeTabCaliper () {

        super("caliper");
        this.setBackgroundImageName("item_search.png");
    }

    @Override
    public boolean hasSearchBar () {

        return true;
    }

    @Override
    public ItemStack getTabIconItem () {

        return new ItemStack(Items.STICK);
    }
}
