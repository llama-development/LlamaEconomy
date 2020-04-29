package net.lldv.LlamaEconomy.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.player.Player;
import net.lldv.LlamaEconomy.LlamaEconomy;
import net.lldv.LlamaEconomy.utils.Language;

import java.util.concurrent.CompletableFuture;

public class AddMoneyCommand extends Command {

    public AddMoneyCommand() {
        super("addmoney", "Add money to an player.", "/addmoney <player> <amount>", new String[]{"givemoney", "incresemoney", "baladd"});
        this.setPermission("llamaeconomy.addmoney");
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

                    LlamaEconomy.getAPI().addMoney(target, amt);
                    sender.sendMessage(Language.getAndReplace("added-money", target, LlamaEconomy.monetaryUnit, amt));

                } catch (NumberFormatException ex) {
                    sender.sendMessage(Language.get("invalid-amount"));
                }
            } else sender.sendMessage(Language.getAndReplace("usage", getUsage()));
        });
        return false;
    }
}
