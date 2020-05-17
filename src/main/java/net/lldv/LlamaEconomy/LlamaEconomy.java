package net.lldv.LlamaEconomy;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.registry.CommandRegistry;
import cn.nukkit.utils.Config;
import net.lldv.LlamaEconomy.commands.*;
import net.lldv.LlamaEconomy.listener.PlayerListener;
import net.lldv.LlamaEconomy.provider.BaseProvider;
import net.lldv.LlamaEconomy.provider.MySQLProvider;
import net.lldv.LlamaEconomy.provider.YAMLProvider;
import net.lldv.LlamaEconomy.utils.API;
import net.lldv.LlamaEconomy.utils.Language;

import java.text.DecimalFormat;
import java.util.HashMap;

public class LlamaEconomy extends PluginBase {

    public static LlamaEconomy instance;
    public static API api;

    public static double defaultMoney;
    public static String monetaryUnit;
    public static DecimalFormat moneyFormat;

    public static boolean providerError = true;

    private BaseProvider provider;
    private HashMap<String, BaseProvider> providers = new HashMap<>();

    public void registerProvider(BaseProvider baseProvider) {
        providers.put(baseProvider.getName(), baseProvider);
    }

    @Override
    public void onLoad() {
        instance = this;
        moneyFormat = new DecimalFormat();
        moneyFormat.setMaximumFractionDigits(2);
        registerProvider(new YAMLProvider());
        registerProvider(new MySQLProvider());
        registerCommands();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Config config = getConfig();

        Language.init();

        getLogger().info(Language.getNoPrefix("starting"));

        defaultMoney = config.getDouble("default-money");
        monetaryUnit = config.getString("monetary-unit");

        provider = providers.get(config.getString("provider").toLowerCase());
        getLogger().info(Language.getAndReplaceNoPrefix("provider", provider.getName()));
        provider.init();

        if (providerError) {
            getLogger().warn("--- ERROR ---");
            getLogger().warn("§cCouldn't load LlamaEconomy: An error occurred while loading the provider \"" + provider.getName() + "\"!");
            getLogger().warn("--- ERROR ---");
            getServer().getPluginManager().disablePlugin(instance);
            return;
        }

        api = new API(provider);

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        saveTask(config.getInt("saveInterval") * 20);
        getLogger().info(Language.getNoPrefix("done-starting"));
    }

    public void registerCommands() {
        CommandRegistry cmd = getServer().getCommandRegistry();

        cmd.register(this, new MoneyCommand(this));
        cmd.register(this, new SetMoneyCommand(this));
        cmd.register(this, new AddMoneyCommand(this));
        cmd.register(this, new ReduceMoneyCommand(this));
        cmd.register(this, new PayCommand(this));
        cmd.register(this, new TopMoneyCommand(this));
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
