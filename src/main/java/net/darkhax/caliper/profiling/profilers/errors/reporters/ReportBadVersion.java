package net.darkhax.caliper.profiling.profilers.errors.reporters;

import java.util.StringJoiner;

import net.darkhax.caliper.FileHelper;
import net.darkhax.caliper.profiling.profilers.errors.ErrorReporter;
import net.darkhax.caliper.profiling.profilers.errors.Level;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class ReportBadVersion extends ErrorReporter {

    private final StringJoiner joiner = new StringJoiner(FileHelper.NEW_LINE);

    private final int errorCount = 0;

    @Override
    public String getErrors () {

        return this.joiner.toString();
    }

    @Override
    public String getDescription () {

        return "The version number @version@ is a placeholder. If a mod has this version number something went wrong in the build process.";
    }

    @Override
    public void collectErrors () {

        for (final ModContainer mod : Loader.instance().getIndexedModList().values()) {

            if ("@VERSION@".equalsIgnoreCase(mod.getVersion())) {

                this.joiner.add(String.format("Mod: %s File: %s", mod.getName(), mod.getSource().getName()));
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

        return "Bad Version";
    }
}