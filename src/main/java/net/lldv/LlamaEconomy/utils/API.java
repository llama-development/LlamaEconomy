package net.lldv.LlamaEconomy.utils;

import cn.nukkit.player.Player;
import net.lldv.LlamaEconomy.LlamaEconomy;
import net.lldv.LlamaEconomy.provider.BaseProvider;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class API {

    private final BaseProvider provider;

    public API(BaseProvider provider) {
        this.provider = provider;
    }

    public void saveAll(boolean async) {
        provider.saveAll(async);
    }

    public boolean hasAccount(UUID uuid) {
        String name = LlamaEconomy.getInstance().getServer().getOfflinePlayer(uuid).getName();
        return this.hasAccount(name);
    }

    public boolean hasAccount(Player player) {
        return this.hasAccount(player.getName());
    }

    public boolean hasAccount(String username) {
        return provider.hasAccount(username);
    }

    public void createAccount(Player player, double money) {
        this.createAccount(player.getName(), money);
    }

    public void createAccount(String username, double money) {
        provider.createAccount(username, money);
    }

    public double getMoney(UUID uuid) {
        String name = LlamaEconomy.getInstance().getServer().getOfflinePlayer(uuid).getName();
        return this.getMoney(name);
    }

    public double getMoney(Player player) {
        return this.getMoney(player.getName());
    }

    public double getMoney(String username) {
        return provider.getMoney(username);
    }

    public void setMoney(UUID uuid, double money) {
        String name = LlamaEconomy.getInstance().getServer().getOfflinePlayer(uuid).getName();
        this.setMoney(name, money);
    }

    public void setMoney(Player player, double money) {
        this.setMoney(player.getName(), money);
    }

    public void setMoney(String username, double money) {
        CompletableFuture.runAsync(() -> provider.setMoney(username, money));
    }

    public void addMoney(UUID uuid, double money) {
        String name = LlamaEconomy.getInstance().getServer().getOfflinePlayer(uuid).getName();
        this.addMoney(name, money);
    }

    public void addMoney(Player player, double money) {
        this.addMoney(player.getName(), money);
    }

    public void addMoney(String username, double money) {
        CompletableFuture.runAsync(() -> provider.setMoney(username, provider.getMoney(username) + money));
    }

    public void reduceMoney(UUID uuid, double money) {
        String name = LlamaEconomy.getInstance().getServer().getOfflinePlayer(uuid).getName();
        this.reduceMoney(name, money);
    }

    public void reduceMoney(Player player, double money) {
        this.reduceMoney(player.getName(), money);
    }

    public void reduceMoney(String username, double money) {
        CompletableFuture.runAsync(() -> provider.setMoney(username, provider.getMoney(username) - money));
    }

    public HashMap<String, Double> getAll() {
        return provider.getAll();
    }

    public String getMonetaryUnit() {
        return LlamaEconomy.monetaryUnit;
    }

}
