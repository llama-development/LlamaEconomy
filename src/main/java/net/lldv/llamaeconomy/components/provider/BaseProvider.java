package net.lldv.llamaeconomy.components.provider;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.lldv.llamaeconomy.LlamaEconomy;

import java.util.Map;

@RequiredArgsConstructor
public class BaseProvider {

    @Getter
    private final LlamaEconomy plugin;

    public void init() { }

    public void close() { }

    public void saveAll(boolean async) { };

    public boolean hasAccount(String id) {
        return false;
    }

    public void createAccount(String id, double money) { }

    public double getMoney(String id) {
        return 0;
    }

    public void setMoney(String id, double money) { }

    public Map<String, Double> getAll() {
        return null;
    }

    public String getName() {
        return "error";
    }

}
