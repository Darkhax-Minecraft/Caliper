package net.darkhax.caliper.profiling.profilers;

import net.darkhax.bookshelf.lib.TableBuilder;

public class InfoPair implements Comparable<InfoPair> {

    private final String key;
    private final long value;

    public InfoPair (String name, long start, long stop) {

        this.key = name;
        this.value = stop - start;
    }

    public static TableBuilder<InfoPair> createDataTable (String key, String value, String valueSuffix) {

        final TableBuilder<InfoPair> builder = new TableBuilder<>();
        builder.addColumn(key, InfoPair::getKey);
        builder.addColumn(value, e -> e.getValue() + valueSuffix);
        return builder;
    }

    public String getKey () {

        return this.key;
    }

    public long getValue () {

        return this.value;
    }

    @Override
    public int compareTo (InfoPair other) {

        return Long.compare(this.getValue(), other.getValue());
    }

    @Override
    public boolean equals (Object other) {

        return other instanceof InfoPair && ((InfoPair) other).getValue() == this.getValue() && ((InfoPair) other).getKey().equals(this.getKey());
    }
}