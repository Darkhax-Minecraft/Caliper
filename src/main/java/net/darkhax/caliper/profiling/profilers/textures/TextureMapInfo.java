package net.darkhax.caliper.profiling.profilers.textures;

import org.apache.commons.io.FileUtils;

import net.darkhax.bookshelf.lib.TableBuilder;

public class TextureMapInfo implements Comparable<TextureMapInfo> {

    private final String domain;
    private long textureCount = 0;
    private long pixelCount = 0;

    public TextureMapInfo (String domain) {

        this.domain = domain;
    }

    public static TableBuilder<TextureMapInfo> createDataTable () {

        final TableBuilder<TextureMapInfo> builder = new TableBuilder<>();
        builder.addColumn("Mod ID", TextureMapInfo::getDomain);
        builder.addColumn("Textures", TextureMapInfo::getTextureCount);
        builder.addColumn("Pixels", TextureMapInfo::getPixelCount);
        builder.addColumn("Est. Size", TextureMapInfo::getFileSize);

        return builder;
    }

    public String getDomain () {

        return this.domain;
    }

    public long getTextureCount () {

        return this.textureCount;
    }

    public void addTextureCount (long count) {

        this.textureCount += count;
    }

    public long getPixelCount () {

        return this.pixelCount;
    }

    public void addPixels (long pixels) {

        this.pixelCount += pixels;
    }

    public String getFileSize () {

        final long bytes = this.pixelCount * 4;
        return FileUtils.byteCountToDisplaySize(bytes);
    }

    @Override
    public int compareTo (TextureMapInfo o) {

        return Long.compare(this.getPixelCount(), o.getPixelCount());
    }

    @Override
    public boolean equals (Object o) {

        return o instanceof TextureMapInfo && this.getDomain().equals(((TextureMapInfo) o).getDomain());
    }
}