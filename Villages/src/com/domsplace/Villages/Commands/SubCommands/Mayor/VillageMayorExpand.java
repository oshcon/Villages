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

package com.domsplace.Villages.Commands.SubCommands.Mayor;

import com.domsplace.Villages.Bases.Base;
import com.domsplace.Villages.Bases.BukkitCommand;
import com.domsplace.Villages.Bases.DataManager;
import com.domsplace.Villages.Bases.PluginHook;
import com.domsplace.Villages.Bases.SubCommand;
import com.domsplace.Villages.Enums.ExpandMethod;
import com.domsplace.Villages.Events.VillageExpandEvent;
import com.domsplace.Villages.Objects.Region;
import com.domsplace.Villages.Objects.Resident;
import com.domsplace.Villages.Objects.Village;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class VillageMayorExpand extends SubCommand {
    public VillageMayorExpand() {
        this("mayor");
    }
    
    public VillageMayorExpand(String alias) {
        super("village", alias, "expand");
        this.setPermission("mayor.expand");
    }
    
    @Override
    public boolean cmd(BukkitCommand bkcmd, CommandSender sender, Command cmd, String label, String[] args) {
        if(!isPlayer(sender)) {sk(sender, "playeronly");return true;}
        
        Resident r = Resident.getResident(getPlayer(sender));
        Village v = Village.getPlayersVillage(r);
        if(v == null) {sk(sender, "notinvillage");return true;}
        if(!v.isMayor(r)) {sk(sender, "onlymayorexpand"); return true;}
        
        List<Region> claiming = new ArrayList<Region>();
        if(Base.ExpandingMethod.equals(ExpandMethod.PER_CHUNK)) {
            Region region = Region.getRegion(getPlayer(sender).getLocation());
            if(region == null) return true; //Make sure Region is Valid
            
            //Check to make sure this Region borders the Village
            if(!v.doesRegionBorder(region)) {
                sk(sender, "cantexpandborder");
                return true;
            }
            
            claiming.add(region);
        } else if(Base.ExpandingMethod.equals(ExpandMethod.CLASSIC)) {
            //Get current "classic size" of Village
            int size = 1;
            List<Region> chunks = v.getRegions();
            if(chunks.size() > 1) {
                size = (int) Math.ceil((Math.sqrt(chunks.size()) / 2.0d) + 0.5d);
            }
            
            //Get Chunks that border
            int x = v.getSpawnRegion().getRegionX();
            int z = v.getSpawnRegion().getRegionZ();
        
            for(int cx = -size; cx <= size; cx++) {
                for(int cz = -size; cz <= size; cz++) {
                    Region re = v.getSpawnRegion().getRelativeRegion(cx, cz);
                    if(r == null) {
                        continue;
                    }
                    
                    if(!(((cx == -size) || (cx == size))  || ((cz == -size) || (cz == size)))) {continue;}
                    
                    claiming.add(re);
                }
            }
        }
        
        for(Region region : claiming) {
            //Make sure the Region is in the same world as their Village
            if(!region.getWorld().equalsIgnoreCase(v.getSpawn().getWorld())) {
                sk(sender, "notinthisworld");
                return true;
            }
            
            //Make Sure region isn't claimed
            if(Village.doesRegionOverlapVillage(region)) {
                sk(sender, "expandvillageoverlap");
                return true;
            }

            //Make sure they have WorldGuard Perms
            if(Base.useWorldGuard) {
                if(PluginHook.WORLD_GUARD_HOOK.isOverlappingRegion(region)) {
                    sk(sender, "expandregionoverlap");
                    return true;
                }
            }
        }
        
        //Charge Village on Per-Chunk basis
        double cost = getCost("expandvillage") * (double)claiming.size();
        
        //First, make sure they have the money to expand
        if(Base.useEconomy() && getConfig().getBoolean("features.banks.money", true)  && !hasBalance(v, cost)) {
            sk(sender, "villagebankneedmore", PluginHook.VAULT_HOOK.formatEconomy(cost));
            return true;
        }
        
        //Fire Event
        VillageExpandEvent event = new VillageExpandEvent(v, claiming, r);
        event.fireEvent();
        if(event.isCancelled()) return true;
        
        //Charge Village
        if(Base.useEconomy()) {
            if(getConfig().getBoolean("features.banks.money", true)) {
                v.getBank().addWealth(-cost);
            } else {
                Base.chargePlayer(sender, cost);
            }
        }
        
        v.addRegions(claiming);
        sk(sender, "villageexpanded", claiming.size());
        DataManager.saveAll();
        return true;
    }
}
