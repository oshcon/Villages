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

package com.domsplace.Villages.Commands.SubCommands.AdminCommands;

import com.domsplace.Villages.Bases.BukkitCommand;
import com.domsplace.Villages.Bases.SubCommand;
import com.domsplace.Villages.Enums.DeleteCause;
import com.domsplace.Villages.Events.VillageDeletedEvent;
import com.domsplace.Villages.Objects.Resident;
import com.domsplace.Villages.Objects.Village;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class VillageAdminDelete extends SubCommand {  
    public VillageAdminDelete() {
        super("village", "admin", "delete");
        this.setPermission("admin.delete");
    }
    
    @Override
    public boolean cmd(BukkitCommand bkcmd, CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length < 1) {
            sk(sender, "neednamedelete");
            return false;
        }
        
        Village v = Village.getVillage(args[0]);
        if(v == null) {
            sk(sender, "villagedoesntexist");
            return true;
        }
        
        Resident r = null;
        if(isPlayer(sender)) {
            r = Resident.getResident(getPlayer(sender));
        }
        
        //Fire Event
        VillageDeletedEvent event = new VillageDeletedEvent(v, DeleteCause.ADMIN_DELETE, r);
        event.fireEvent();
        if(event.isCancelled()) return true;
        
        sk(sender, "villagedelete", v);
        Village.deleteVillage(v);
        return true;
    }
}
