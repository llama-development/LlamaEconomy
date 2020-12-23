package net.lldv.llamaeconomy.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.utils.ConfigSection;
import net.lldv.llamaeconomy.LlamaEconomy;
import net.lldv.llamaeconomy.components.language.Language;

/**
 * @author LlamaDevelopment
 * @project LlamaEconomy
 * @website http://llamadevelopment.net/
 */
public class LecoReloadCommand extends PluginCommand<LlamaEconomy> {

    public LecoReloadCommand(LlamaEconomy owner, ConfigSection section) {
        super(section.getString("Name"), owner);
        setDescription(section.getString("Description"));
        setUsage(section.getString("Usage"));
        setAliases(section.getStringList("Aliases").toArray(new String[]{}));
        setPermission(section.getString("Permission"));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender.hasPermission(getPermission())) {
            sender.sendMessage(Language.get("reload"));
            this.getPlugin().reload();
            sender.sendMessage(Language.get("reloaded"));
        }
        return false;
    }
}
