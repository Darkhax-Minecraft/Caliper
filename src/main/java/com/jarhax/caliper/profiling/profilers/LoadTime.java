package com.jarhax.caliper.profiling.profilers;

import net.darkhax.bookshelf.lib.TableBuilder;

public class LoadTime implements Comparable<LoadTime> {

    private final String name;
    private final long time;

    public LoadTime (String name, long start, long stop) {

        this.name = name;
        this.time = stop - start;
    }

    public static TableBuilder<LoadTime> createDataTable () {

        final TableBuilder<LoadTime> builder = new TableBuilder<>();
        builder.addColumn("Name", e -> e.getName());
        builder.addColumn("Time", e -> e.getTime() + "ms");
        return builder;
    }

    public String getName () {

        return this.name;
    }

    public long getTime () {

        return this.time;
    }

    @Override
    public int compareTo (LoadTime other) {

        return Long.compare(this.getTime(), other.getTime());
    }

    @Override
    public boolean equals (Object other) {

        return other instanceof LoadTime && ((LoadTime) other).getTime() == this.getTime() && ((LoadTime) other).getName().equals(this.getName());
    }

    @Override
    public String toString () {

        return "LoadTime [name=" + this.name + ", time=" + this.time + "]";
    }
}