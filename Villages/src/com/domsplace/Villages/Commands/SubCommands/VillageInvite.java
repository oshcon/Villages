/*
 * Copyright 2013 Dominic Masters and Jordan Atkins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.domsplace.Villages.Commands.SubCommands;

import com.domsplace.Villages.Bases.BukkitCommand;
import com.domsplace.Villages.Bases.SubCommand;
import com.domsplace.Villages.Objects.Resident;
import com.domsplace.Villages.Objects.Village;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class VillageInvite extends SubCommand {
    public static final Map<Resident, Village> VILLAGE_INVITES = new HashMap<Resident, Village>();
    
    public VillageInvite() {
        super("village", "invite");
        this.setPermission("invite");
    }
    
    @Override
    public boolean cmd(BukkitCommand bkcmd, CommandSender sender, Command cmd, String label, String[] args) {
        if(!isPlayer(sender)) {
            sk(sender, "playeronly");
            return true;
        }
        
        if(args.length < 1) {
            sk(sender, "enterplayer");
            return false;
        }
        
        Resident target = Resident.guessResident(args[0]);
        if(target == null || !target.getOfflinePlayer().isOnline()) {
            sk(sender, "playernotfound");
            return true;
        }
        
        Village v = Village.getPlayersVillage(target);
        if(v != null) {
            sk(sender, "playerinvillage", target);
            return true;
        }
        
        Resident player = Resident.getResident(getPlayer(sender));
        Village playersVillage = Village.getPlayersVillage(player);
        if(player == null || playersVillage == null) {
            sk(sender, "notinvillage");
            return true;
        }
        
        VILLAGE_INVITES.remove(target);
        VILLAGE_INVITES.put(target, playersVillage);
        
        sk(target.getPlayer(), "villageinvite", player, playersVillage);
        playersVillage.broadcast(gk("residentinvited", target));
        return true;
    }
}
