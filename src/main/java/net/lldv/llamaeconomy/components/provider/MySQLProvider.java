package net.lldv.llamaeconomy.components.provider;

import cn.nukkit.utils.Config;
import lombok.SneakyThrows;
import net.lldv.llamaeconomy.LlamaEconomy;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class MySQLProvider extends BaseProvider {

    private Connection connection;
    private String database;

    public MySQLProvider(LlamaEconomy plugin) {
        super(plugin);
    }

    @SneakyThrows
    @Override
    public void init() {
        Config c = this.getPlugin().getConfig();

        Class.forName("com.mysql.cj.jdbc.Driver");
        this.database = c.getString("mysql.database");
        String connectionUri = "jdbc:mysql://" + c.getString("mysql.ip") + ":" + c.getString("mysql.port") + "/" + c.getString("mysql.database");

        this.connection = DriverManager.getConnection(connectionUri, c.getString("mysql.username"), c.getString("mysql.password"));
        this.connection.setAutoCommit(true);

        String tableCreate = "CREATE TABLE IF NOT EXISTS money (username VARCHAR(64), money DOUBLE(64,0), constraint username_pk primary key(username))";
        Statement createTable = this.connection.createStatement();
        createTable.executeUpdate(tableCreate);

        this.getPlugin().setProviderError(false);
    }

    @SneakyThrows
    @Override
    public void close() {
        this.connection.close();
    }

    @Override
    public void saveAll(boolean async) {
    }

    @SneakyThrows
    @Override
    public boolean hasAccount(String id) {
        ResultSet res = this.connection.createStatement().executeQuery("SELECT * FROM " + this.database + ".money WHERE username='" + id + "'");
        return res.next();
    }

    @SneakyThrows
    @Override
    public void createAccount(String id, double money) {
        PreparedStatement newUser = this.connection.prepareStatement("INSERT INTO " + this.database + ".money (username, money) VALUES (?, ?)");
        newUser.setString(1, id);
        newUser.setDouble(2, money);
        newUser.executeUpdate();
    }

    @SneakyThrows
    @Override
    public double getMoney(String id) {
        ResultSet res = this.connection.createStatement().executeQuery("SELECT * FROM " + this.database + ".money WHERE username='" + id + "'");
        return res.next() ? res.getDouble("money") : 0;
    }

    @SneakyThrows
    @Override
    public void setMoney(String id, double money) {
        ResultSet res = this.connection.createStatement().executeQuery("SELECT * FROM " + this.database + ".money WHERE username='" + id + "'");
        if (res.next()) {
            PreparedStatement upt = this.connection.prepareStatement("UPDATE " + this.database + ".money SET money = ? WHERE username='" + id + "'");
            upt.setDouble(1, money);
            upt.executeUpdate();
        } else {
            this.createAccount(id, money);
        }
    }

    @SneakyThrows
    @Override
    public Map<String, Double> getAll() {
        Map<String, Double> hashMap = new HashMap<>();
        ResultSet res = this.connection.createStatement().executeQuery("SELECT * FROM " + this.database + ".money");
        while (res.next()) {
            hashMap.put(res.getString("username"), res.getDouble("money"));
        }
        return hashMap;
    }

    @Override
    public String getName() {
        return "mysql";
    }
}
