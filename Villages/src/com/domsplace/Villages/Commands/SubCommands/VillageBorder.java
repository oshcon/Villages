package com.domsplace.Villages.Commands.SubCommands;

import static com.domsplace.Villages.Bases.Base.getPlayer;
import static com.domsplace.Villages.Bases.Base.sk;
import com.domsplace.Villages.Bases.BukkitCommand;
import com.domsplace.Villages.Bases.SubCommand;
import com.domsplace.Villages.Objects.Resident;
import com.domsplace.Villages.Objects.Village;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class VillageBorder extends SubCommand {
    public VillageBorder() {
        super("village", "border");
        this.setPermission("border");
    }
    
    @Override
    public boolean cmd(BukkitCommand bkcmd, CommandSender sender, Command cmd, String label, String[] args) {
        //Make sure it's a player running the command
        if(!isPlayer(sender)) {
            sk(sender, "playeronly");
            return false;
        }
        
        //Make sure player is in a Village world
        if(!inVillageWorld(sender)) {
            sk(sender, "notinthisworld");
            return true;
        }
        
        Resident player = Resident.getResident(getPlayer(sender));
        Village playersVillage = Village.getPlayersVillage(player);
        if(player == null || playersVillage == null) {
            sk(sender, "notinvillage");
            return true;
        }
        
        player.setShowBorder(!player.getShowBorder());
        
        if(player.getShowBorder()) {
            sk(sender, "showingborder");
        } else {
            sk(sender, "hidingborder");
        }
        return true;
    }
}
