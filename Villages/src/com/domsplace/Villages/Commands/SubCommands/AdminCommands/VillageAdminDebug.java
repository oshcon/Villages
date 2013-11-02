package com.domsplace.Villages.Commands.SubCommands.AdminCommands;

import com.domsplace.Villages.Bases.Base;
import com.domsplace.Villages.Bases.BukkitCommand;
import com.domsplace.Villages.Bases.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class VillageAdminDebug extends SubCommand {  
    public VillageAdminDebug() {
        super("village", "admin", "debug");
        this.setPermission("admin.debug");
    }
    
    @Override
    public boolean cmd(BukkitCommand bkcmd, CommandSender sender, Command cmd, String label, String[] args) {
        Base.DebugMode = !Base.DebugMode;
        debug(getPlugin().getName() + " Debug Mode Enabled!");
        sendMessage(sender, "Turned Debug Mode " + ChatImportant + (Base.DebugMode ? "On" : "Off"));
        return true;
    }
}
