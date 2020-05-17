package net.lldv.LlamaEconomy.utils;

import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandParameter;

public class Command {

    public static CommandData create(String name, String description, String usage) {
        return CommandData.builder(name).setDescription(description).setUsageMessage(usage).build();
    }

    public static CommandData create(String name, String description, String usage, String[] permission) {
        return CommandData.builder(name).setDescription(description).setPermissions(permission).setUsageMessage(usage).build();
    }

    public static CommandData create(String name, String description, String usage, String[] permission, String[] aliases) {
        return CommandData.builder(name).setDescription(description).setPermissions(permission).setAliases(aliases).setUsageMessage(usage).build();
    }

    public static CommandData create(String name, String description, String usage, String[] permission, String[] aliases, CommandParameter[]... commandParameters) {
        return CommandData.builder(name).setDescription(description).setPermissions(permission).setAliases(aliases).setParameters(commandParameters).setUsageMessage(usage).build();
    }

}
