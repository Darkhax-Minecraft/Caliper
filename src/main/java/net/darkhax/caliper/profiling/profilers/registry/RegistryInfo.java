package net.darkhax.caliper.profiling.profilers.registry;

import net.darkhax.bookshelf.lib.TableBuilder;

public class RegistryInfo implements Comparable<RegistryInfo> {

    private final String name;
    private final long entries;
    private final float percentage;

    public RegistryInfo (String name, long entries, float percentage) {

        this.name = name;
        this.entries = entries;
        this.percentage = percentage;
    }

    public static TableBuilder<RegistryInfo> createDataTable () {

        final TableBuilder<RegistryInfo> builder = new TableBuilder<>();
        builder.addColumn("Mod Name", RegistryInfo::getName);
        builder.addColumn("Entries", RegistryInfo::getEntries);
        builder.addColumn("Utilization", RegistryInfo::getUtilization);

        return builder;
    }

    public String getName () {

        return this.name;
    }

    public long getEntries () {

        return this.entries;
    }

    public String getUtilization () {

        return this.percentage >= 0.001f ? String.format("%.3f%%", this.percentage) : "<0.001%";
    }

    @Override
    public int compareTo (RegistryInfo o) {

        return Long.compare(this.getEntries(), o.getEntries());
    }

    @Override
    public boolean equals (Object o) {

        return o instanceof RegistryInfo && this.getName().equals(((RegistryInfo) o).getName());
    }
}
