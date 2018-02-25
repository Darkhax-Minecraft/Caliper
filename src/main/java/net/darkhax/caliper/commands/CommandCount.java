package net.darkhax.caliper.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.darkhax.bookshelf.command.Command;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;

public class CommandCount extends Command {

    @Override
    public String getName () {

        return "count";
    }

    @Override
    public int getRequiredPermissionLevel () {

        return 0;
    }

    @Override
    public String getUsage (ICommandSender sender) {

        return "/caliper count entity|tile|ticktile|chunk";
    }

    @Override
    public void execute (MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        Map<String, Integer> entries = new HashMap<>();

        if (args.length == 1) {

            if ("entity".equalsIgnoreCase(args[0])) {

                for (final WorldServer world : server.worlds) {

                    for (final Entity entity : world.loadedEntityList) {

                        final String name = entity instanceof EntityPlayer ? "Player" : entity instanceof EntityItem ? "Dropped Item" : entity.getName();
                        entries.put(name, entries.getOrDefault(name, 0) + 1);
                    }
                }
            }

            else if ("tile".equalsIgnoreCase(args[0])) {

                for (final WorldServer world : server.worlds) {

                    for (final TileEntity tile : world.loadedTileEntityList) {

                        final ResourceLocation tileId = TileEntity.getKey(tile.getClass());
                        final String name = tileId != null ? tileId.toString() : "unregistered:" + tile.getClass().getName();
                        entries.put(name, entries.getOrDefault(name, 0) + 1);
                    }
                }
            }

            else if ("ticktile".equalsIgnoreCase(args[0])) {

                for (final WorldServer world : server.worlds) {

                    for (final TileEntity tile : world.loadedTileEntityList) {

                        if (tile instanceof ITickable) {

                            final ResourceLocation tileId = TileEntity.getKey(tile.getClass());
                            final String name = tileId != null ? tileId.toString() : "unregistered:" + tile.getClass().getName();
                            entries.put(name, entries.getOrDefault(name, 0) + 1);
                        }
                    }
                }
            }

            else if ("chunk".equalsIgnoreCase(args[0])) {

                for (final WorldServer world : server.worlds) {

                    if (world.getChunkProvider() != null && world.provider != null) {

                        entries.put(world.provider.getDimensionType().getName(), world.getChunkProvider().getLoadedChunkCount());
                    }
                }
            }
        }

        else {

            sender.sendMessage(new TextComponentString("Please specify what to count. Valid options are entity, tile, ticktile, chunk"));
        }

        entries = sortByValue(entries);

        for (final Entry<String, Integer> s : entries.entrySet()) {

            sender.sendMessage(new TextComponentString(s.getKey() + ": " + s.getValue()));
        }
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue (Map<K, V> map) {

        final List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        final Map<K, V> result = new LinkedHashMap<>();
        for (final Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
