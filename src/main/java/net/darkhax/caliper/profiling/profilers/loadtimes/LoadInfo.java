package net.darkhax.caliper.profiling.profilers.loadtimes;

import java.io.File;

import net.darkhax.bookshelf.lib.TableBuilder;
import net.darkhax.bookshelf.util.MathsUtils;
import net.darkhax.caliper.Caliper;
import net.minecraftforge.fml.common.FMLModContainer;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

/**
 * This object represents a bunch of load time info for a mod.
 */
class LoadInfo implements Comparable<LoadInfo> {

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

    public static TableBuilder<LoadInfo> createDataTable () {

        final TableBuilder<LoadInfo> table = new TableBuilder<>();
        table.addColumn("Mod", (info) -> info.mod);
        table.addColumn("Total Time", (info) -> info.total + "s");
        table.addColumn("Pre Init", (info) -> info.preInit + "s");
        table.addColumn("Init", (info) -> info.init + "s");
        table.addColumn("IMC", (info) -> info.imc + "s");
        table.addColumn("Post Init", (info) -> info.postInit + "s");
        table.addColumn("Valid Signature", (info) -> info.signed);
        table.addColumn("File Name", (info) -> info.sourceFile);

        return table;
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

                    this.signed = !(boolean) ObfuscationReflectionHelper.getPrivateValue(FMLModContainer.class, (FMLModContainer) container, "fingerprintNotPresent");

                    if (this.signed) {

                        DebugLoadtimes.signedMods++;
                    }
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