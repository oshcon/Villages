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

package com.domsplace.Villages.DataManagers;

import com.domsplace.Villages.Bases.DataManager;
import com.domsplace.Villages.Enums.ManagerType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Dominic Masters
 */
public class HelpManager extends DataManager {
    public Map<String, String> helps = new HashMap<String, String>();
    
    public HelpManager() {
        super(ManagerType.HELP);
    }
    
    @Override
    public void tryLoad() throws IOException {
        InputStream rulesIS = getPlugin().getResource("defaultHelp.txt");
        BufferedReader rules = new BufferedReader(new InputStreamReader(rulesIS));
        String line;
        
        List<String> helps = new ArrayList<String>();
        while((line = rules.readLine()) != null) {
            helps.add(line);
        }
        
        rules.close();
        
        this.helps = new HashMap<String, String>();
        for(String s : helps) {
            try {
                String[] x = s.split("\\|");
                this.helps.put(x[0], x[1]);
            } catch(Exception e) {}
        }
        
        rules.close();
    }
}
