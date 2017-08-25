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

package com.domsplace.Villages.Listeners;

import com.domsplace.Villages.Events.GriefEvent;
import com.domsplace.Villages.Bases.VillageListener;
import com.domsplace.Villages.Enums.GriefType;
import com.domsplace.Villages.Events.ArrowPvPEvent;
import com.domsplace.Villages.Events.PreCommandEvent;
import com.domsplace.Villages.Events.PvPEvent;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class CustomEventListener extends VillageListener {
    @EventHandler(ignoreCancelled=true)
    public void fireVillageGriefEventBreak(BlockBreakEvent e) {
        if(e.isCancelled()) return;
        if(!inVillageWorld(e.getBlock())) return;
        GriefEvent event = new GriefEvent(e.getPlayer(), e.getBlock(), GriefType.BREAK);
        event.fireEvent();
        if(event.isCancelled()) e.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled=true)
    public void fireVillageGriefEventPlace(BlockPlaceEvent e) {
        if(e.isCancelled()) return;
        if(!inVillageWorld(e.getBlockPlaced())) return;
        GriefEvent event = new GriefEvent(e.getPlayer(), e.getBlockPlaced(), GriefType.PLACE);
        event.fireEvent();
        if(event.isCancelled()) e.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled=true)
    public void fireVillageGriefEventInteract(PlayerInteractEvent e) {
        if(e.isCancelled()) return;
        if(!inVillageWorld(e.getClickedBlock())) return;
        GriefEvent event = new GriefEvent(e.getPlayer(), e.getClickedBlock(), GriefType.INTERACT);
        event.fireEvent();
        if(event.isCancelled()) e.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled=true)
    public void fireVillageGriefEventDamage(BlockDamageEvent e) {
        if(e.isCancelled()) return;
        if(!inVillageWorld(e.getBlock())) return;
        GriefEvent event = new GriefEvent(e.getPlayer(), e.getBlock(), GriefType.BLOCK_DAMAGE);
        event.fireEvent();
        if(event.isCancelled()) e.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled=true)
    public void handlePvPEvent(EntityDamageByEntityEvent e) {
        if(e.getEntity() == null) return;
        if(!inVillageWorld(e.getEntity())) return;
        if(e.getDamager() == null) return;
        if(e.getEntity().getType() == null) return;
        if(e.getDamager().getType() == null) return;
        if(!e.getEntity().getType().equals(EntityType.PLAYER)) return;
        if(!e.getDamager().getType().equals(EntityType.PLAYER)) return;
        
        Player attacked = getPlayer(e.getEntity());
        Player damager = getPlayer(e.getDamager());
        double damage = e.getDamage();
        PvPEvent event = new PvPEvent(attacked, damager, damage);
        event.fireEvent();
        if(event.isCancelled()) e.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled=true)
    public void handleArrowPvPEvent(EntityDamageByEntityEvent e) {
        if(e.getEntity() == null) return;
        if(!inVillageWorld(e.getEntity())) return;
        if(e.getDamager() == null) return;
        if(e.getEntity().getType() == null) return;
        if(e.getDamager().getType() == null) return;
        if(!e.getEntity().getType().equals(EntityType.PLAYER)) return;
        if(!e.getDamager().getType().equals(EntityType.ARROW)) return;
        
        Arrow arrow = (Arrow) e.getDamager();
        if(arrow.getShooter() == null) return;
//        if(arrow.getShooter().getType() == null) return;
//        if(!arrow.getShooter().getType().equals(EntityType.PLAYER)) return;
        if(!(arrow.getShooter() instanceof Player)) return;
        
        Player attacked = getPlayer(e.getEntity());
        Player damager = getPlayer(arrow.getShooter());
        double damage = e.getDamage();
        ArrowPvPEvent event = new ArrowPvPEvent(attacked, damager, arrow, damage);
        event.fireEvent();
        if(event.isCancelled()) e.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled=true)
    public void handlePreCommandEvent(PlayerCommandPreprocessEvent e) {
        if(e.isCancelled()) return;
        if(e.getPlayer() == null) return;
        if(e.getMessage().equalsIgnoreCase(" ")) return;
        
        if(!e.getMessage().replaceAll(" ", "").startsWith("/")) return;
        
        String[] parts = e.getMessage().split(" ");
        if(parts.length < 1) return;
        
        String command = parts[0].replaceFirst("/", "");
        List<String> args = new ArrayList<String>();
        if(parts.length > 1) {for(int i = 1; i < parts.length; i++) {args.add(parts[i]);}}
        
        PreCommandEvent event = new PreCommandEvent(e.getPlayer(), command, args);
        event.fireEvent();
        if(event.isCancelled()) e.setCancelled(event.isCancelled());
    }
    
    @EventHandler(ignoreCancelled=true)
    public void handlePreServerCommandEvent(ServerCommandEvent e) {
        if(e.getSender() == null) return;
        if(e.getCommand().equalsIgnoreCase(" ")) return;
        
        //All Server stuffs are commans ;)
        //if(!e.getCommand().replaceAll(" ", "").startsWith("/")) return;
        
        String[] parts = e.getCommand().split(" ");
        if(parts.length < 1) return;
        
        String command = parts[0];
        List<String> args = new ArrayList<String>();
        if(parts.length > 1) {for(int i = 1; i < parts.length; i++) {args.add(parts[i]);}}
        
        PreCommandEvent event = new PreCommandEvent(e.getSender(), command, args);
        event.fireEvent();
        if(event.isCancelled()) {
            e.setCommand("");
        }
    }
}
