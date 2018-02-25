package com.jarhax.caliper.profiling.profilers.textures;

import org.apache.commons.io.FileUtils;

import net.darkhax.bookshelf.lib.TableBuilder;

public class LargeTextureInfo implements Comparable<LargeTextureInfo> {

    private final String name;
    private final long width;
    private final long height;
    private final long pixels;

    public LargeTextureInfo (String name, long width, long height) {

        this.name = name;
        this.width = width;
        this.height = height;
        this.pixels = height * width;
    }

    public static TableBuilder<LargeTextureInfo> createDataTable () {

        final TableBuilder<LargeTextureInfo> builder = new TableBuilder<>();
        builder.addColumn("Texture Name", LargeTextureInfo::getName);
        builder.addColumn("Dimensions", (e) -> e.getWidth() + " X " + e.getHeight());
        builder.addColumn("Pixels", LargeTextureInfo::getPixels);
        builder.addColumn("Est. Size", LargeTextureInfo::getFileSize);

        return builder;
    }

    public String getName () {

        return this.name;
    }

    public long getWidth () {

        return this.width;
    }

    public long getHeight () {

        return this.height;
    }

    public long getPixels () {

        return this.pixels;
    }

    public String getFileSize () {

        return FileUtils.byteCountToDisplaySize(this.pixels * 4);
    }

    @Override
    public int compareTo (LargeTextureInfo o) {

        return Long.compare(this.getPixels(), o.getPixels());
    }

    @Override
    public boolean equals (Object o) {

        return o instanceof LargeTextureInfo && this.getName().equals(((LargeTextureInfo) o).getName());
    }
}
