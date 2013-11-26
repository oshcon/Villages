/*
 * Copyright 2013 Dominic Masters.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.domsplace.Villages.Listeners;

import com.domsplace.DomsCommands.Bases.Base;
import static com.domsplace.Villages.Bases.Base.getConfig;
import com.domsplace.Villages.Bases.VillageListener;
import com.domsplace.Villages.Enums.DeleteCause;
import com.domsplace.Villages.Events.VillageDeletedEvent;
import com.domsplace.Villages.Objects.Resident;
import com.domsplace.Villages.Objects.Village;
import org.bukkit.event.EventHandler;

/**
 *
 * @author Dominic Masters
 */
public class VillageRefundListener extends VillageListener {
    @EventHandler(ignoreCancelled=true)
    public void handleVillageClosed(VillageDeletedEvent e) {
        Resident r = e.getVillage().getMayor();
        Village v = e.getVillage();
        if(e.getCause().equals(DeleteCause.ADMIN_DELETE)) return;
        if(Base.useEcon()) {
            double refund = getConfig().getDouble("refund.closevillage", 0.0d);
            Base.chargePlayer(r.getOfflinePlayer(), -refund);
            Base.chargePlayer(r.getOfflinePlayer(), -v.getBank().getWealth());
            refund = getConfig().getDouble("refund.closevillageperchunk", 0.0d) * ((double) v.getRegions().size());
            Base.chargePlayer(r.getOfflinePlayer(), -refund);
        }
    }
    
}
