package net.darkhax.caliper.profiling.profilers.errors.reporters;

import java.util.StringJoiner;

import net.darkhax.bookshelf.util.ModUtils;
import net.darkhax.caliper.FileHelper;
import net.darkhax.caliper.profiling.profilers.errors.ErrorReporter;
import net.darkhax.caliper.profiling.profilers.errors.Level;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ReportAirRecipe extends ErrorReporter {

    private final StringJoiner joiner = new StringJoiner(FileHelper.NEW_LINE);

    private int errorCount = 0;

    @Override
    public String getErrors () {

        return this.joiner.toString();
    }

    @Override
    public String getDescription () {

        return "If a recipe has air in it as an ingredient or output it can cause many issues such as crashes and uncraftable recipes.";
    }

    @Override
    public void collectErrors () {

        for (final IRecipe recipe : ForgeRegistries.RECIPES) {

            if (!recipe.isDynamic()) {
                
                if (recipe.getRecipeOutput().isEmpty() || containsAir(recipe.getIngredients())) {
                    
                    this.joiner.add(String.format("Recipe: %s Class: %s", recipe.getRegistryName().toString(), recipe.getClass().toGenericString()));
                }
            }
        }
    }
   
    private boolean containsAir (NonNullList<Ingredient> ingredients) {
        
        for (Ingredient ingredient : ingredients) {
            
            for (ItemStack stack : ingredient.getMatchingStacks()) {
                
                if (stack.isEmpty()) {
                    
                    return true;
                }
            }
        }
        
        return false;
    }

    @Override
    public Level getErrorLevel () {

        return Level.FATAL;
    }

    @Override
    public boolean foundErrors () {

        return this.errorCount > 0;
    }

    @Override
    public String getName () {

        return "Recipes with Air";
    }
}