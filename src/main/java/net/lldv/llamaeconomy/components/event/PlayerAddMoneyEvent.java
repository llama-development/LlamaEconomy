package net.lldv.llamaeconomy.components.event;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import lombok.Getter;

/**
 * @author LlamaDevelopment
 * @project LlamaEconomy
 * @website http://llamadevelopment.net/
 */
@Getter
public class PlayerAddMoneyEvent extends PlayerEvent {

    private final String target;
    private final double amount;
    private static final HandlerList handlers = new HandlerList();

    public PlayerAddMoneyEvent(Player player, String target, double amount) {
        this.player = player;
        this.target = target;
        this.amount = amount;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}
