package net.darkhax.caliper.profiling.profilers.authors;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import net.darkhax.caliper.Caliper;
import net.darkhax.caliper.FileHelper;
import net.darkhax.caliper.profiling.Profiler;
import net.darkhax.caliper.profiling.RegisterProfiler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

@RegisterProfiler(name = "Authors", description = "Profiles author info for mods in the instance.")
public class ProfileAuthors extends Profiler {

    private final File file = new File(Caliper.LOG_DIR, "authors.md");

    @Override
    public void onLoadComplete () {

        final List<String> authors = new ArrayList<>();

        for (final ModContainer mod : Loader.instance().getIndexedModList().values()) {

            authors.addAll(mod.getMetadata().authorList);
        }

        final Map<String, Long> counts = authors.stream().collect(Collectors.groupingBy(e -> e.toLowerCase(), Collectors.counting()));

        try (final FileWriter writer = new FileWriter(this.file, false)) {

            FileHelper.writeInfoBlock(writer, 0, "Author Count", "This is a list of all the authors of the mods in the instance sorted by how many they have.", true);

            for (final Entry<String, Long> s : entriesSortedByValues(counts)) {

                writer.append(s.getValue() + " - " + s.getKey() + FileHelper.NEW_LINE);
            }
        }

        catch (final IOException e) {

        }
    }

    static <K, V extends Comparable<? super V>> List<Entry<K, V>> entriesSortedByValues (Map<K, V> map) {

        final List<Entry<K, V>> sortedEntries = new ArrayList<>(map.entrySet());

        Collections.sort(sortedEntries, (e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        return sortedEntries;
    }
}