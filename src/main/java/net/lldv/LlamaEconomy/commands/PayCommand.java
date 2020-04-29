package net.lldv.LlamaEconomy.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.player.Player;
import net.lldv.LlamaEconomy.LlamaEconomy;
import net.lldv.LlamaEconomy.utils.Language;

import java.util.concurrent.CompletableFuture;

public class PayCommand extends Command {

    public PayCommand() {
        super("pay", "Pay money to an player.", "/pay <player> <amount>", new String[]{"balpay"});
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        CompletableFuture.runAsync(() -> {
            if (sender.isPlayer()) {
                if (args.length >= 2) {
                    Player payer = (Player) sender;

                    double senderMoney = LlamaEconomy.getAPI().getMoney(payer);

                    try {
                        double toPay = Double.parseDouble(args[1]);

                        if (toPay > senderMoney) {
                            payer.sendMessage(Language.get("pay-not-enough-money"));
                            return;
                        }

                        if (toPay < 0) {
                            sender.sendMessage(Language.get("invalid-amount"));
                            return;
                        }

                        String target = args[0];
                        Player playerTarget = LlamaEconomy.instance.getServer().getPlayer(target);
                        if (playerTarget != null) target = playerTarget.getName();

                        if (!LlamaEconomy.getAPI().hasAccount(target)) {
                            payer.sendMessage(Language.getAndReplace("not-registered", target));
                            return;
                        }

                        LlamaEconomy.getAPI().reduceMoney(payer.getName(), toPay);
                        LlamaEconomy.getAPI().addMoney(target, toPay);

                        payer.sendMessage(Language.getAndReplace("you-paid", target, LlamaEconomy.monetaryUnit, toPay));

                        if (playerTarget != null) {
                            playerTarget.sendMessage(Language.getAndReplace("paid-you", payer.getName(), LlamaEconomy.monetaryUnit, toPay));
                        }

                    } catch (NumberFormatException ex) {
                        sender.sendMessage(Language.get("invalid-amount"));
                    }

                } else sender.sendMessage(Language.getAndReplace("usage", getUsage()));
            }
        });

        return false;
    }
}
