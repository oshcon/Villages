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
