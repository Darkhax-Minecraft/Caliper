package net.darkhax.caliper.profiling.profilers.errors.reporters;

import java.util.StringJoiner;

import net.darkhax.caliper.FileHelper;
import net.darkhax.caliper.profiling.profilers.errors.ErrorReporter;
import net.darkhax.caliper.profiling.profilers.errors.Level;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ReportMissingEnchantmentDescriptions extends ErrorReporter {

    private final StringJoiner joiner = new StringJoiner(FileHelper.NEW_LINE);
    private int errorCount = 0;

    @Override
    public String getErrors () {

        return this.joiner.toString();
    }

    @Override
    public String getDescription () {

        return "Many mods have systems for showing descriptions for enchantments. Adding translations for these can improve mod compatibility.";
    }

    @Override
    public String getName () {

        return "Enchantment Descriptions";
    }

    @Override
    public Level getErrorLevel () {

        return Level.WARNING;
    }

    @Override
    public boolean foundErrors () {

        return this.errorCount > 0;
    }

    @Override
    public void collectErrors () {

        for (final Enchantment ench : ForgeRegistries.ENCHANTMENTS) {

            if ("minecraft".equals(ench.getRegistryName().getResourceDomain())) {

                continue;
            }

            final String translationKey = getTranslationKey(ench);
            final String description = I18n.format(translationKey);

            if (description.startsWith("enchantment.")) {

                this.joiner.add(String.format("Enchant: %s Translation Key: %s", ench.getRegistryName().toString(), translationKey));
                this.errorCount++;
            }
        }
    }

    private static String getTranslationKey (Enchantment enchant) {

        if (enchant != null && enchant.getRegistryName() != null) {

            return String.format("enchantment.%s.%s.desc", enchant.getRegistryName().getResourceDomain(), enchant.getRegistryName().getResourcePath());
        }

        return "NULL";
    }
}