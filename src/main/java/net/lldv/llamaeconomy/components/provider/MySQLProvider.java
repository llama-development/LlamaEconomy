package net.lldv.llamaeconomy.components.provider;

import cn.nukkit.utils.Config;
import lombok.SneakyThrows;
import net.lldv.llamaeconomy.LlamaEconomy;
import net.lldv.simplesqlclient.MySqlClient;
import net.lldv.simplesqlclient.objects.SqlColumn;
import net.lldv.simplesqlclient.objects.SqlInsert;
import net.lldv.simplesqlclient.objects.SqlResult;
import net.lldv.simplesqlclient.objects.SqlUpdate;

import java.util.HashMap;
import java.util.Map;

public class MySQLProvider extends BaseProvider {

    private MySqlClient client;

    public MySQLProvider(LlamaEconomy plugin) {
        super(plugin);
    }

    @SneakyThrows
    @Override
    public void init() {
        Config config = this.getPlugin().getConfig();

        this.client = new MySqlClient(
                config.getString("mysql.ip"),
                config.getString("mysql.port"),
                config.getString("mysql.username"),
                config.getString("mysql.password"),
                config.getString("mysql.database")
        );

        this.client.createTable("money", "username",
                new SqlColumn("username", "VARCHAR", 32),
                new SqlColumn("money", "DOUBLE")
        );

        this.getPlugin().setProviderError(false);
    }

    @Override
    public void close() {
        this.client.close();
    }

    @Override
    public void saveAll(boolean async) {
    }

    @Override
    public boolean hasAccount(String id) {
        SqlResult result = this.client.find("money", "username", id).first();
        return result != null;
    }

    @Override
    public void createAccount(String id, double money) {
        this.client.insert("money",
                new SqlInsert("username", id),
                new SqlInsert("money", money)
        );
    }

    @Override
    public double getMoney(String id) {
        SqlResult result = this.client.find("money", "username", id).first();
        return result != null ? result.getDouble("money") : 0;
    }

    @Override
    public void setMoney(String id, double money) {
        SqlResult result = this.client.find("money", "username", id).first();
        if (result != null) {
            this.client.update("money", "username", id,
                    new SqlUpdate("money", money)
            );
        } else this.createAccount(id, money);
    }

    @Override
    public Map<String, Double> getAll() {
        Map<String, Double> map = new HashMap<>();
        for (SqlResult result : this.client.find("money").getAll())
            map.put(result.getString("username"), result.getDouble("money"));
        return map;
    }

    @Override
    public String getName() {
        return "mysql";
    }
}
