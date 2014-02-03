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
import com.domsplace.Villages.Objects.Village;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class VillageTop extends SubCommand {
    public VillageTop() {
        super("village", "top");
        this.setPermission("villagetop");
    }
    
    @Override
    public boolean cmd(BukkitCommand bkcmd, CommandSender sender, Command cmd, String label, String[] args) {
        List<Village> dontmod = Village.getVillages();
        List<Village> copy = new ArrayList<Village>(dontmod);
        List<Village> top = new ArrayList<Village>();
        for(int i = 0; i < 5; i++) {
            Village best = null;
            for(Village v : dontmod) {
                if(!copy.contains(v)) continue;
                if(best == null) {
                    best = v;
                    continue;
                }

                if(v.getValue() > best.getValue()) {
                    best = v;
                }
            }
            if(best == null) continue;
            top.add(best);
            copy.remove(best);
        }
        
        List<String> messages = new ArrayList<String>();
        messages.addAll(gk("topvillages", top.size()));
        int i = 1;
        for(Village v : top) {
            messages.add("\t#" + i + ": " + ChatImportant + v.getName());
            i++;
        }
        
        sendMessage(sender, messages);
        return true;
    }
}
