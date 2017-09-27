package com.jarhax.caliper.debuggers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.filter.AbstractFilter;

import com.jarhax.caliper.Caliper;
import com.jarhax.caliper.TableBuilder;

import net.darkhax.bookshelf.util.MathsUtils;
import net.minecraftforge.fml.common.FMLModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class DebugLoadtimes extends AbstractFilter {

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private static final Map<String, LoadInfo> MODS = new HashMap<>();

    private static final String NEW_LINE = System.lineSeparator();

    /**
     * Hook for when FML finishes loading mods.
     */
    public static void onLoadingComplete () {

        // Sets up the Markdown Table Builder
        final TableBuilder<LoadInfo> table = new TableBuilder<>();
        table.addColumn("Mod", (info) -> info.mod);
        table.addColumn("Total Time", (info) -> info.total + "s");
        table.addColumn("Pre Init", (info) -> info.preInit + "s");
        table.addColumn("Init", (info) -> info.init + "s");
        table.addColumn("IMC", (info) -> info.imc + "s");
        table.addColumn("Post Init", (info) -> info.postInit + "s");
        table.addColumn("Valid Signature", (info) -> info.signed);
        table.addColumn("File Name", (info) -> info.sourceFile);

        // Adds info from Markdown tables
        for (final ModContainer mod : Loader.instance().getIndexedModList().values()) {

            getLoadInfo(mod.getName()).setContainerInfo(mod);
        }

        // Sorts all the entries, and adds them to the table builder.
        MODS.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).forEach( (info) -> table.addEntry(info.getValue()));

        // Prints out info
        try {

            final File file = new File("logs/caliper/game-analysis.md");
            final FileWriter writer = new FileWriter(file, false);

            writer.append("# Analysis - " + TIME_FORMAT.format(new Date()));
            writer.append(NEW_LINE + NEW_LINE);
            writer.append(WordUtils.wrap("This file contains an alaysis of the game state. This analysis was performed by the mod Caliper. The purpose of this analysis is to provide the user concise debug information about their game instance. This analysis will also have warnings for common errors with Minecraft mods. This data is anonymous, and is not automatically submitted to any online service.", 80));
            writer.append(NEW_LINE + NEW_LINE);
            writer.append(table.createString());

            writer.close();
        }

        catch (final IOException e) {

        }
    }

    @Override
    public Result filter (LogEvent event) {

        if (event.getMessage().getFormattedMessage().startsWith("Bar Step: ")) {

            processBarStep(event.getMessage().getFormattedMessage());
        }

        return Result.NEUTRAL;
    }

    /**
     * Processes a bar step message.
     *
     * @param barStep The bar step message to process.
     */
    public static void processBarStep (String barStep) {

        for (final InfoType info : InfoType.values()) {

            if (info.processMessage(barStep)) {

                break;
            }
        }
    }

    /**
     * Gets a LoadInfo object for a mod name.
     *
     * @param mod The name of the mod. not the id.
     * @return Load info for that mod name.
     */
    private static LoadInfo getLoadInfo (String mod) {

        final LoadInfo info = MODS.getOrDefault(mod, new LoadInfo(mod));
        MODS.put(mod, info);
        return info;
    }

    /**
     * Enum containing the basic load time trackerss.
     */
    private static enum InfoType {

        PRE_INIT("PreInitialization", (info, amount) -> info.preInit += amount),
        INIT("Initialization", (info, amount) -> info.init += amount),
        IMC("InterModComms$IMC", (info, amount) -> info.imc += amount),
        POST_INIT("PostInitialization", (info, amount) -> info.postInit += amount);

        private final String needle;
        private final InfoAdder op;

        InfoType (String needle, InfoAdder op) {

            this.needle = "Bar Step: " + needle + " - ";
            this.op = op;
        }

        public boolean processMessage (String message) {

            if (message.startsWith(this.needle)) {

                // Splits data into mod name and load time.
                final String[] info = message.replace(this.needle, "").split(" took ");
                info[1] = info[1].substring(0, info[1].length() - 1);

                // Adds the time to the info object.
                this.op.addInfo(getLoadInfo(info[0]), Double.parseDouble(info[1]));

                return true;
            }

            return false;
        }
    }

    /**
     * Functional interface for adding info to a load info object.
     */
    private static interface InfoAdder {

        void addInfo (LoadInfo info, double amount);
    }

    /**
     * This object represents a bunch of load time info for a mod.
     */
    private static class LoadInfo implements Comparable<LoadInfo> {

        public final String mod;
        public String sourceFile;
        public double preInit = 0;
        public double init = 0;
        public double imc = 0;
        public double postInit = 0;
        public boolean signed = false;

        public boolean totaled = false;
        public double total = 0;

        public LoadInfo (String mod) {

            this.mod = mod;
        }

        public void setSourceFile (File file) {

            this.sourceFile = file != null ? file.getName() : "Unknown";
        }

        public void initTotal () {

            if (!this.totaled) {

                this.total = MathsUtils.round(this.preInit + this.init + this.imc + this.postInit, 3);
                this.totaled = true;
            }
        }

        public void setContainerInfo (ModContainer container) {

            if (container != null) {

                this.setSourceFile(container.getSource());

                try {

                    if (container instanceof FMLModContainer) {

                        this.signed = !(boolean) ReflectionHelper.getPrivateValue(FMLModContainer.class, (FMLModContainer) container, "fingerprintNotPresent");
                    }
                }

                catch (final Exception e) {

                    Caliper.LOG.catching(e);
                }
            }
        }

        @Override
        public String toString () {

            return "LoadInfo [mod=" + this.mod + ", preInit=" + this.preInit + ", init=" + this.init + ", imc=" + this.imc + ", postInit=" + this.postInit + ", total=" + this.total + "]";
        }

        @Override
        public int compareTo (LoadInfo o) {

            this.initTotal();
            o.initTotal();
            return Double.valueOf(this.total).compareTo(o.total);
        }
    }
}
