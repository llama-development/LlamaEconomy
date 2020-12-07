package net.lldv.llamaeconomy;

import cn.nukkit.command.CommandMap;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import lombok.Getter;
import lombok.Setter;
import net.lldv.llamaeconomy.commands.*;
import net.lldv.llamaeconomy.components.provider.MongoDBProvider;
import net.lldv.llamaeconomy.listener.PlayerListener;
import net.lldv.llamaeconomy.components.provider.BaseProvider;
import net.lldv.llamaeconomy.components.provider.MySQLProvider;
import net.lldv.llamaeconomy.components.provider.YAMLProvider;
import net.lldv.llamaeconomy.components.api.API;
import net.lldv.llamaeconomy.components.language.Language;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class LlamaEconomy extends PluginBase {

    @Getter
    private static API API;

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
    private final Map<String, BaseProvider> providers = new HashMap<>();

    public void registerProvider(BaseProvider baseProvider) {
        providers.put(baseProvider.getName(), baseProvider);
    }

    @Override
    public void onLoad() {
        this.moneyFormat = new DecimalFormat();
        this.moneyFormat.setMaximumFractionDigits(2);
        this.registerProvider(new YAMLProvider(this));
        this.registerProvider(new MySQLProvider(this));
        this.registerProvider(new MongoDBProvider(this));
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        final Config config = this.getConfig();

        Language.init(this);

        this.getLogger().info("§aStarting LlamaEconomy...");

        this.defaultMoney = config.getDouble("default-money");
        this.monetaryUnit = config.getString("monetary-unit");

        this.provider = providers.get(config.getString("provider").toLowerCase());
        this.getLogger().info("§eUsing Provider: " + this.provider.getName());
        this.provider.init();

        if (providerError) {
            this.getLogger().warning("--- ERROR ---");
            this.getLogger().warning("§cCouldn't load LlamaEconomy: An error occurred while loading the provider \"" + provider.getName() + "\"!");
            this.getLogger().warning("--- ERROR ---");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        API = new API(this, provider);

        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.registerCommands(config);

        this.saveTask(config.getInt("saveInterval") * 20);
        this.getLogger().info("§aDone.");
    }

    public void registerCommands(Config config) {
        CommandMap cmd = getServer().getCommandMap();

        cmd.register("money", new MoneyCommand(this, config.getSection("commands.money")));
        cmd.register("setmoney", new SetMoneyCommand(this, config.getSection("commands.setmoney")));
        cmd.register("addmoney", new AddMoneyCommand(this, config.getSection("commands.addmoney")));
        cmd.register("reducemoney", new ReduceMoneyCommand(this, config.getSection("commands.reducemoney")));
        cmd.register("pay", new PayCommand(this, config.getSection("commands.pay")));
        cmd.register("topmoney", new TopMoneyCommand(this, config.getSection("commands.topmoney")));
        cmd.register("lecoreload", new LecoReloadCommand(this, config.getSection("commands.lecoreload")));
    }

    @Override
    public void onDisable() {
        getAPI().saveAll(false);
        this.provider.close();
    }

    public void reload() {
        Language.init(this);
    }

    private void saveTask(int saveInterval) {
        this.getServer().getScheduler().scheduleDelayedRepeatingTask(this, () -> getAPI().saveAll(true), saveInterval, saveInterval);
    }
}
