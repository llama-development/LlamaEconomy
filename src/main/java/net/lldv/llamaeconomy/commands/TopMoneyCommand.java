package net.lldv.llamaeconomy.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.ConfigSection;
import net.lldv.llamaeconomy.LlamaEconomy;
import net.lldv.llamaeconomy.components.language.Language;
import net.lldv.llamaeconomy.components.math.SortPlayer;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TopMoneyCommand extends PluginCommand<LlamaEconomy> {

    public TopMoneyCommand(LlamaEconomy owner, ConfigSection section) {
        super(section.getString("name"), owner);
        this.setDescription(section.getString("description"));
        this.setUsage(section.getString("usage"));
        this.setAliases(section.getStringList("aliases").toArray(new String[]{}));
        final String param = section.getString("parameters");
        this.addCommandParameters("default", new CommandParameter[]{new CommandParameter(param, CommandParamType.INT, true)});
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        CompletableFuture.runAsync(() -> {
            Map<String, Double> all = LlamaEconomy.getAPI().getAll();
            List<SortPlayer> sortPlayers = new ArrayList<>();
            all.forEach((string, money) -> sortPlayers.add(new SortPlayer(string, money)));
            sortPlayers.sort(Comparator.comparing(SortPlayer::getMoney).reversed());

            int maxPages = sortPlayers.size() / 5;
            if (maxPages * 5 == sortPlayers.size()) maxPages--; // idk why
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

            sender.sendMessage(Language.getNP("topmoney-header"));

            int startFromIndex = page * 5;
            for (int i = 0; i < 5; i++) {
                int at = startFromIndex + i;
                if (sortPlayers.size() - 1 >= at) {
                    SortPlayer sortPlayer = sortPlayers.get(at);
                    sender.sendMessage(Language.getNP("topmoney-player", at + 1, sortPlayer.name, getPlugin().getMonetaryUnit(), getPlugin().getMoneyFormat().format(sortPlayer.money)));
                }
            }

            sender.sendMessage("\n" + Language.getNP("topmoney-siteinfo", page + 1, maxPages + 1));

            sender.sendMessage(Language.getNP("topmoney-footer"));

        });
        return false;
    }
}
