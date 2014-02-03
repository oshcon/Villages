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
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class VillageMessage extends SubCommand {
    public VillageMessage() {
        super("village", "msg");
        this.setPermission("message");
    }
    
    @Override
    public boolean cmd(BukkitCommand bkcmd, CommandSender sender, Command cmd, String label, String[] args) {
        //Make sure it's a player running the command
        if(!isPlayer(sender)) {
            sk(sender, "playeronly");
            return false;
        }
        
        if(args.length < 1) {
            sk(sender, "entermessage");
            return false;
        }
        
        Village v = Village.getPlayersVillage(Resident.getResident(getPlayer(sender)));
        if(v == null) {
            sk(sender, "notinvillage");
            return true;
        }
        
        //Check if Muted (SELBans)
        if(isMuted(getPlayer(sender))) {
            sk(sender, "muted");
            return true;
        }
        
        String message = getVillagePrefix(v) + sender.getName() + ": " + ChatColor.WHITE;
        for(int i = 0; i < args.length; i++) {
            message += args[i];
            if(i < (args.length - 1)) message += " ";
        }
        
        v.broadcast(message);
        return true;
    }
}
