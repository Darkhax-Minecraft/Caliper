package com.jarhax.caliper.commands;

import com.jarhax.caliper.MiscUtils;
import com.jarhax.caliper.TableBuilder;

import net.darkhax.bookshelf.command.Command;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;

public class CommandTPS extends Command {

    @Override
    public String getName () {

        return "tps";
    }

    @Override
    public String getUsage (ICommandSender sender) {

        return "commands.tps.usage";
    }

    @Override
    public void execute (MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        final TableBuilder<WorldServer> builder = new TableBuilder<>();
        builder.setNewLine("\n");
        builder.addColumn("Name", world -> MiscUtils.getWorldName(world));
        builder.addColumn("Entities", world -> world.loadedEntityList.size());
        builder.addColumn("Tiles", world -> world.loadedTileEntityList.size());
        builder.addColumn("Chunks", world -> MiscUtils.getChunkCount(world));

        for (final WorldServer world : server.worlds) {

            builder.addEntry(world);
        }

        sender.sendMessage(new TextComponentString(builder.createString()));
    }

    @Override
    public int getRequiredPermissionLevel () {

        return 0;
    }
}
