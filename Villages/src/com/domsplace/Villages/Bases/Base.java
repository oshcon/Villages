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

import com.domsplace.Villages.Hooks.VaultHook;
import com.domsplace.Villages.DataManagers.CraftBukkitManager;
import com.domsplace.Villages.Enums.ExpandMethod;
import com.domsplace.Villages.GUI.VillagesGUIManager;
import com.domsplace.Villages.Objects.Resident;
import com.domsplace.Villages.Objects.Village;
import com.domsplace.Villages.VillagesPlugin;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Base extends RawBase {
    public static final String TAB = "    ";
    public static final String OVERRIDE_PERMISSION = "Villages.admin.override";
    public static final String BUKKIT_PAGE = "http://dev.bukkit.org/bukkit-plugins/villages/";
    
    public static String GUIScreen;
    
    public static boolean DebugMode = false;
    public static VillagesPlugin plugin;
    
    public static String ChatDefault = ChatColor.GRAY.toString();
    public static String ChatImportant = ChatColor.BLUE.toString();
    public static String ChatError = ChatColor.RED.toString();
    
    public static String ChatPrefix = "&9[&7Villages&9]";
    public static String VillagePrefix = "&9[&7%v%&9]";
    public static String WildernessPrefix = "&9[&7Wilderness&9]";
    
    public static String WildernessName = "Wilderness";
    
    public static String FriendColor = "&a";
    public static String EnemyColor = "&4";
    
    private static String permissionMessage = "&4You don't have permission to do this!";
    
    public static List<World> VillageWorlds = new ArrayList<World>();
    public static List<String> TryWorlds = new ArrayList<String>();
    
    public static ExpandMethod ExpandingMethod = ExpandMethod.PER_CHUNK;
    
    public static VillagesGUIManager guiManager;
    
    //HOOKING OPTIONS
    public static boolean useSQL = false;
//    public static boolean useWorldGuard = false;
//    public static boolean useTagAPI = false;
    public static boolean useScoreboards = false;
    
    //String Utils
    public static String getPrefix() {
        if(!ChatPrefix.contains("§")) ChatPrefix = colorise(ChatPrefix);
        if(ChatPrefix.equalsIgnoreCase("")) return "";
        if(ChatPrefix.endsWith(" ")) return ChatPrefix;
        return ChatPrefix + " ";
    }
    
    public static String getVillagePrefix(Village v) {
        String p;
        if(v != null) {
            p = VillagePrefix.replaceAll("%v%", v.getName());
        } else {
            p = VillagePrefix.replaceAll("%v%", WildernessName);
        }
        
        if(!p.contains("§")) p = colorise(p);
        if(p.replaceAll(" ", "").equalsIgnoreCase("")) return "";
        return p;
    }
    
    public static String getDebugPrefix() {
        return ChatColor.LIGHT_PURPLE + "DEBUG: " + ChatColor.AQUA;
    }
    
    public static String colorise(Object o) {
        String msg = o.toString();
        
        String[] andCodes = {"&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", 
            "&8", "&9", "&a", "&b", "&c", "&d", "&e", "&f", "&l", "&o", "&n", 
            "&m", "&k", "&r"};
        
        String[] altCodes = {"§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", 
            "§8", "§9", "§a", "§b", "§c", "§d", "§e", "§f", "§l", "§o", "§n", 
            "§m", "§k", "§r"};
        
        for(int x = 0; x < andCodes.length; x++) {
            msg = msg.replaceAll(andCodes[x], altCodes[x]);
        }
        
        return msg;
    }
    
    public static String decolorise(Object o) {
        String msg = o.toString();
        
        String[] andCodes = {"&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", 
            "&8", "&9", "&a", "&b", "&c", "&d", "&e", "&f", "&l", "&o", "&n", 
            "&m", "&k", "&r"};
        
        String[] altCodes = {"§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", 
            "§8", "§9", "§a", "§b", "§c", "§d", "§e", "§f", "§l", "§o", "§n", 
            "§m", "§k", "§r"};
        
        for(int x = 0; x < andCodes.length; x++) {
            msg = msg.replaceAll(altCodes[x], andCodes[x]);
        }
        
        return msg;
    }
    
    public static String getPermissionMessage() {
        return Base.permissionMessage;
    }
    
    public static void setPermissionMessage(String msg) {
        Base.permissionMessage = msg;
    }

    public static String[] listToArray(List<String> c) {
        String[] s = new String[c.size()];
        for(int i = 0; i < c.size(); i++) {
            s[i] = c.get(i);
        }
        
        return s;
    }
    
    public static String capitalizeFirstLetter(String s) {
        if(s.length() < 2) return s.toUpperCase();
        String end = s.substring(1, s.length());
        return s.substring(0, 1).toUpperCase() + end;
    }

    public static String capitalizeEachWord(String toLowerCase) {
        String[] words = toLowerCase.split(" ");
        String w = "";
        for(int i = 0; i < words.length; i++) {
            w += capitalizeFirstLetter(words[i]);
            if(i < (words.length-1)) {
                w += " ";
            }
        }
        return w;
    }
    
    public static String arrayToString(Object[] array) {
        return Base.arrayToString(array, " ");
    }
    
    public static String arrayToString(Object[] array, String seperator) {
        String m = "";
        for(int i = 0; i < array.length; i++) {
            m += array[i].toString();
            if(i < (array.length - 1)) {
                m += seperator;
            }
        }
        
        return m;
    }
    
    public static String trim(String s, int length) {
        if(s.length() < length) return s;
        return s.substring(0, length);
    }
    
    //Messaging Utils
    public static boolean sendRawMessage(Player player, String message) {
        if(!CraftBukkitManager.CRAFT_BUKKIT_MANAGER.canFindCraftBukkit()) return false;
        Class IChatBaseComponent = CraftBukkitManager.CRAFT_BUKKIT_MANAGER.getMineClass("IChatBaseComponent");
        Class ChatSerializer = CraftBukkitManager.CRAFT_BUKKIT_MANAGER.getMineClass("ChatSerializer");
        Class PacketPlayOutChat = CraftBukkitManager.CRAFT_BUKKIT_MANAGER.getMineClass("PacketPlayOutChat");
        Class CraftPlayer = CraftBukkitManager.CRAFT_BUKKIT_MANAGER.getCraftClass("entity.CraftPlayer");
        Class Packet = CraftBukkitManager.CRAFT_BUKKIT_MANAGER.getMineClass("Packet");
        Class PlayerConnection = CraftBukkitManager.CRAFT_BUKKIT_MANAGER.getMineClass("PlayerConnection");
        if(IChatBaseComponent == null || ChatSerializer == null || PacketPlayOutChat == null || CraftPlayer == null) return false;
        
        try {
            Object comp = ChatSerializer.getDeclaredMethod("a", String.class).invoke(null, message);
            Object packet = PacketPlayOutChat.getDeclaredConstructor(IChatBaseComponent, boolean.class).newInstance(comp, true);
            Object cPlayer = CraftPlayer.cast(player);
            Object handle = CraftPlayer.getMethod("getHandle").invoke(cPlayer);
            Object playerConnection = handle.getClass().getDeclaredField("playerConnection").get(handle);
            PlayerConnection.getDeclaredMethod("sendPacket", Packet).invoke(playerConnection, packet);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    
    public static void sendMessage(CommandSender sender, String msg) {
        sendMessage(sender, msg, true);
    }
    
    public static void sendMessage(CommandSender sender, String msg, boolean loopback) {
        if(msg.replaceAll(" ", "").equalsIgnoreCase("")) return;
        if(!inVillageWorld(sender)) return;
        msg = msg.replaceAll("\\t", TAB);
        sender.sendMessage(ChatDefault + getPrefix() + ChatDefault + msg);
    }

    public static void sendMessage(CommandSender sender, String msg, Object... objs) {
        String s = msg;
        for(int i = 0; i < objs.length; i++) {
            s = s.replaceAll("{" + i + "}", objs[i].toString());
        }
        sendMessage(sender, s);
    }
    
    public static void sendMessage(CommandSender sender, Object[] msg) {
        for(Object o : msg) {
            sendMessage(sender, o);
        }
    }

    public static void sendMessage(CommandSender sender, List<?> msg) {
        sendMessage(sender, msg.toArray());
    }

    public static void sendMessage(CommandSender sender, Object msg) {
        if(msg == null) return;
        if(msg instanceof String) {
            sendMessage(sender, (String) msg);
            return;
        }
        
        if(msg instanceof Object[]) {
            sendMessage(sender, (Object[]) msg);
            return;
        }
        
        if(msg instanceof List<?>) {
            sendMessage(sender, (List<?>) msg);
            return;
        }
        sendMessage(sender, msg.toString());
    }

    public static void sendMessage(Player sender, Object... msg) {
        sendMessage((CommandSender) sender, msg);
    }

    public static void sendMessage(OfflinePlayer sender, Object... msg) {
        if(!sender.isOnline()) return;
        sendMessage(sender.getPlayer(), msg);
    }

    public static void sendMessage(Entity sender, Object... msg) {
        if(!(sender instanceof CommandSender)) return;
        sendMessage(sender, msg);
    }

    public static void sendMessage(Resident sender, Object... msg) {
        sendMessage(sender.getOfflinePlayer(), msg);
    }
    
    public static void sendMessage(Object o) {
        sendMessage(Bukkit.getConsoleSender(), o);
    }
    
    public static void sendAll(List<Player> players, Object o) {
        for(Player p : players) {
            sendMessage(p, o);
        }
    }
    
    public static void sendAll(Player[] players, Object o) {
        for(Player p : players) {
            sendMessage(p, o);
        }
    }
    
    public static void sendAll(Object o) {
//        sendAll(Bukkit.getOnlinePlayers(), o);
        Collection online = Bukkit.getOnlinePlayers();
        Player[] players = (Player[]) online.toArray(new Player[0]);
        sendAll(players, o);
    }
    
    public static void sendAll(String permission, Object o) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(!hasPermission(p, permission)) continue;
            sendMessage(p, o);
        }
    }
    
    public static void broadcast(Object o) {
        sendMessage(o);
        sendAll(o);
    }
    
    public static void broadcast(String permission, Object o) {
        sendMessage(o);
        sendAll(permission, o);
    }
    
    public static void debug(Object o) {
        if(!DebugMode) return;
        broadcast(getDebugPrefix() + o.toString());
    }
    
    public static void error(String message, boolean postfix) {
        String msg = ChatError + "Error: " + ChatColor.DARK_RED + message;
        if(postfix && DebugMode) msg += ChatColor.YELLOW + " Caused by: ";
        if(postfix && !DebugMode) msg += ChatColor.YELLOW + " Turn debug mode on to view whole error.";
        sendMessage(msg);
    }
    
    public static void error(String message) {
        error(message, false);
    }
    
    public static void error(String message, Exception e) {
        error(message, true);
        if(!DebugMode) return;
        String lines = "\n" + e.getClass().getName() + ":  " +  e.getMessage();
        for(StackTraceElement ste : e.getStackTrace()) {
            
            lines += "\t" + ChatColor.GRAY + "at " + ste.getClassName() + "." 
                    + ste.getMethodName() + "(" + ste.getFileName() + ":" + 
                    ste.getLineNumber() + ")\n";
        }
        
        sendMessage(lines);
    }
    
    public static void log(Object o) {
        getPlugin().getLogger().info(o.toString());
    }
    
    //Conversion Utils
    public static boolean isPlayer(Object o) {
        return o instanceof Player;
    }
    
    public static Player getPlayer(Object o) {
        return (Player) o;
    }
    
    public static Player getPlayer(CommandSender sender, String argument) {
        for(Player plyr : Bukkit.getOnlinePlayers()) {
            if(!canSee(sender, plyr)) continue;
            if(plyr.getName().toLowerCase().startsWith(argument.toLowerCase())) {
                return plyr;
            }
        }
        
        for(Player plyr : Bukkit.getOnlinePlayers()) {
            if(!canSee(sender, plyr)) continue;
            if(plyr.getName().toLowerCase().contains(argument.toLowerCase())) {
                return plyr;
            }
        }
        
        for(Player plyr : Bukkit.getOnlinePlayers()) {
            if(!canSee(sender, plyr)) continue;
            if(plyr.getDisplayName().toLowerCase().contains(argument.toLowerCase())) {
                return plyr;
            }
        }
        return null;
    }
    
    public static OfflinePlayer getOfflinePlayer(Player player) {
        return Bukkit.getOfflinePlayer(player.getName());
    }
    
    public static OfflinePlayer getOfflinePlayer(String player) {
        return Bukkit.getOfflinePlayer(player);
    }
    
    public static boolean isInt(Object o) {
        try {
            Integer.parseInt(o.toString());
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    
    public static int getInt(Object o) {
        return Integer.parseInt(o.toString());
    }
    
    public static double getDouble(Object o) {
        return Double.parseDouble(o.toString());
    }
    
    public static boolean isDouble(Object o) {
        try {
            Double.parseDouble(o.toString());
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    
    public static boolean isShort(Object o) {
        try {
            Short.parseShort(o.toString());
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    
    public static short getShort(Object o) {
        return Short.parseShort(o.toString());
    }
    
    public static boolean isByte(Object o) {
        try {
            Byte.parseByte(o.toString());
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    
    public static byte getByte(Object o) {
        return Byte.parseByte(o.toString());
    }
    
    public static boolean isFloat(Object o) {
        try {
            Long.parseLong(o.toString());
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public static float getFloat(Object o) {
        return Float.parseFloat(o.toString());
    }
    
    public static String listToString(List<String> strings) {
        return listToString(strings, ", ");
    }
    
    public static String listToString(List<String> strings, String seperator) {
        String m = "";
        
        for(int i = 0; i < strings.size(); i++) {
            m += strings.get(i);
            if(i < (strings.size() - 1)) m += seperator;
        }
        
        return m;
    }
    
    //Plugin Utils
    public static void setPlugin(VillagesPlugin plugin) {
        Base.plugin = plugin;
    }
    
    public static VillagesPlugin getPlugin() {
        return plugin;
    }
    
    public static File getDataFolder() {
        return getPlugin().getDataFolder();
    }
    
    public static YamlConfiguration getConfig() {
        return DataManager.CONFIG_MANAGER.getCFG();
    }
    
    //Location Utils
    public static boolean isVillageWorld(World world) {
        for(World w : VillageWorlds) if(w.equals(world)) return true;
        return false;
    }
    
    public static boolean inVillageWorld(Player player) {
        return isVillageWorld(player.getWorld());
    }
    
    public static boolean inVillageWorld(Entity player) {
        return isVillageWorld(player.getWorld());
    }
    
    public static boolean inVillageWorld(Block block) {
        return isVillageWorld(block.getWorld());
    }
    
    public static boolean inVillageWorld(CommandSender sender) {
        if(!isPlayer(sender)) return true;
        return inVillageWorld(getPlayer(sender));
    }
    
    public static String getLocationString(Location location) {
        return location.getX() + ", " + location.getY() + ", " + location.getZ()
                + " " + location.getWorld().getName();
    }
    
    public static String getStringLocation (Chunk chunk) {
        return chunk.getX() + ", " + chunk.getZ() + " : " + chunk.getWorld().getName();
    }
    
    public static boolean isCoordBetweenCoords(int checkX, int checkZ, int outerX, int outerZ, int maxX, int maxZ) {
        if(checkX >= Math.min(outerX, maxX) && checkX <= Math.max(outerX, maxX)) {
            if(checkZ >= Math.min(outerZ, maxZ) && checkZ <= Math.max(outerZ, maxZ)) { return true; }
        }
        return false;
    }
    
    //Player Utils
    public static boolean hasPermission(Entity e, String perm) {
        if(!isPlayer(e)) return true;
        return hasPermission(getPlayer(e), perm);
    }
    
    public static boolean hasPermission(OfflinePlayer sender, String permission) {
        if(permission.equals("Villages.none")) return true;
        if(sender.isOp()) return true;
        if(sender.isOnline()) return hasPermission(sender.getPlayer(), permission);
        
        //PermissionsEx Permission Checking
//        if(PluginHook.PEX_HOOK.isHooked()) {
//            return PluginHook.PEX_HOOK.hasPermission(sender.getName(), permission);
//        }
        
        World world = Bukkit.getWorlds().get(0);
        if(sender.isOnline()) {
            world = sender.getPlayer().getWorld();
        }
        
        //Vault Permission Checking
        if(PluginHook.VAULT_HOOK.isHooked() && PluginHook.VAULT_HOOK.getPermission() != null) {
            return PluginHook.VAULT_HOOK.getPermission().playerHas(world, sender.getName(), permission);
        }
        
        if(!sender.isOnline()) return false;
        
        return hasPermission(sender.getPlayer(), permission);
    }
    
    public static boolean hasPermission(Player sender, String permission) {return hasPermission((CommandSender) sender, permission);}
    
    public static boolean hasPermission(CommandSender sender, String permission) {
        if(permission.equals(getPlugin().getName() + ".none")) return true;
        if(!isPlayer(sender)) return true;
        if(getPlayer(sender).isOp()) return true;
        if(getPlayer(sender).hasPermission(permission)) return true;
        
        //PermissionsEx Permission Checking
//        if(PluginHook.PEX_HOOK.isHooked()) {
//            return PluginHook.PEX_HOOK.hasPermission(getPlayer(sender), permission);
//        }
        
        //Vault Permission Checking
        if(PluginHook.VAULT_HOOK.isHooked() && PluginHook.VAULT_HOOK.getPermission() != null) {
            return PluginHook.VAULT_HOOK.getPermission().has(sender, permission);
        }
        
        return getPlayer(sender).hasPermission(permission);
    }
    
    public static boolean canSee(CommandSender p, OfflinePlayer target) {
        if(!isPlayer(p)) return true;
        if(!target.isOnline()) return true;
        return getPlayer(p).canSee(target.getPlayer());
    }
    
    public static boolean isVisible(OfflinePlayer t) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(!canSee(p, t)) return false;
        }
        return true;
    }
    
    public static List<OfflinePlayer> getPlayersList() {
        List<OfflinePlayer> rv = new ArrayList<OfflinePlayer>();
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(!isVisible(p)) continue;
            rv.add(Bukkit.getOfflinePlayer(p.getName()));
        }
        return rv;
    }
    
    public static List<Player> getOnlinePlayers(CommandSender rel) {
        List<Player> players = new ArrayList<Player>();
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(!canSee(rel, p)) continue;
            players.add(p);
        }
        return players;
    }
    
    public static boolean isMuted(OfflinePlayer player) {
//        try {if(PluginHook.SEL_BANS_HOOK.isHooked() && !BansUtils.CanPlayerTalk(player)) return true;}catch(Error e) {} catch(Exception e) {}
//        try {if(PluginHook.DOMS_COMMANDS_HOOK.isHooked() && DomsPlayer.getPlayer(player).isMuted()) return true;}catch(Error e) {}catch(Exception e){}
//        try {if(PluginHook.HERO_CHAT_HOOK.isHooked() && Herochat.getChatterManager().getChatter(player.getName()).isMuted()) return true;}catch(Error e) {} catch(Exception e) {}
        if (player != null) {
            if (player.isOnline()) {
                AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(true, (Player) player, "Am I muted?", null);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return true;
                }
            }
        }

        return false;
    }
    
    //Language Utils
    public static List<String> gk(String key, Object... o) {
        return DataManager.LANGUAGE_MANAGER.getKey(key, o);
    }
    
    public static void sk(CommandSender sender, String key, Object... o) {
        sendMessage(sender, gk(key, o));
    }
    
    public static void sk(Resident sender, String key, Object... o) {
        sendMessage(sender, gk(key, o));
    }
    
    public static void bk(String key, Object... o) {
        broadcast(gk(key, o));
    }
    
    //Economy Utils
    public static boolean hasBalance(String player, double amt) {
        if(!useEconomy() || PluginHook.VAULT_HOOK.getEconomy() == null) return true;
        if(getBalance(player) >= amt) return true;
        return false;
    }
    
    public static boolean hasBalance(Village village, double amt) {
        if(!useEconomy() || PluginHook.VAULT_HOOK.getEconomy() == null) return true;
        if(getBalance(village) >= amt) return true;
        return false;
    }
    
    public static double getBalance(String player) {
        if(!useEconomy() || PluginHook.VAULT_HOOK.getEconomy() == null) return -1.0d;
        return PluginHook.VAULT_HOOK.getEconomy().getBalance(player);
    }
    
    public static double getBalance(Village village) {
        if(!useEconomy() || PluginHook.VAULT_HOOK.getEconomy() == null) return -1.0d;
        return village.getBank().getWealth();
    }
    
    public static double getCost(String key) {
        return getConfig().getDouble("costs." + key, 0d);
    }
    
    public static String getMoney(double money) {
        return PluginHook.VAULT_HOOK.formatEconomy(money);
    }
    
    public static boolean useEconomy() {
        return PluginHook.VAULT_HOOK.isHooked() && PluginHook.VAULT_HOOK.getEconomy() != null;
    }
    
    public static void chargePlayer(OfflinePlayer player, double amt) {
        if(player == null) return;
        chargePlayer(player.getName(), amt);
    }
    
    public static void chargePlayer(Resident player, double amt) {
        if(player == null) return;
        chargePlayer(player.getName(), amt);
    }
    
    public static void chargePlayer(CommandSender player, double amt) {
        if(player == null) return;
        if(!isPlayer(player)) return;
        chargePlayer(player.getName(), amt);
    }
    
    public static void chargePlayer(String player, double amt) {
        if(player == null) return;
        try {
            if(amt == 0) return;
            if(amt < 0) {
                VaultHook.VAULT_HOOK.getEconomy().depositPlayer(player, -amt);
            } else if(amt > 0) {
                VaultHook.VAULT_HOOK.getEconomy().withdrawPlayer(player, amt);
            }
        } catch(Exception e) {} catch(Error e) {}
    }

    
    //Time Utils
    public static long getNow() {
        return System.currentTimeMillis();
    }
    
    public static String getTimeDifference(Date late) {return Base.getTimeDifference(new Date(), late);}
    
    public static String getTimeDifference(Date early, Date late) {
        Long NowInMilli = late.getTime();
        Long TargetInMilli = early.getTime();
        Long diffInSeconds = (NowInMilli - TargetInMilli) / 1000;

        long diff[] = new long[] {0,0,0,0,0};
        /* sec */diff[4] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
        /* min */diff[3] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        /* hours */diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        /* days */diff[1] = (diffInSeconds = (diffInSeconds / 24)) >= 31 ? diffInSeconds % 31: diffInSeconds;
        /* months */diff[0] = (diffInSeconds = (diffInSeconds / 31));
        
        String message = "";
        
        if(diff[0] > 0) {
            message += diff[0] + " month";
            if(diff[0] > 1) {
                message += "s";
            }
            return message;
        }
        if(diff[1] > 0) {
            message += diff[1] + " day";
            if(diff[1] > 1) {
                message += "s";
            }
            return message;
        }
        if(diff[2] > 0) {
            message += diff[2] + " hour";
            if(diff[2] > 1) {
                message += "s";
            }
            return message;
        }
        if(diff[3] > 0) {
            message += diff[3] + " minute";
            if(diff[3] > 1) {
                message += "s";
            }
            return message;
        }
        if(diff[4] > 0) {
            message += diff[4] + " second";
            if(diff[4] > 1) {
                message += "s";
            }
            return message;
        }
        
        return "Time Error";
    }
}
