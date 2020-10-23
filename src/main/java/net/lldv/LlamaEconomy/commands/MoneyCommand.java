package net.lldv.LlamaEconomy.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.ConfigSection;
import net.lldv.LlamaEconomy.LlamaEconomy;
import net.lldv.LlamaEconomy.components.language.Language;

import java.util.concurrent.CompletableFuture;

public class MoneyCommand extends PluginCommand<LlamaEconomy> {

    public MoneyCommand(LlamaEconomy owner, ConfigSection section) {
        super(section.getString("name"), owner);
        setDescription(section.getString("description"));
        setUsage(section.getString("usage"));
        setAliases(section.getStringList("aliases").toArray(new String[]{}));
        addCommandParameters("default", new CommandParameter[]{new CommandParameter("player", CommandParamType.STRING, true)});
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        // Making the command async so it's more efficient when using real database providers
        CompletableFuture.runAsync(() -> {
            if (args.length >= 1) {
                String target = args[0];
                Player playerTarget = getPlugin().getServer().getPlayer(target);
                if (playerTarget != null) target = playerTarget.getName();

                if (!LlamaEconomy.getAPI().hasAccount(target)) {
                    sender.sendMessage(Language.getAndReplace("not-registered", target));
                    return;
                }

                double money = LlamaEconomy.getAPI().getMoney(target);
                sender.sendMessage(Language.getAndReplace("money-other", target, getPlugin().getMonetaryUnit(), getPlugin().getMoneyFormat().format(money)));
            } else {
                if (sender.isPlayer()) {
                    double money = LlamaEconomy.getAPI().getMoney(sender.getName());
                    sender.sendMessage(Language.getAndReplace("money", getPlugin().getMonetaryUnit(), getPlugin().getMoneyFormat().format(money)));
                }
            }
        });
        return false;
    }
}
