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

package com.domsplace.Villages.Enums;

import com.domsplace.Villages.Bases.Enum;

public class ManagerType extends Enum {
    public static final ManagerType CONFIG = new ManagerType("Configuration");
    public static final ManagerType LANGUAGE = new ManagerType("Language");
    public static final ManagerType PLUGIN = new ManagerType("Plugin");
    public static final ManagerType UPKEEP = new ManagerType("Upkeep");
    public static final ManagerType SQL = new ManagerType("SQL");
    public static final ManagerType VILLAGE = new ManagerType("Village");
    public static final ManagerType VILLAGE_BANK = new ManagerType("Village Bank");
    public static final ManagerType HELP = new ManagerType("Help");
    public static final ManagerType CRAFT_BUKKIT = new ManagerType("CraftBukkit");
    
    //Instance
    private String type;
    
    public ManagerType(String type) {
        this.type = type;
    }
    
    public String getType() {
        return this.type;
    }
}
