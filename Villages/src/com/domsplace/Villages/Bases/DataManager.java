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

package com.domsplace.Villages.Bases;

import com.domsplace.Villages.DataManagers.*;
import com.domsplace.Villages.Enums.ManagerType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataManager extends Base {
    private static final List<DataManager> MANAGERS = new CopyOnWriteArrayList<DataManager>();
    
    public static final PluginManager PLUGIN_MANAGER = new PluginManager();
    public static final ConfigManager CONFIG_MANAGER = new ConfigManager();
    public static final LanguageManager LANGUAGE_MANAGER = new LanguageManager();
    public static final SQLManager SQL_MANAGER = new SQLManager();
    public static final GUIManager GUI_MANAGER = new GUIManager();
    public static final UpkeepManager UPKEEP_MANAGER = new UpkeepManager();
    public static final VillageManager VILLAGE_MANAGER = new VillageManager();
    public static final HelpManager HELP_MANAGER = new HelpManager();
    public static final CraftBukkitManager CRAFT_BUKKIT_MANAGER = new CraftBukkitManager();
    public static final DataSyncManager DATA_SYNC_MANAGER = new DataSyncManager(180);
    
    private static void registerManager(DataManager manager) {
        DataManager.MANAGERS.add(manager);
    }
    
    public static List<DataManager> getManagers() {
        return new CopyOnWriteArrayList<DataManager>(MANAGERS);
    }
    
    public static boolean loadAll() {
    	for(DataManager dm : MANAGERS) {
    	if(dm.load()) continue;
    	return false;
    	}
        
        return true;
    }
    
    public static boolean saveAll() {
        for(DataManager dm : MANAGERS) {
            if(dm.getType().equals(ManagerType.CONFIG)) continue;
            if(dm.getType().equals(ManagerType.LANGUAGE)) continue;
            if(dm.save()) continue;
            return false;
        }
        
        return true;
    }
    
    //Instance
    private ManagerType type;
    
    public DataManager(ManagerType type) {
        this.type = type;
        
        DataManager.registerManager(this);
    }
    
    public ManagerType getType() {
        return this.type;
    }
    
    public boolean load() {
        try {
            tryLoad();
            return true;
        } catch(IOException e) {
            error("Failed to load " + this.getType().getType(), e);
            return false;
        }
    }
    
    public void tryLoad() throws IOException {
    }
    
    public boolean save() {
        try {
            trySave();
            return true;
        } catch(IOException e) {
            error("Failed to save " + this.getType().getType(), e);
            return false;
        }
    }
    
    public void trySave() throws IOException {
    }
}
