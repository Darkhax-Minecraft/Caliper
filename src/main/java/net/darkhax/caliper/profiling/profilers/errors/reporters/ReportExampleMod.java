package net.darkhax.caliper.profiling.profilers.errors.reporters;

import java.util.StringJoiner;

import net.darkhax.caliper.FileHelper;
import net.darkhax.caliper.profiling.profilers.errors.ErrorReporter;
import net.darkhax.caliper.profiling.profilers.errors.Level;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class ReportExampleMod extends ErrorReporter {

    private final StringJoiner joiner = new StringJoiner(FileHelper.NEW_LINE);

    private int errorCount = 0;

    @Override
    public String getErrors () {

        return this.joiner.toString();
    }

    @Override
    public String getDescription () {

        return "The modid examplemod should not be used by mods!";
    }

    @Override
    public void collectErrors () {

        for (final ModContainer mod : Loader.instance().getIndexedModList().values()) {

            if ("examplemod".equalsIgnoreCase(mod.getModId())) {

                this.joiner.add(String.format("Mod: %s File: %s", mod.getName(), mod.getSource().getName()));
                this.errorCount++;
            }
        }
    }

    @Override
    public Level getErrorLevel () {

        return Level.ERROR;
    }

    @Override
    public boolean foundErrors () {

        return this.errorCount > 0;
    }

    @Override
    public String getName () {

        return "Example Mod";
    }
}