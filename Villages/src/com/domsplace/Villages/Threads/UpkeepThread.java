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

package com.domsplace.Villages.Threads;

import com.domsplace.Villages.Bases.Base;
import com.domsplace.Villages.Bases.VillageThread;
import com.domsplace.Villages.Objects.Tax;
import com.domsplace.Villages.Objects.TaxData;
import com.domsplace.Villages.Objects.Village;

public class UpkeepThread extends VillageThread {
    public UpkeepThread() {
        super(1, 10, true);
    }
    
    @Override
    public void run() {
        for(Tax t : Tax.getTaxes()) {
            for(Village v : Village.getVillages()) {
                TaxData td = v.getTaxData(t);
                if(td == null) {
                    td = new TaxData(v, t);
                    v.addTaxData(td);
                }
                
                long lastChecked = td.getLastChecked();
                long now = Base.getNow();
                
                long diff = (long) (t.getHours() * 3600000d); //Hours to Milliseconds
                
                long difference = now - lastChecked;
                if(difference < diff) continue;
                
                td.run();
            }
        }
    }
}
