package net.lldv.LlamaEconomy.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.player.Player;
import net.lldv.LlamaEconomy.LlamaEconomy;
import net.lldv.LlamaEconomy.utils.Language;

import java.util.concurrent.CompletableFuture;

public class SetMoneyCommand extends Command {

    public SetMoneyCommand() {
        super("setmoney", "Set money of a player", "/setmoney <player> <amount>", new String[]{"balset"});
        this.setPermission("llamaeconomy.setmoney");
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
