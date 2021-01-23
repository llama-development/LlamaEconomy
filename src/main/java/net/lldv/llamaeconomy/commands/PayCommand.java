package net.lldv.llamaeconomy.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.ConfigSection;
import net.lldv.llamaeconomy.LlamaEconomy;
import net.lldv.llamaeconomy.components.event.PlayerPayMoneyEvent;
import net.lldv.llamaeconomy.components.language.Language;

import java.util.concurrent.CompletableFuture;

public class PayCommand extends PluginCommand<LlamaEconomy> {

    public PayCommand(LlamaEconomy owner, ConfigSection section) {
        super(section.getString("Name"), owner);
        setDescription(section.getString("Description"));
        setUsage(section.getString("Usage"));
        setAliases(section.getStringList("Aliases").toArray(new String[]{}));
        final String[] params = section.getString("Parameters").split(";");
        addCommandParameters("default", new CommandParameter[]{
                new CommandParameter(params[0], CommandParamType.STRING, false),
                new CommandParameter(params[1], CommandParamType.FLOAT, false)
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

                        if (target.equals(sender.getName())) return;

                        if (!LlamaEconomy.getAPI().hasAccount(target)) {
                            payer.sendMessage(Language.get("not-registered", target));
                            return;
                        }

                        LlamaEconomy.getAPI().reduceMoney(payer.getName(), toPay);
                        LlamaEconomy.getAPI().addMoney(target, toPay);

                        payer.sendMessage(Language.get("you-paid", target, getPlugin().getMonetaryUnit(), this.getPlugin().getMoneyFormat().format(toPay)));

                        if (playerTarget != null) {
                            playerTarget.sendMessage(Language.get("paid-you", payer.getName(), this.getPlugin().getMonetaryUnit(), this.getPlugin().getMoneyFormat().format(toPay)));
                        }

                        if (sender.isPlayer()) Server.getInstance().getPluginManager().callEvent(new PlayerPayMoneyEvent((Player) sender, target, toPay));

                    } catch (NumberFormatException ex) {
                        sender.sendMessage(Language.get("invalid-amount"));
                    }

                } else sender.sendMessage(Language.get("usage", getUsage()));
            }
        });

        return false;
    }
}
