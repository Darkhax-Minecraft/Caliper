package notamodder.caliper.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import notamodder.notalib.utils.StackUtils;

public class ItemLootSpawner extends Item {

    @Override
    public ActionResult<ItemStack> onItemRightClick (World worldIn, EntityPlayer playerIn, EnumHand handIn) {

        if (worldIn.isRemote) {
            return super.onItemRightClick(worldIn, playerIn, handIn);
        }

        final ResourceLocation location = new ResourceLocation(playerIn.getHeldItem(handIn).getTagCompound().getString("LootTable"));
        final List<ItemStack> items = worldIn.getLootTableManager().getLootTableFromLocation(location).generateLootForPools(worldIn.rand, new LootContext.Builder((WorldServer) worldIn).build());

        for (final ItemStack stack : items) {

            playerIn.dropItem(stack, false);
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {

        tooltip.add(stack.getTagCompound().getString("LootTable"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems (Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {

        for (final ResourceLocation location : LootTableList.getAll()) {

            final ItemStack stack = StackUtils.prepareStack(new ItemStack(itemIn));
            stack.getTagCompound().setString("LootTable", location.toString());
            subItems.add(stack);
        }
    }
}
