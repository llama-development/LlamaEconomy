package net.lldv.llamaeconomy.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import net.lldv.llamaeconomy.LlamaEconomy;

import java.util.concurrent.CompletableFuture;

public class PlayerListener implements Listener {

    private final LlamaEconomy plugin;

    public PlayerListener(LlamaEconomy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        LlamaEconomy.getAPI().hasAccount(event.getPlayer(), has -> {
            if (!has) LlamaEconomy.getAPI().createAccount(event.getPlayer(), this.plugin.getDefaultMoney());
        });
    }

}
