package net.lldv.llamaeconomy.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.ConfigSection;
import net.lldv.llamaeconomy.LlamaEconomy;
import net.lldv.llamaeconomy.components.language.Language;

import java.util.concurrent.CompletableFuture;

public class MoneyCommand extends PluginCommand<LlamaEconomy> {

    public MoneyCommand(LlamaEconomy owner, ConfigSection section) {
        super(section.getString("Name"), owner);
        setDescription(section.getString("Description"));
        setUsage(section.getString("Usage"));
        setAliases(section.getStringList("Aliases").toArray(new String[]{}));
        final String param = section.getString("Parameters");
        addCommandParameters("default", new CommandParameter[]{new CommandParameter(param, CommandParamType.STRING, true)});
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        CompletableFuture.runAsync(() -> {
            if (args.length >= 1) {
                String target = args[0];
                Player playerTarget = getPlugin().getServer().getPlayer(target);
                if (playerTarget != null) target = playerTarget.getName();

                if (!LlamaEconomy.getAPI().hasAccount(target)) {
                    sender.sendMessage(Language.get("not-registered", target));
                    return;
                }

                double money = LlamaEconomy.getAPI().getMoney(target);
                sender.sendMessage(Language.get("money-other", target, getPlugin().getMonetaryUnit(), getPlugin().getMoneyFormat().format(money)));
            } else {
                if (sender.isPlayer()) {
                    double money = LlamaEconomy.getAPI().getMoney(sender.getName());
                    sender.sendMessage(Language.get("money", getPlugin().getMonetaryUnit(), getPlugin().getMoneyFormat().format(money)));
                }
            }
        });
        return false;
    }
}
