package net.lldv.LlamaEconomy.provider;

import cn.nukkit.utils.Config;
import net.lldv.LlamaEconomy.LlamaEconomy;

import java.sql.*;
import java.util.HashMap;

public class MySQLProvider implements BaseProvider {

    private Connection connection;
    private String database;

    @Override
    public void init() {
        try {
            Config c = LlamaEconomy.instance.getConfig();

            Class.forName("com.mysql.cj.jdbc.Driver");
            database = c.getString("mysql.database");
            String connectionUri = "jdbc:mysql://" + c.getString("mysql.ip") + ":" + c.getString("mysql.port") + "/" + c.getString("mysql.database");

            connection = DriverManager.getConnection(connectionUri, c.getString("mysql.username"), c.getString("mysql.password"));
            connection.setAutoCommit(true);

            String tableCreate = "CREATE TABLE IF NOT EXISTS money (username VARCHAR(64), money DOUBLE(64,0), constraint username_pk primary key(username))";
            Statement createTable = connection.createStatement();
            createTable.executeUpdate(tableCreate);

            LlamaEconomy.providerError = false;
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("It was not possible to establish a connection with the database.");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            System.out.println("MySQL Driver is missing... Have you installed DbLib correctly?");
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void saveAll(boolean async) {
    }

    @Override
    public boolean hasAccount(String id) {
        try {
            ResultSet res = connection.createStatement().executeQuery("SELECT * FROM " + database + ".money WHERE username='" + id + "'");
            if (res.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public void createAccount(String id, double money) {
        try {
            PreparedStatement newUser = connection.prepareStatement("INSERT INTO " + database + ".money (username, money) VALUES (?, ?)");
            newUser.setString(1, id);
            newUser.setDouble(2, money);
            newUser.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public double getMoney(String id) {
        try {
            ResultSet res = connection.createStatement().executeQuery("SELECT * FROM " + database + ".money WHERE username='" + id + "'");
            if (res.next()) {
                return res.getDouble("money");
            } else {
                return 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public void setMoney(String id, double money) {
        try {
            ResultSet res = connection.createStatement().executeQuery("SELECT * FROM " + database + ".money WHERE username='" + id + "'");
            if (res.next()) {
                PreparedStatement upt = connection.prepareStatement("UPDATE " + database + ".money SET money = ? WHERE username='" + id + "'");
                upt.setDouble(1, money);
                upt.executeUpdate();
            } else {
                createAccount(id, money);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public HashMap<String, Double> getAll() {
        try {
            HashMap<String, Double> hashMap = new HashMap<>();
            ResultSet res = connection.createStatement().executeQuery("SELECT * FROM " + database + ".money");
            while (res.next()) {
                hashMap.put(res.getString("username"), res.getDouble("money"));
            }
            return hashMap;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public String getName() {
        return "mysql";
    }
}
