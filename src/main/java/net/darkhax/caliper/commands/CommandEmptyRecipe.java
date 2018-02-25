package net.darkhax.caliper.commands;

import net.darkhax.bookshelf.command.Command;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class CommandEmptyRecipe extends Command {

    @Override
    public String getName () {

        return "recipe";
    }

    @Override
    public String getUsage (ICommandSender sender) {

        return "/caliper recipe book|bookall|normal|normalall";
    }

    @Override
    public void execute (MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if (args.length == 1) {

            if ("book".equalsIgnoreCase(args[0]) && sender instanceof EntityPlayerMP) {

                this.findBadRecipes(((EntityPlayerMP) sender).getRecipeBook().getRecipes(), sender, false);
            }

            else if ("bookall".equalsIgnoreCase(args[0]) && sender instanceof EntityPlayerMP) {

                this.findBadRecipes(((EntityPlayerMP) sender).getRecipeBook().getRecipes(), sender, true);
            }

            else if ("normalall".equalsIgnoreCase(args[0]) && sender instanceof EntityPlayerMP) {

                this.findBadRecipes(ForgeRegistries.RECIPES, sender, true);
            }

            else if ("normal".equalsIgnoreCase(args[0]) && sender instanceof EntityPlayerMP) {

                this.findBadRecipes(ForgeRegistries.RECIPES, sender, false);
            }

            return;
        }

        this.findBadRecipes(ForgeRegistries.RECIPES, sender, false);
    }

    @Override
    public int getRequiredPermissionLevel () {

        return 0;
    }

    private void findBadRecipes (Iterable<IRecipe> recipes, ICommandSender sender, boolean includeMisc) {

        boolean hasFound = false;

        for (final IRecipe recipe : recipes) {

            if (includeMisc || recipe instanceof ShapedRecipes || recipe instanceof ShapelessRecipes || recipe instanceof ShapedOreRecipe || recipe instanceof ShapelessOreRecipe) {

                final ItemStack output = recipe.getRecipeOutput();

                if (!recipe.getRegistryName().toString().startsWith("minecraft") && (output == null || output.isEmpty())) {

                    hasFound = true;
                    sender.sendMessage(new TextComponentString("Invalid Recipe: " + recipe.getGroup() + " - " + recipe.getRegistryName().toString()));
                }
            }
        }

        if (!hasFound) {

            sender.sendMessage(new TextComponentString("No invalid recipes found."));
        }
    }
}
