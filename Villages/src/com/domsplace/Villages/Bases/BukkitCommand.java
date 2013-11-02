/*
 * Copyright 2013 Dominic.
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

package com.domsplace.Villages.Bases;

import com.domsplace.Villages.Objects.SubCommandOption;
import com.domsplace.Villages.Objects.VillageHelpTopic;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

/**
 * @author      Dominic
 * @since       11/10/2013
 */
public abstract class BukkitCommand extends Base implements CommandExecutor, TabCompleter, Helpable {
    private static final List<BukkitCommand> COMMANDS = new ArrayList<BukkitCommand>();
    private static final int HELP_PER_PAGE = 6;
    
    private static PluginCommand registerCommand(BukkitCommand command) {
        PluginCommand cmd = getPlugin().getCommand(command.getCommand());
        cmd.setExecutor(command);
        cmd.setPermissionMessage(colorise(Base.getPermissionMessage()));
        COMMANDS.add(command);
        Bukkit.getHelpMap().addTopic(new VillageHelpTopic(command));
        return cmd;
    }
    
    public static List<BukkitCommand> getCommands() {return new ArrayList<BukkitCommand>(COMMANDS);}

    public static BukkitCommand getCommand(String command) {
        for(BukkitCommand bc : COMMANDS) {
            if(!bc.getCommand().equalsIgnoreCase(command)) continue;
            return bc;
        }
        return null;
    }
    
    //Instance
    private String command;
    private PluginCommand cmd;
    private List<SubCommand> subCommands;
    private List<SubCommandOption> subOptions;
    private String permission = "none";
    
    public BukkitCommand(String command) {
        this.command = command;
        this.cmd = BukkitCommand.registerCommand(this);
        this.subCommands = new ArrayList<SubCommand>();
        this.subOptions = new ArrayList<SubCommandOption>();
        registerCommand(this);
    }
    
    public String getCommand() { return this.command; }
    public PluginCommand getCmd() {return this.cmd;}
    public List<SubCommand> getSubCommands() {return this.subCommands;}
    public List<SubCommandOption> getSubCommandOptions() {return new ArrayList<SubCommandOption>(this.subOptions);}
    public String getPermission() {return "Villages." + this.permission;}
    
    public void setPermission(String perm) {this.permission = perm;}
    
    public void addSubCommand(SubCommand cmd) {this.subCommands.add(cmd);}
    public void removeSubCommand(SubCommand aThis) {this.subCommands.remove(aThis);}
    
