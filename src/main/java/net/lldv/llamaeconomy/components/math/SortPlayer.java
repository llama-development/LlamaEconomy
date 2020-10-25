package net.lldv.llamaeconomy.components.math;

public class SortPlayer {

    public final String name;
    public final double money;

    public SortPlayer(String name, double money) {
        this.name = name;
        this.money = money;
    }

    public double getMoney() {
        return this.money;
    }
}
