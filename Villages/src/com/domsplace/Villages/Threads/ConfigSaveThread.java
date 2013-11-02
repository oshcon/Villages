package com.domsplace.Villages.Threads;

import com.domsplace.Villages.Bases.DataManager;
import com.domsplace.Villages.Bases.VillageThread;

public class ConfigSaveThread extends VillageThread {
    public ConfigSaveThread() {
        super(3, 1500, true);
    }
    
    @Override
    public void run() {
        //log("Saving Data...");
        if(!DataManager.saveAll()) log("Failed to save Data!\nTry a manual save!");
    }
}
