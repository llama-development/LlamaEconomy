package net.lldv.LlamaEconomy.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import net.lldv.LlamaEconomy.LlamaEconomy;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!LlamaEconomy.getAPI().hasAccount(event.getPlayer())) LlamaEconomy.getAPI().createAccount(event.getPlayer(), LlamaEconomy.defaultMoney);
    }

}
