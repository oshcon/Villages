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

package com.domsplace.Villages.Objects;

import com.domsplace.Villages.Bases.Base;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Resident {
    private static final List<Resident> RESIDENTS = new ArrayList<Resident>();
    
    public static Resident registerResident(OfflinePlayer player) {
        Resident r = new Resident(player.getName());
        RESIDENTS.add(r);
        return r;
    }
    
    public static Resident getResident(Player player) {
        return getResident(Base.getOfflinePlayer(player));
    }
    
    public static Resident getResident(CommandSender player) {
        if(!Base.isPlayer(player)) return null;
        return getResident(Base.getPlayer(player));
    }
    
    public static Resident getResident(String player) {
        if(player == null) return null;
        return getResident(Base.getOfflinePlayer(player));
    }
    
    public static Resident getResident(Entity player) {
        if(player == null) return null;
        if(!Base.isPlayer(player)) return null;
        return getResident(Base.getPlayer(player));
    }
    
    public static Resident getResident(OfflinePlayer player) {
        for(Resident r : RESIDENTS) {
            if(r.getName().equalsIgnoreCase(player.getName())) return r;
        }
        
        return registerResident(player);
    }
    
    public static Resident guessResident(String string) {
        for(Resident r : RESIDENTS) {
            if(r.getName().toLowerCase().startsWith(string.toLowerCase())) return r;
        }
        
        return null;
    }

    public static List<Resident> getRegisteredResidents() {
        return new ArrayList<Resident>(RESIDENTS);
    }
    
    //Instance
    private final String player;
    private boolean showBorder;
    
    private Resident(String player) {
        this.player = player;
        this.showBorder = false;
    }
    
    public String getName() {return this.player;}
    public OfflinePlayer getOfflinePlayer() {return Bukkit.getOfflinePlayer(this.player);}
    public Player getPlayer() {return this.getOfflinePlayer().getPlayer();}
    public boolean getShowBorder() {return this.showBorder;}
    
    public void setShowBorder(boolean t) {this.showBorder = t;}

    public void teleport(Region spawn) {
        this.getPlayer().teleport(spawn.getSafeMiddle());
    }
    
    public void teleport(DomsLocation spawn) {
        this.getPlayer().teleport(spawn.toLocation());
    }
}
