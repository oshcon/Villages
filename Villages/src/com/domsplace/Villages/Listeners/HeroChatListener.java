package com.domsplace.Villages.Listeners;

import com.domsplace.Villages.Bases.Base;
import com.domsplace.Villages.Bases.VillageListener;
import com.domsplace.Villages.Objects.Resident;
import com.domsplace.Villages.Objects.Village;
import com.dthielke.herochat.ChannelChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class HeroChatListener extends VillageListener {
    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void handleVillageHeroChat(ChannelChatEvent e) {
        if(!inVillageWorld(e.getSender().getPlayer())) return;
        String prefix = Base.getVillagePrefix(Village.getPlayersVillage(Resident.getResident(e.getSender().getPlayer())));
        e.setFormat(e.getFormat().replaceAll("\\{village\\}", prefix));
    }
}
