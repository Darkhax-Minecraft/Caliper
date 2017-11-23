package com.jarhax.caliper.debuggers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.jarhax.caliper.Caliper;

import net.darkhax.bookshelf.lib.TableBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DebugTextureMap {

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private static final String NEW_LINE = System.lineSeparator();

    @SideOnly(Side.CLIENT)
    public static void run () {

        final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

        final File file = new File("logs/caliper/texture-analysis.md");

        try (final FileWriter writer = new FileWriter(file, false)) {

            writer.append("# Texture Analysis - " + TIME_FORMAT.format(new Date()));
            writer.append(NEW_LINE + NEW_LINE);
            writer.append(WordUtils.wrap("This file contains an analysis on how mods are adding textures. This data is anonymous, and is not automatically submitted to any online service. Please note that file size count is based on the assumption that every pixel is 4 bytes. This size relates to the amount of graphic memory the textures take up, and not the actual file size on your hard drive.", 80));
            writer.append(NEW_LINE + NEW_LINE);

            // Gets all texture maps from the texture manager.
            for (final Map.Entry<ResourceLocation, ITextureObject> entry : textureManager.mapTextureObjects.entrySet()) {

                final ITextureObject textureObject = entry.getValue();

                if (textureObject instanceof TextureMap) {

                    logTextureMap(writer, (TextureMap) textureObject, entry.getKey());
                }
            }
        }

        catch (final IOException e) {

            Caliper.LOG.catching(e);
        }
    }

    private static void logTextureMap (FileWriter writer, TextureMap map, ResourceLocation name) throws IOException {

        final TableBuilder<TextureInfo> table = getNewTable();

        final Map<String, TextureInfo> modTextureInfo = new HashMap<>();

        // Sorts the mod texture count to the main map.
        final Map<String, Long> modTextureCounts = map.mapUploadedSprites.values().stream().collect(Collectors.groupingBy(sprite -> new ResourceLocation(sprite.getIconName()).getResourceDomain(), Collectors.summingLong(sprite -> 1)));

        for (final Entry<String, Long> value : modTextureCounts.entrySet()) {

            final TextureInfo info = modTextureInfo.computeIfAbsent(value.getKey(), (string) -> new TextureInfo(string));
            info.setTextureCount(value.getValue());
        }

        // Sorts the mod pixel count to the main map.

        final Map<String, Long> modPixelCounts = map.mapUploadedSprites.values().stream().collect(Collectors.groupingBy(sprite -> new ResourceLocation(sprite.getIconName()).getResourceDomain(), Collectors.summingLong(sprite -> sprite.getIconWidth() * sprite.getIconHeight())));

        for (final Entry<String, Long> value : modPixelCounts.entrySet()) {

            final TextureInfo info = modTextureInfo.computeIfAbsent(value.getKey(), (string) -> new TextureInfo(string));
            info.setPixelCount(value.getValue());
        }

        // Adds all the texture info to the table.
        for (final TextureInfo info : modTextureInfo.values()) {

            table.addEntry(info);
        }

        writer.write("Texture: " + name.toString());
        writer.write(NEW_LINE);
        writer.write(table.createString());
    }

    @SideOnly(Side.CLIENT)
    private static TableBuilder<TextureInfo> getNewTable () {

        final TableBuilder<TextureInfo> builder = new TableBuilder<>();
        builder.addColumn("Mod ID", (info) -> info.getDomain());
        builder.addColumn("Textures", (info) -> info.getTextureCount());
        builder.addColumn("Pixels", (info) -> info.getPixelCount());
        builder.addColumn("Est. Size", (info) -> info.getFileSize());

        return builder;
    }

    @SideOnly(Side.CLIENT)
    private static void dumpTextureMap (TextureMap map, String name) {

        final File outputFolder = new File("logs/caliper/images/");

        if (!outputFolder.exists()) {

            outputFolder.mkdirs();
        }

        saveGlTexture(name, map.getGlTextureId(), outputFolder);
    }

    private static void saveGlTexture (String name, int textureId, File outputFolder) {

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        final int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        final int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
        final int size = width * height;

        final BufferedImage bufferedimage = new BufferedImage(width, height, 2);
        final String fileName = name + ".png";

        final File output = new File(outputFolder, fileName);
        final IntBuffer buffer = BufferUtils.createIntBuffer(size);
        final int[] data = new int[size];

        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buffer);
        buffer.get(data);
        bufferedimage.setRGB(0, 0, width, height, data, 0, width);

        try {
            ImageIO.write(bufferedimage, "png", output);
        }
        catch (final IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    public static class TextureInfo {

        private String domain;
        private long textureCount = 0;
        private long pixelCount = 0;

        public TextureInfo (String domain) {

            this.domain = domain;
        }

        public String getDomain () {

            return this.domain;
        }

        public long getTextureCount () {

            return this.textureCount;
        }

        public long getPixelCount () {

            return this.pixelCount;
        }

        public String getFileSize () {

            final long bytes = this.pixelCount * 4;
            final int unit = 1024;

            if (bytes < unit) {

                return bytes + " B";
            }

            final int exp = (int) (Math.log(bytes) / Math.log(unit));
            final String pre = "KMGTPE".charAt(exp - 1) + "i";
            return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
        }

        public void setDomain (String domain) {

            this.domain = domain;
        }

        public void setTextureCount (long textureCount) {

            this.textureCount = textureCount;
        }

        public void setPixelCount (long pixelCount) {

            this.pixelCount = pixelCount;
        }
    }
}