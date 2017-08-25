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

package com.domsplace.Villages.Hooks;

import com.domsplace.Villages.Bases.Base;
import com.domsplace.Villages.Bases.PluginHook;
import com.domsplace.Villages.Listeners.LegacyTagAPIListener;
import com.domsplace.Villages.Listeners.TagAPIListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TagAPIHook extends PluginHook {
    private LegacyTagAPIListener legacyListener;
    private TagAPIListener listener;

    public TagAPIHook() {
        super("TagAPI");
    }

//    public TagAPI getTagAPI() {
//        return (TagAPI) this.getHookedPlugin();
//    }
//
//    public void refreshTags(Player p) {
//        if(!this.isHooked()) return;
//        try {
//            TagAPI.refreshPlayer(p);
//        } catch(NoClassDefFoundError e) {
//        }
//    }
//
//    public void refreshTags() {
//        for(Player p : Bukkit.getOnlinePlayers()) {
//            if(!Base.inVillageWorld(p)) return;
//            refreshTags(p);
//        }
//    }
//
//    public boolean useLegacy() {
//        try {
//            return !Class.forName("AsyncPlayerReceiveNameTagEvent").equals(null);
//        } catch(Throwable t) {
//        }
//        return false;
//    }
//
//    @Override
//    public void onHook() {
//        super.onHook();
//        Base.useTagAPI = true;
//        if(useLegacy()) {
//            this.legacyListener = new LegacyTagAPIListener();
//        } else {
//            this.listener = new TagAPIListener();
//        }
//    }
//
//    @Override
//    public void onUnhook() {
//        super.onUnhook();
//        Base.useTagAPI = false;
//
//        if(this.legacyListener != null) {
//            this.legacyListener.deRegisterListener();
//            this.legacyListener = null;
//        }
//
//        if(this.listener != null) {
//            this.listener.deRegisterListener();
//            this.listener = null;
//        }
//    }
}
