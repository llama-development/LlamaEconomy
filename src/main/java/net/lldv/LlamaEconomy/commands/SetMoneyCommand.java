package net.lldv.LlamaEconomy.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.lldv.LlamaEconomy.LlamaEconomy;
import net.lldv.LlamaEconomy.utils.Language;

import java.util.concurrent.CompletableFuture;

public class SetMoneyCommand extends PluginCommand<LlamaEconomy> {

    public SetMoneyCommand(LlamaEconomy owner) {
        super("setmoney", owner);
        setDescription("Set money of an player");
        setUsage("/setmoney <player> <amount>");
        setPermission("llamaeconomy.setmoney");
        setAliases(new String[]{"balset"});
        addCommandParameters("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.STRING, false),
                new CommandParameter("amount", CommandParamType.FLOAT, false)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!sender.hasPermission(getPermission())) return false;
        CompletableFuture.runAsync(() -> {
            if (args.length >= 2) {
                try {
                    String target = args[0];
                    Player playerTarget = LlamaEconomy.instance.getServer().getPlayer(target);
                    if (playerTarget != null) target = playerTarget.getName();

                    if (!LlamaEconomy.getAPI().hasAccount(target)) {
                        sender.sendMessage(Language.getAndReplace("not-registered", target));
                        return;
                    }

                    double amt = Double.parseDouble(args[1]);

                    if (amt < 0) {
                        sender.sendMessage(Language.get("invalid-amount"));
                        return;
                    }

                    LlamaEconomy.getAPI().setMoney(target, amt);
                    sender.sendMessage(Language.getAndReplace("set-money", target, LlamaEconomy.monetaryUnit, amt));

                } catch (NumberFormatException ex) {
                    sender.sendMessage(Language.get("invalid-amount"));
                }
            } else sender.sendMessage(Language.getAndReplace("usage", getUsage()));
        });
        return false;
    }
}
