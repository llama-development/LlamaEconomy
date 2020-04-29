package net.lldv.LlamaEconomy;

import cn.nukkit.command.CommandMap;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import net.lldv.LlamaEconomy.commands.*;
import net.lldv.LlamaEconomy.listener.PlayerListener;
import net.lldv.LlamaEconomy.provider.BaseProvider;
import net.lldv.LlamaEconomy.provider.MySQLProvider;
import net.lldv.LlamaEconomy.provider.YAMLProvider;
import net.lldv.LlamaEconomy.utils.API;
import net.lldv.LlamaEconomy.utils.Language;

import java.util.HashMap;

public class LlamaEconomy extends PluginBase {

    public static LlamaEconomy instance;
    public static API api;

    public static double defaultMoney;
    public static String monetaryUnit;

    private BaseProvider provider;
    private HashMap<String, BaseProvider> providers = new HashMap<>();

    public void registerProvider(BaseProvider baseProvider) {
        providers.put(baseProvider.getName(), baseProvider);
    }

    @Override
    public void onLoad() {
        instance = this;
        registerProvider(new YAMLProvider());
        registerProvider(new MySQLProvider());
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Config config = getConfig();

        Language.init();

        getLogger().info(Language.getNoPrefix("starting"));

        defaultMoney = config.getDouble("default-money");
        monetaryUnit = config.getString("monetary-unit");

        provider = providers.get(config.getString("provider"));
        getLogger().info(Language.getAndReplaceNoPrefix("provider", provider.getName()));
        provider.init();
        api = new API(provider);

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        CommandMap cmd = getServer().getCommandMap();

        cmd.register(this, new MoneyCommand());
        cmd.register(this, new SetMoneyCommand());
        cmd.register(this, new AddMoneyCommand());
        cmd.register(this, new ReduceMoneyCommand());
        cmd.register(this, new PayCommand());
        cmd.register(this, new TopMoneyCommand());

        saveTask(config.getInt("saveInterval") * 20);
        getLogger().info(Language.getNoPrefix("done-starting"));
    }

    @Override
    public void onDisable() {
        provider.close();
    }

    public void reload() {
        Language.init();
    }

    public static LlamaEconomy getInstance() {
        return instance;
    }

    public static API getAPI() {
        return api;
    }

    private void saveTask(int saveInterval) {
        getServer().getScheduler().scheduleDelayedRepeatingTask(this, () -> provider.saveAll(true), saveInterval, saveInterval);
    }
}
