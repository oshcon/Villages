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

package com.domsplace.Villages.DataManagers;

import com.domsplace.Villages.Bases.DataManager;
import com.domsplace.Villages.Enums.ManagerType;
import java.io.IOException;
import java.io.InputStream;
import org.bukkit.configuration.file.YamlConfiguration;

public class GUIManager extends DataManager {
    private YamlConfiguration config;
    
    public GUIManager() {
        super(ManagerType.CONFIG);
    }
    
    public YamlConfiguration getCFG() {
        return config;
    }
    
    @Override
    public void tryLoad() throws IOException {
        InputStream is = getPlugin().getResource("GUI.yml");
        this.config = YamlConfiguration.loadConfiguration(is);
        String gui = config.getString("gui");
        GUIScreen = gui.replaceAll("\\*", "\n").replaceAll("%x%", DataManager.PLUGIN_MANAGER.getVersion()).replaceAll("TAB", "-----");
    }
}
