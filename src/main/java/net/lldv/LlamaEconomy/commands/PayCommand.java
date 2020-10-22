package net.lldv.LlamaEconomy.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.lldv.LlamaEconomy.LlamaEconomy;
import net.lldv.LlamaEconomy.components.language.Language;

import java.util.concurrent.CompletableFuture;

public class PayCommand extends PluginCommand<LlamaEconomy> {

    public PayCommand(LlamaEconomy owner) {
        super("pay", owner);
        setDescription("Pay money to an player");
        setUsage("/pay <player> <amount>");
        setAliases(new String[]{"balpay"});
        addCommandParameters("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.STRING, false),
                new CommandParameter("amount", CommandParamType.FLOAT, false)
        });
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
                        Player playerTarget = getPlugin().getServer().getPlayer(target);
                        if (playerTarget != null) target = playerTarget.getName();

                        if (!LlamaEconomy.getAPI().hasAccount(target)) {
                            payer.sendMessage(Language.getAndReplace("not-registered", target));
                            return;
                        }

                        LlamaEconomy.getAPI().reduceMoney(payer.getName(), toPay);
                        LlamaEconomy.getAPI().addMoney(target, toPay);

                        payer.sendMessage(Language.getAndReplace("you-paid", target, getPlugin().getMonetaryUnit(), toPay));

                        if (playerTarget != null) {
                            playerTarget.sendMessage(Language.getAndReplace("paid-you", payer.getName(), getPlugin().getMonetaryUnit(), toPay));
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
