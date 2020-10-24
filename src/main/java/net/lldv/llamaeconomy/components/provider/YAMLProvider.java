package net.lldv.llamaeconomy.components.provider;

import cn.nukkit.utils.Config;
import lombok.SneakyThrows;
import net.lldv.llamaeconomy.LlamaEconomy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class YAMLProvider extends BaseProvider {

    private Config moneyDB;
    private final HashMap<String, Double> playerMoney = new HashMap<>();

    public YAMLProvider(LlamaEconomy plugin) {
        super(plugin);
    }

    @SneakyThrows
    @Override
    public void init() {
        this.moneyDB = new Config(this.getPlugin().getDataFolder() + "/data/money.yml");
        for (Map.Entry<String, Object> mo : this.moneyDB.getSection("money").entrySet()) {
            if (mo.getValue() instanceof Double) {
                this.playerMoney.put(mo.getKey(), (double) mo.getValue());
            } else if (mo.getValue() instanceof Integer) {
                int m = (int) mo.getValue();
                this.playerMoney.put(mo.getKey(), (double) m);
            } else {
                this.getPlugin().getLogger().info("Something went wrong when loading player " + mo.getKey() + " please check if the value assigned to it is a number.");
            }
        }
        this.getPlugin().setProviderError(false);
    }

    @Override
    public void close() {
        this.saveAll(false);
    }

    @Override
    public void saveAll(boolean async) {
        if (async) { // Well i don't think this is really necessary for the yaml provider
            CompletableFuture.runAsync(() -> {
                this.moneyDB.set("money", this.playerMoney);
                this.moneyDB.save();
            });
        } else {
            this.moneyDB.set("money", this.playerMoney);
            this.moneyDB.save();
        }
    }

    @Override
    public boolean hasAccount(String id) {
        return this.playerMoney.containsKey(id);
    }

    @Override
    public void createAccount(String id, double money) {
        this.playerMoney.put(id, money);
    }

    @Override
    public double getMoney(String id) {
        return this.playerMoney.getOrDefault(id, 0.0);
    }

    @Override
    public void setMoney(String id, double money) {
        this.playerMoney.put(id, money);
    }


    @Override
    public Map<String, Double> getAll() {
        return this.playerMoney;
    }

    @Override
    public String getName() {
        return "yaml";
    }

}
