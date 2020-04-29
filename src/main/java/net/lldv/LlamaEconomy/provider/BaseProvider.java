package net.lldv.LlamaEconomy.provider;

import java.util.HashMap;

public interface BaseProvider {

    void init();

    void close();

    void saveAll(boolean async);

    boolean hasAccount(String id);

    void createAccount(String id, double money);

    double getMoney(String id);

    void setMoney(String id, double money);

    //void addMoney(String id, double money);

    //void reduceMoney(String id, double money);

    HashMap<String, Double> getAll();

    String getName();

}
