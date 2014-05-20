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

package com.domsplace.Villages.Objects;

import com.domsplace.Villages.Bases.Base;
import com.domsplace.Villages.Bases.Helpable;
import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpTopic;

public class VillageHelpTopic extends HelpTopic {
    private Helpable topic;
    
    public VillageHelpTopic(Helpable topic) {
        this.topic = topic;
    }
    
    @Override
    public boolean canSee(CommandSender cs) {
        return Base.hasPermission(cs, this.topic.getHelpPermission());
    }

    @Override
    public void amendCanSee(String string) {
        this.topic.setHelpPermission(string);
    }

    @Override
    public String getName() {
        return this.topic.getHelpTopic();
    }

    @Override
    public String getShortText() {
        return this.topic.getHelpTextShort("");
    }

    @Override
    public String getFullText(CommandSender cs) {
        return this.topic.getHelpTextLarge(cs);
    }
}
