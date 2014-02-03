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

package com.domsplace.Villages.Events;

import com.domsplace.Villages.Bases.CancellableEvent;
import org.bukkit.entity.Player;

public class PvPEvent extends CancellableEvent {
    private Player attacked;
    private Player attacker;
    private double damage;
    
    public PvPEvent(Player attacked, Player attacker, double damage) {
        this.attacked = attacked;
        this.attacker = attacker;
        this.damage = damage;
    }
    
    public Player getAttackedPlayer() {
        return this.attacked;
    }
    
    public Player getAttacker() {
        return this.attacker;
    }
    
    public double getDamage() {
        return this.damage;
    }
}
