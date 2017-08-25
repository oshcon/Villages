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
import java.io.InputStreamReader;

import org.bukkit.configuration.file.YamlConfiguration;

public class PluginManager extends DataManager {
    private YamlConfiguration plugin;
    
    public PluginManager() {
        super(ManagerType.PLUGIN);
    }
    
    @Override
    public void tryLoad() throws IOException {
        if(!getDataFolder().exists()) getDataFolder().mkdir();
        InputStream is = getPlugin().getResource("plugin.yml");
        plugin = YamlConfiguration.loadConfiguration(new InputStreamReader(is));
        is.close();
    }

    public String getVersion() {
        return plugin.getString("version");
    }

    public String getAuthor() {
        return plugin.getString("author", "Dominic");
    }
}
