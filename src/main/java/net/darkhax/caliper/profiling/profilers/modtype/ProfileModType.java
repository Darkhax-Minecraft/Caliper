package net.darkhax.caliper.profiling.profilers.modtype;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.StringJoiner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.darkhax.caliper.Caliper;
import net.darkhax.caliper.FileHelper;
import net.darkhax.caliper.profiling.Profiler;
import net.darkhax.caliper.profiling.RegisterProfiler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

@RegisterProfiler(name = "Mod Types", description = "Looks at what types of mods are installed.")
public class ProfileModType extends Profiler {

    private final File file = new File(Caliper.LOG_DIR, "mod-types.md");
    private final StringJoiner mcreator = new StringJoiner(FileHelper.NEW_LINE);
    private int foundMods = 0;

    @Override
    public void onLoadComplete () {

        for (final ModContainer mod : Loader.instance().getIndexedModList().values()) {

            try (ZipFile zfile = new ZipFile(mod.getSource())) {

                final Enumeration<? extends ZipEntry> zipEntries = zfile.entries();
                boolean found = false;

                while (zipEntries.hasMoreElements()) {

                    if (zipEntries.nextElement().getName().startsWith("mod/mcreator/")) {

                        this.mcreator.add(String.format("Mod: %s File: %s", mod.getName(), mod.getSource().getName()));
                        found = true;
                        this.foundMods++;
                        break;
                    }
                }

                if (found) {

                    break;
                }
            }

            catch (final IOException e) {

            }
        }

        if (this.foundMods > 0) {

            try (final FileWriter writer = new FileWriter(this.file, false)) {

                FileHelper.writeInfoBlock(writer, 0, "Mod Types", "This contains a break down of mods by what type they are.", true);
                FileHelper.writeInfoBlock(writer, 1, "MCreator", "This list contains mods made with the tool MCreator.");
                writer.append(this.mcreator.toString());
            }

            catch (final IOException e) {

            }
        }
    }
}