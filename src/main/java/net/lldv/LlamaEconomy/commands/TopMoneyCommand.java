package net.lldv.LlamaEconomy.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import net.lldv.LlamaEconomy.LlamaEconomy;
import net.lldv.LlamaEconomy.utils.Language;
import net.lldv.LlamaEconomy.utils.SortPlayer;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TopMoneyCommand extends Command {

    public TopMoneyCommand() {
        super("topmoney", "", "/topmoney <page>", new String[]{});
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        CompletableFuture.runAsync(() -> {
            HashMap<String, Double> all = LlamaEconomy.getAPI().getAll();
            List<SortPlayer> sortPlayers = new ArrayList<>();
            all.forEach((string, money) -> sortPlayers.add(new SortPlayer(string, money)));
            sortPlayers.sort(Comparator.comparing(SortPlayer::getMoney).reversed());

            int maxPages = sortPlayers.size() / 5;
            int page = 0;

            try {
                if (args.length >= 1) {
                    int tPage = Integer.parseInt(args[0]) - 1;

                    page = tPage;
                    if (tPage > maxPages) page = maxPages;
                }
            } catch (Exception ex) {
                sender.sendMessage(Language.get("topmoney-invalid"));
                return;
            }

            sender.sendMessage(Language.getNoPrefix("topmoney-header"));

            int startFromIndex = page * 5;
            for (int i = 0; i < 5; i++) {
                int at = startFromIndex + i;
                if (sortPlayers.size() - 1 >= at) {
                    SortPlayer sortPlayer = sortPlayers.get(at);
                    sender.sendMessage(Language.getAndReplaceNoPrefix("topmoney-player", at + 1, sortPlayer.name, LlamaEconomy.monetaryUnit, sortPlayer.money));
                }
            }

            sender.sendMessage("\n" + Language.getAndReplaceNoPrefix("topmoney-siteinfo", page + 1, maxPages + 1));

            sender.sendMessage(Language.getNoPrefix("topmoney-footer"));

        });
        return false;
    }
}
