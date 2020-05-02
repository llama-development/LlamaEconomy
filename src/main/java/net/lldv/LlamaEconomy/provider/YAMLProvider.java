package net.lldv.LlamaEconomy.provider;

import cn.nukkit.utils.Config;
import net.lldv.LlamaEconomy.LlamaEconomy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class YAMLProvider implements BaseProvider {

    private Config moneyDB;
    private HashMap<String, Double> playerMoney = new HashMap<>();

    @Override
    public void init() {

        try {
            moneyDB = new Config(LlamaEconomy.instance.getDataFolder() + "/data/money.yml", Config.YAML);

            for (Map.Entry<String, Object> mo : moneyDB.getSection("money").entrySet()) {
                if (mo.getValue() instanceof Double) {
                    playerMoney.put(mo.getKey(), (double) mo.getValue());
                } else if (mo.getValue() instanceof Integer) {
                    int m = (int) mo.getValue();
                    playerMoney.put(mo.getKey(), (double) m);
                } else {
                    LlamaEconomy.instance.getLogger().info("Something went wrong when loading player " + mo.getKey() + " please check if the value assigned to it is a number.");
                }
            }
            LlamaEconomy.providerError = false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void close() {
        saveAll(false);
    }

    @Override
    public void saveAll(boolean async) {
        if (async) { // Well i don't think this is really necessary for the yaml provider xD
            CompletableFuture.runAsync(() -> {
                moneyDB.set("money", playerMoney);
                moneyDB.save();
            });
        } else {
            moneyDB.set("money", playerMoney);
            moneyDB.save();
        }
    }

    @Override
    public boolean hasAccount(String id) {
        return playerMoney.containsKey(id);
    }

    @Override
    public void createAccount(String id, double money) {
        playerMoney.put(id, money);
    }

    @Override
    public double getMoney(String id) {
        return playerMoney.getOrDefault(id, 0.0);
    }

    @Override
    public void setMoney(String id, double money) {
        playerMoney.put(id, money);
    }


    @Override
    public HashMap<String, Double> getAll() {
        return playerMoney;
    }

    @Override
    public String getName() {
        return "yaml";
    }

}
