package com.jarhax.caliper.commands;

import net.darkhax.bookshelf.command.CommandTree;
import net.minecraft.command.ICommandSender;

public class CommandCaliper extends CommandTree {
    
    public CommandCaliper () {
        
        this.addSubcommand(new CommandTeleport());
        this.addSubcommand(new CommandCount());
        this.addSubcommand(new CommandTPS());
        this.addSubcommand(new CommandEmptyRecipe());
    }
    
    @Override
    public int getRequiredPermissionLevel () {
        
        return 0;
    }
    
    @Override
    public String getName () {
        
        return "caliper";
    }
    
    @Override
    public String getUsage (ICommandSender sender) {
        
        return "commands.caliper.usage";
    }
}
