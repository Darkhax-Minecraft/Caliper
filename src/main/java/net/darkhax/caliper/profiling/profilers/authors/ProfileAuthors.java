package net.darkhax.caliper.profiling.profilers.authors;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

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

        final Multimap<String, String> authors = ArrayListMultimap.create();

        for (final ModContainer mod : Loader.instance().getIndexedModList().values()) {

            for (final String author : mod.getMetadata().authorList) {

                authors.put(author.toLowerCase(), mod.getName());
            }
        }

        final List<AuthorInfo> authorInfo = new ArrayList<>();

        for (final String author : authors.keySet()) {

            final Collection<String> mods = authors.get(author);
            authorInfo.add(new AuthorInfo(author, mods.size(), mods));
        }

        Collections.sort(authorInfo, Collections.reverseOrder());

        try (final FileWriter writer = new FileWriter(this.file, false)) {

            FileHelper.writeInfoBlock(writer, 0, "Author Count", "This is a list of all the authors of the mods in the instance sorted by how many they have.", true);

            for (final AuthorInfo author : authorInfo) {

                writer.write(author.getModCount() + " - " + author.getAuthor() + FileHelper.NEW_LINE);

                for (final String mod : author.getMods()) {

                    writer.write("     - " + mod + FileHelper.NEW_LINE);
                }
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