    public void addSubCommandOption(SubCommandOption o) {this.subOptions.add(o);}
    public void removeSubCommandOption(SubCommandOption o) {this.subOptions.remove(o);}

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase(this.command)) {
            debug("Got Command");
            if(!hasPermission(sender, this.getPermission())) return noPermission(sender, cmd, label, args);
            if(!inVillageWorld(sender)) {
                sk(sender, "notinthisworld");
                return true;
            }
            try {
                SubCommand sc = getSubCommand(args, sender);
                boolean result;
                if(sc != null) {
                    result = sc.transExecute(this, sender, cmd, label, args);
                } else {
                    result = this.cmd(sender, cmd, label, args);
                }
                if(!result) return commandFailed(sender, cmd, label, args); 
                return commandSuccess(sender, cmd, label, args);
            } catch(Exception e) {
                Base.error("Command error! Show Villages Dev Team!", e);
                sendMessage(sender, ChatError + "A command error occured, please contact a server Admin!");
                return this.commandFailed(sender, cmd, label, args);
            }
        }
        
        return badCommand(sender, cmd, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase(this.getCommand())) {
            List<String> tab = this.tab(sender, cmd, label, args);
            if(tab != null) {
                return tabSuccess(sender, cmd, label, args, tab);
            }
            
            return this.tabFailed(sender, cmd, label, args);
        }
        return badTab(sender, cmd, label, args);
    }
    
    
    public boolean badCommand(CommandSender sender, Command cmd, String label, String[] args) {return false;}
    public boolean commandSuccess(CommandSender sender, Command cmd, String label, String[] args) {return true;}
    public boolean commandFailed(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> msgs = new ArrayList<String>();
        
        int page = 1;
        for(String s : args) {
            if(!isInt(s)) continue;
            page = getInt(s);
        }
        
        int pages = (int)Math.ceil((double)this.subCommands.size() / (double)BukkitCommand.HELP_PER_PAGE);
        msgs.add(ChatImportant + "Village Help - Page " + page + " of " + pages);
        for(int i = (page - 1)*HELP_PER_PAGE; i < page*HELP_PER_PAGE; i++) {
            try {
                SubCommand x = this.subCommands.get(i);
                msgs.add(ChatImportant + "\t" + x.asCommand() + ChatDefault + " " + trim(x.getCommandUsage(), 20) + (x.getCommandUsage().length() >= 20 ? "..." : ""));
            } catch(Exception e) {}
        }
        
        if(page < 1 || page > pages) {
            sendMessage(sender, ChatError + "Please enter a page between 1 and " + pages + ".");
        }
        sendMessage(sender, msgs);
        return true;
    }
    public abstract boolean cmd(CommandSender sender, Command cmd, String label, String[] args);
    
    public List<String> tab(CommandSender sender, Command cmd, String label, String[] args) {return this.getArgumentGuesses(args, sender);}
    public List<String> tabFailed(CommandSender sender, Command cmd, String label, String[] args) {return null;}
    public List<String> badTab(CommandSender sender, Command cmd, String label, String[] args) {return null;}
    public List<String> tabSuccess(CommandSender sender, Command cmd, String label, String[] args, List<String> successValue) {return successValue;}
    
    public boolean noPermission(CommandSender sender, Command cmd, String label, String[] args) {
        cmd.setPermissionMessage(colorise(Base.getPermissionMessage()));
        sender.sendMessage(cmd.getPermissionMessage());
        return true;
    }
    
    public SubCommand getSubCommand(String[] args, CommandSender sender) {
        if(args.length < 1) return null;
        SubCommand bestMatch = null;
        
        for(SubCommand sc : this.subCommands) {
            int m = sc.getMatches(args);
            if(m == 0) continue;
            if(bestMatch != null && m <= bestMatch.getMatches(args)) continue;
            bestMatch = sc;
        }
        
        return bestMatch;
    }
    
    public boolean fakeExecute(CommandSender sender, String commandLine) {
        if(commandLine.startsWith("/")) commandLine = commandLine.replaceFirst("/", "");
        
        String[] s = commandLine.split(" ");
        if(s.length < 1) return false;
        
        String lbl = s[0];
        String[] args = new String[0];
        if(s.length > 1) {
            args = new String[s.length - 1];
            
            for(int i = 1; i < s.length; i++) {
                args[i-1] = s[i];
            }
        }
        
        return this.onCommand(sender, cmd, lbl, args);
    }
    
    public List<String> getArgumentGuesses(String[] args, CommandSender sender) {
        List<String> options = new ArrayList<String>();
        if(args.length == 0) {
            for(SubCommandOption sco : this.subOptions) {
                options.addAll(sco.getOptionsFormatted(sender));
            }
        } else if(args.length == 1) {
            for(SubCommandOption sco : this.subOptions) {
                for(String s : sco.getOptionsFormatted(sender)) {
                    if(!s.toLowerCase().startsWith(args[0].toLowerCase())) continue;
                    options.add(s);
                }
            }
        } else if(args.length > 1) {            
            List<String> matches = new ArrayList<String>();
            
            for(SubCommandOption sco : this.subOptions) {
                String s = args[0].toLowerCase();
                s = SubCommandOption.reverse(s, sender);
                if(!sco.getOption().toLowerCase().startsWith(s.toLowerCase())) continue;
                matches.addAll(sco.tryFetch(args, 1, sender));
            }
            
            if(args[args.length - 1].replaceAll(" ", "").equalsIgnoreCase("")) return matches;
            
            List<String> closeMatch = new ArrayList<String>();
            
            for(String match : matches) {
                if(match.toLowerCase().startsWith(args[args.length-1].toLowerCase())) closeMatch.add(match);
            }
            
            options.addAll(closeMatch);
        }
        return options;
    }
    
    @Override public String getHelpTopic() {return "/" + this.command;}
    @Override public String getHelpPermission() {return this.getPermission();}
    @Override public void setHelpPermission(String permission) {this.setPermission(permission);}
    @Override public String getHelpTextLarge(CommandSender forWho) {return this.getHelpTextShort();}
    @Override public String getHelpTextShort() {return this.cmd.getDescription();}
}
