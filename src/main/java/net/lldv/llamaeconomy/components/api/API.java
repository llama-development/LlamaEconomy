package net.lldv.llamaeconomy.components.api;

import cn.nukkit.Player;
import cn.nukkit.Server;
import lombok.RequiredArgsConstructor;
import net.lldv.llamaeconomy.LlamaEconomy;
import net.lldv.llamaeconomy.components.event.AddMoneyEvent;
import net.lldv.llamaeconomy.components.event.ReduceMoneyEvent;
import net.lldv.llamaeconomy.components.event.SetMoneyEvent;
import net.lldv.llamaeconomy.components.provider.Provider;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class API {

    private final LlamaEconomy plugin;
    private final Provider provider;

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

    public void hasAccount(Player player, Consumer<Boolean> callback) {
        this.hasAccount(player.getName(), callback);
    }

    public void hasAccount(String player, Consumer<Boolean> callback) {
        CompletableFuture.runAsync(() -> {
            callback.accept(this.provider.hasAccount(player));
        });
    }

    public void createAccount(UUID uuid, double money) {
        String name = this.plugin.getServer().getOfflinePlayer(uuid).getName();
        this.provider.createAccount(name, money);
    }

    public void createAccount(Player player, double money) {
        this.createAccount(player.getName(), money);
    }

    public void createAccount(String username, double money) {
        this.provider.createAccount(username, money);
    }

    public void createAccount(UUID uuid, double money, Runnable runnable) {
        String name = this.plugin.getServer().getOfflinePlayer(uuid).getName();
        this.createAccount(name, money, runnable);
    }

    public void createAccount(Player player, double money, Runnable runnable) {
        this.createAccount(player.getName(), money, runnable);
    }

    public void createAccount(String username, double money, Runnable runnable) {
        CompletableFuture.runAsync(() -> {
            this.provider.createAccount(username, money);
            runnable.run();
        });
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

    public void getMoney(UUID uuid, Consumer<Double> callback) {
        String name = this.plugin.getServer().getOfflinePlayer(uuid).getName();
        this.getMoney(name, callback);
    }

    public void getMoney(Player player, Consumer<Double> callback) {
        this.getMoney(player.getName(), callback);
    }

    public void getMoney(String username, Consumer<Double> callback) {
        CompletableFuture.runAsync(() -> callback.accept(this.provider.getMoney(username)));
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
        Server.getInstance().getPluginManager().callEvent(new SetMoneyEvent(username, money));
    }

    public void setMoney(UUID uuid, double money, Runnable runnable) {
        String name = this.plugin.getServer().getOfflinePlayer(uuid).getName();
        this.setMoney(name, money, runnable);
    }

    public void setMoney(Player player, double money, Runnable runnable) {
        this.setMoney(player.getName(), money, runnable);
    }

    public void setMoney(String username, double money, Runnable runnable) {
        CompletableFuture.runAsync(() -> {
            this.provider.setMoney(username, money);
            runnable.run();
        });
        Server.getInstance().getPluginManager().callEvent(new SetMoneyEvent(username, money));
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
        Server.getInstance().getPluginManager().callEvent(new AddMoneyEvent(username, money));
    }

    public void addMoney(UUID uuid, double money, Runnable runnable) {
        String name = this.plugin.getServer().getOfflinePlayer(uuid).getName();
        this.addMoney(name, money, runnable);
    }

    public void addMoney(Player player, double money, Runnable runnable) {
        this.addMoney(player.getName(), money, runnable);
    }

    public void addMoney(String username, double money, Runnable runnable) {
        CompletableFuture.runAsync(() -> {
            this.provider.setMoney(username, this.provider.getMoney(username) + money);
            runnable.run();
        });
        Server.getInstance().getPluginManager().callEvent(new AddMoneyEvent(username, money));
    }

    public void reduceMoney(Player player, double money) {
        this.reduceMoney(player.getName(), money);
    }

    public void reduceMoney(UUID uuid, double money) {
        String name = this.plugin.getServer().getOfflinePlayer(uuid).getName();
        this.reduceMoney(name, money);
    }

    public void reduceMoney(String username, double money) {
        CompletableFuture.runAsync(() -> this.provider.setMoney(username, this.provider.getMoney(username) - money));
        Server.getInstance().getPluginManager().callEvent(new ReduceMoneyEvent(username, money));
    }

    public void reduceMoney(Player player, double money, Runnable runnable) {
        this.reduceMoney(player.getName(), money, runnable);
    }

    public void reduceMoney(UUID uuid, double money, Runnable runnable) {
        String name = this.plugin.getServer().getOfflinePlayer(uuid).getName();
        this.reduceMoney(name, money, runnable);
    }

    public void reduceMoney(String username, double money, Runnable runnable) {
        CompletableFuture.runAsync(() -> {
            this.provider.setMoney(username, this.provider.getMoney(username) - money);
            runnable.run();
        });
        Server.getInstance().getPluginManager().callEvent(new ReduceMoneyEvent(username, money));
    }

    public void getAll(Consumer<Map<String, Double>> callback) {
        CompletableFuture.runAsync(() -> callback.accept(this.provider.getAll()));
    }

    public Map<String, Double> getAll() {
        return this.provider.getAll();
    }

    public String getMonetaryUnit() {
        return this.plugin.getMonetaryUnit();
    }

    public DecimalFormat getMoneyFormat() {
        return this.plugin.getMoneyFormat();
    }

    public double getDefaultMoney() {
        return this.plugin.getDefaultMoney();
    }

}
