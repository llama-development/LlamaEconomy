package net.lldv.LlamaEconomy;

import cn.nukkit.command.CommandMap;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import lombok.Getter;
import lombok.Setter;
import net.lldv.LlamaEconomy.commands.*;
import net.lldv.LlamaEconomy.listener.PlayerListener;
import net.lldv.LlamaEconomy.components.provider.BaseProvider;
import net.lldv.LlamaEconomy.components.provider.MySQLProvider;
import net.lldv.LlamaEconomy.components.provider.YAMLProvider;
import net.lldv.LlamaEconomy.components.api.API;
import net.lldv.LlamaEconomy.components.language.Language;

import java.text.DecimalFormat;
import java.util.HashMap;

public class LlamaEconomy extends PluginBase {

    @Getter
    private static LlamaEconomy instance;
    @Getter
    public static API API;

    @Getter
    private double defaultMoney;
    @Getter
    private String monetaryUnit;
    @Getter
    private DecimalFormat moneyFormat;
    @Getter
    @Setter
    private boolean providerError = true;

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
        registerProvider(new YAMLProvider(this));
        registerProvider(new MySQLProvider(this));
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
            getLogger().warning("--- ERROR ---");
            getLogger().warning("§cCouldn't load LlamaEconomy: An error occurred while loading the provider \"" + provider.getName() + "\"!");
            getLogger().warning("--- ERROR ---");
            getServer().getPluginManager().disablePlugin(instance);
            return;
        }

        API = new API(provider);

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        saveTask(config.getInt("saveInterval") * 20);
        getLogger().info(Language.getNoPrefix("done-starting"));
    }

    public void registerCommands() {
        CommandMap cmd = getServer().getCommandMap();

        cmd.register("money", new MoneyCommand(this));
        cmd.register("setmoney", new SetMoneyCommand(this));
        cmd.register("addmoney", new AddMoneyCommand(this));
        cmd.register("reducemoney", new ReduceMoneyCommand(this));
        cmd.register("pay", new PayCommand(this));
        cmd.register("topmoney", new TopMoneyCommand(this));
    }

    @Override
    public void onDisable() {
        provider.close();
    }

    public void reload() {
        Language.init();
    }

    private void saveTask(int saveInterval) {
        getServer().getScheduler().scheduleDelayedRepeatingTask(this, () -> provider.saveAll(true), saveInterval, saveInterval);
    }
}
