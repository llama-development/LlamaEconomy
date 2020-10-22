package net.lldv.LlamaEconomy.components.math;

public class SortPlayer {

    public String name;
    public double money;

    public SortPlayer(String name, double money) {
        this.name = name;
        this.money = money;
    }

    public double getMoney() {
        return money;
    }
}
