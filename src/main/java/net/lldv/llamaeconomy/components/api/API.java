package net.lldv.llamaeconomy.components.api;

import cn.nukkit.Player;
import net.lldv.llamaeconomy.LlamaEconomy;
import net.lldv.llamaeconomy.components.provider.BaseProvider;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class API {

    private final LlamaEconomy plugin;
    private final BaseProvider provider;

    public API(LlamaEconomy plugin, BaseProvider provider) {
        this.plugin = plugin;
        this.provider = provider;
    }

    public void saveAll(boolean async) {
        this.provider.saveAll(async);
    }

    public boolean hasAccount(UUID uuid) {
        String name = this.plugin.getServer().getOfflinePlayer(uuid).getName();
        return this.hasAccount(name);
    }

    public boolean hasAccount(Player player) {
        return this.hasAccount(player.getName());
    }

    public boolean hasAccount(String username) {
        return this.provider.hasAccount(username);
    }

    public void createAccount(Player player, double money) {
        this.createAccount(player.getName(), money);
    }

    public void createAccount(String username, double money) {
        this.provider.createAccount(username, money);
    }

    public double getMoney(UUID uuid) {
        String name = this.plugin.getServer().getOfflinePlayer(uuid).getName();
        return this.getMoney(name);
    }

    public double getMoney(Player player) {
        return this.getMoney(player.getName());
    }

    public double getMoney(String username) {
        return this.provider.getMoney(username);
    }

    public void setMoney(UUID uuid, double money) {
        String name = this.plugin.getServer().getOfflinePlayer(uuid).getName();
        this.setMoney(name, money);
    }

    public void setMoney(Player player, double money) {
        this.setMoney(player.getName(), money);
    }

    public void setMoney(String username, double money) {
        CompletableFuture.runAsync(() -> this.provider.setMoney(username, money));
    }

    public void addMoney(UUID uuid, double money) {
        String name = this.plugin.getServer().getOfflinePlayer(uuid).getName();
        this.addMoney(name, money);
    }

    public void addMoney(Player player, double money) {
        this.addMoney(player.getName(), money);
    }

    public void addMoney(String username, double money) {
        CompletableFuture.runAsync(() -> this.provider.setMoney(username, this.provider.getMoney(username) + money));
    }

    public void reduceMoney(UUID uuid, double money) {
        String name = this.plugin.getServer().getOfflinePlayer(uuid).getName();
        this.reduceMoney(name, money);
    }

    public void reduceMoney(String username, double money) {
        CompletableFuture.runAsync(() -> this.provider.setMoney(username, this.provider.getMoney(username) - money));
    }

    public Map<String, Double> getAll() {
        return this.provider.getAll();
    }

    public String getMonetaryUnit() {
        return this.provider.getPlugin().getMonetaryUnit();
    }

    public DecimalFormat getMoneyFormat() {
        return this.provider.getPlugin().getMoneyFormat();
    }

}
