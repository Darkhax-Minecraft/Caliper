package net.darkhax.caliper.profiling.profilers.textures;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.darkhax.bookshelf.lib.TableBuilder;
import net.darkhax.bookshelf.util.RenderUtils;
import net.darkhax.caliper.Caliper;
import net.darkhax.caliper.FileHelper;
import net.darkhax.caliper.profiling.Profiler;
import net.darkhax.caliper.profiling.RegisterProfiler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@RegisterProfiler(name = "Texture Analysis", description = "Profiles the texture data loaded by the game.")
public class ProfileTextureMap extends Profiler {

    private final File imageDir = new File(Caliper.LOG_DIR, "images");
    private final TableBuilder<LargeTextureInfo> largeTextures = LargeTextureInfo.createDataTable();

    @Override
    public void onPreInit () {

        // Creates image directory if it doesn't exist.
        if (!this.imageDir.exists()) {

            this.imageDir.mkdirs();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClientLoadComplete () {

        final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        final File file = new File(Caliper.LOG_DIR, "texture-maps.md");

        try (final FileWriter writer = new FileWriter(file, false)) {

            // Print initial info block.
            FileHelper.writeInfoBlock(writer, 1, "Texture Map Analysis", "This file contains an analysis on the texture maps in the game, and which mods are adding textures to them. The size value is meant to represent the amount of graphical memory used by the texture and not the file size on your hard drive. Size is based on the assumption that every pixel is four bytes.", true);

            // Gets all texture maps from the texture manager.
            for (final Map.Entry<ResourceLocation, ITextureObject> entry : textureManager.mapTextureObjects.entrySet()) {

                final ITextureObject textureObject = entry.getValue();

                if (textureObject instanceof TextureMap) {

                    this.logTextureMap(writer, (TextureMap) textureObject, entry.getKey());
                }
            }

            if (!this.largeTextures.getEntries().isEmpty()) {

                Collections.sort(this.largeTextures.getEntries(), Collections.reverseOrder());
                FileHelper.writeInfoBlock(writer, 2, "Large Texture Files", "The following table of textures are ones that are considered very large. While some of these may be justified, it's possible that these are frivolous.");
                writer.append(this.largeTextures.createString());
            }
        }

        catch (final IOException e) {

            Caliper.LOG.catching(e);
        }
    }

    private void logTextureMap (FileWriter writer, TextureMap map, ResourceLocation name) throws IOException {

        final TableBuilder<TextureMapInfo> table = TextureMapInfo.createDataTable();
        final Map<String, TextureMapInfo> modTextureMapInfo = new HashMap<>();

        for (final TextureAtlasSprite sprite : map.mapUploadedSprites.values()) {

            final String owner = sprite.getIconName().split(":")[0];
            final long pixelCount = sprite.getIconHeight() * sprite.getIconWidth();
            final TextureMapInfo info = modTextureMapInfo.computeIfAbsent(owner, TextureMapInfo::new);

            // Checks if area of pixels is larger than a 256x256 image.
            if (pixelCount > 65536) {

                this.largeTextures.addEntry(new LargeTextureInfo(sprite.getIconName(), sprite.getIconWidth(), sprite.getIconHeight()));
            }

            info.addPixels(pixelCount);
            info.addTextureCount(1);
        }

        // Adds all the texture info to the table.
        for (final TextureMapInfo info : modTextureMapInfo.values()) {

            table.addEntry(info);
        }

        Collections.sort(table.getEntries(), Collections.reverseOrder());

        writer.write(FileHelper.NEW_LINE);
        writer.write("## Texture: " + name.toString());
        writer.write(FileHelper.NEW_LINE);
        writer.write(table.createString());

        // Saves image
        RenderUtils.saveTextureToFile(map.getGlTextureId(), new File(this.imageDir, name.toString().replaceAll(":", "_").replaceAll("/", "_") + ".png"));
    }
}