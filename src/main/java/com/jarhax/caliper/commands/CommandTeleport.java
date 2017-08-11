package com.jarhax.caliper.commands;

import org.apache.commons.lang3.math.NumberUtils;

import net.darkhax.bookshelf.command.Command;
import net.darkhax.bookshelf.util.PlayerUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandTeleport extends Command {

    @Override
    public String getName () {

        return "tp";
    }

    @Override
    public int getRequiredPermissionLevel () {

        return 2;
    }

    @Override
    public String getUsage (ICommandSender sender) {

        return "commands.tp.usage";
    }

    @Override
    public void execute (MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        EntityPlayerMP player = null;
        int dimension = 0;

        if (args.length == 1 && sender instanceof EntityPlayerMP) {

            player = (EntityPlayerMP) sender;
            dimension = this.getDimension(args[0]);
        }

        else if (args.length == 2) {

            player = getPlayer(server, sender, args[0]);
            dimension = this.getDimension(args[1]);
        }

        if (player == null) {

            throw new CommandException("command.caliper.tp.badplayer");
        }

        PlayerUtils.changeDimension(player, dimension);
    }

    private int getDimension (String arg) throws CommandException {

        if (!NumberUtils.isParsable(arg)) {

            throw new CommandException("command.caliper.tp.baddim");
        }

        return Integer.parseInt(arg);
    }
}
