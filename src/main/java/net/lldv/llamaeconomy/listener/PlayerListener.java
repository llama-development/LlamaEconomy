package net.lldv.llamaeconomy.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import net.lldv.llamaeconomy.LlamaEconomy;

public class PlayerListener implements Listener {

    private final LlamaEconomy plugin;

    public PlayerListener(LlamaEconomy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!LlamaEconomy.getAPI().hasAccount(event.getPlayer())) LlamaEconomy.getAPI().createAccount(event.getPlayer(), this.plugin.getDefaultMoney());
    }

}
