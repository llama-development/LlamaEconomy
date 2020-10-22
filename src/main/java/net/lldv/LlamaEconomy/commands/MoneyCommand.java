package net.lldv.LlamaEconomy.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.lldv.LlamaEconomy.LlamaEconomy;
import net.lldv.LlamaEconomy.utils.Language;

import java.util.concurrent.CompletableFuture;

public class MoneyCommand extends PluginCommand<LlamaEconomy> {

    public MoneyCommand(LlamaEconomy owner) {
        super("money", owner);
        setDescription("Money command");
        setUsage("/money <optional: player>");
        setAliases(new String[]{"mymoney", "getmoney", "bal", "balance"});
        addCommandParameters("default", new CommandParameter[]{new CommandParameter("player", CommandParamType.STRING, true)});
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        // Making the command async so it's more efficient when using real database providers
        CompletableFuture.runAsync(() -> {
            if (args.length >= 1) {
                String target = args[0];
                Player playerTarget = LlamaEconomy.instance.getServer().getPlayer(target);
                if (playerTarget != null) target = playerTarget.getName();

                if (!LlamaEconomy.getAPI().hasAccount(target)) {
                    sender.sendMessage(Language.getAndReplace("not-registered", target));
                    return;
                }

                double money = LlamaEconomy.getAPI().getMoney(target);
                sender.sendMessage(Language.getAndReplace("money-other", target, LlamaEconomy.monetaryUnit, LlamaEconomy.moneyFormat.format(money)));
            } else {
                if (sender.isPlayer()) {
                    double money = LlamaEconomy.getAPI().getMoney(sender.getName());
                    sender.sendMessage(Language.getAndReplace("money", LlamaEconomy.monetaryUnit, LlamaEconomy.moneyFormat.format(money)));
                }
            }
        });
        return false;
    }
}
