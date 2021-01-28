package net.lldv.llamaeconomy;

import cn.nukkit.command.CommandMap;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import com.creeperface.nukkit.placeholderapi.api.PlaceholderAPI;
import lombok.Getter;
import net.lldv.llamaeconomy.commands.*;
import net.lldv.llamaeconomy.components.api.API;
import net.lldv.llamaeconomy.components.language.Language;
import net.lldv.llamaeconomy.components.provider.Provider;
import net.lldv.llamaeconomy.components.universalclient.UniversalClient;
import net.lldv.llamaeconomy.components.universalclient.data.clientdetails.MongoDbDetails;
import net.lldv.llamaeconomy.components.universalclient.data.clientdetails.MySqlDetails;
import net.lldv.llamaeconomy.components.universalclient.data.clientdetails.YamlDetails;
import net.lldv.llamaeconomy.listener.PlayerListener;

import java.text.DecimalFormat;

public class LlamaEconomy extends PluginBase {

    @Getter
    private static API API;

    @Getter
    private double defaultMoney;
    @Getter
    private String monetaryUnit;
    @Getter
    private DecimalFormat moneyFormat;

    private Provider provider;

    @Override
    public void onLoad() {
        this.moneyFormat = new DecimalFormat();
        this.moneyFormat.setMaximumFractionDigits(2);
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        final Config config = this.getConfig();

        Language.init(this);

        this.getLogger().info("§aStarting LlamaEconomy...");

        this.defaultMoney = config.getDouble("DefaultMoney");
        this.monetaryUnit = config.getString("MonetaryUnit");

        UniversalClient client;
        switch (this.getConfig().getString("Provider").toLowerCase()) {
            case "mysql":
                client = new UniversalClient(
                        UniversalClient.Type.MySql,
                        new MySqlDetails(
                                this.getConfig().getString("MySql.Host"),
                                this.getConfig().getString("MySql.Port"),
                                this.getConfig().getString("MySql.User"),
                                this.getConfig().getString("MySql.Password"),
                                this.getConfig().getString("MySql.Database")
                        )
                );
                break;
            case "mongodb":
                client = new UniversalClient(
                        UniversalClient.Type.MongoDB,
                        new MongoDbDetails(
                                this.getConfig().getString("MongoDB.Uri"),
                                this.getConfig().getString("MongoDB.Database")
                        )
                );
                break;
            case "yaml":
                client = new UniversalClient(
                        UniversalClient.Type.Yaml,
                        new YamlDetails(
                                this.getDataFolder().toString() + "/data"
                        )
                );
                break;
            default:
                this.getLogger().error("§4Please specify a valid provider: Yaml, MySql, MongoDB");
                this.getServer().getPluginManager().disablePlugin(this);
                return;
        }
        this.getLogger().info("§eUsing Provider: " + config.getString("Provider").toLowerCase());
        this.provider = new Provider(client);
        this.provider.init();

        if (this.provider.getClient().isErrored()) {
            this.getLogger().warning("--- ERROR ---");
            this.getLogger().error("Error: ", this.provider.getClient().getException());
            this.getLogger().warning("§cCouldn't load LlamaEconomy: An error occurred while loading the provider \"" + config.getString("Provider") + "\"!");
            this.getLogger().warning("--- ERROR ---");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        API = new API(this, provider);

        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.registerCommands(config);

        this.getServer().getPluginManager().getPlugins().values().forEach(e -> {
            if (e.getName().equalsIgnoreCase("PlaceholderAPI")) this.placeholder();
        });

        this.getLogger().info("§aDone.");
    }

    public void placeholder() {
        final PlaceholderAPI api = PlaceholderAPI.getInstance();
        api.visitorSensitivePlaceholder("money", (p, pp) -> {
            final double money = LlamaEconomy.getAPI().getMoney(p);
            return LlamaEconomy.getAPI().getMoneyFormat().format(money);
        }); /* jenkins test thingy */
    }

    public void registerCommands(Config config) {
        CommandMap cmd = getServer().getCommandMap();

        cmd.register("money", new MoneyCommand(this, config.getSection("Commands.Money")));
        cmd.register("setmoney", new SetMoneyCommand(this, config.getSection("Commands.Setmoney")));
        cmd.register("addmoney", new AddMoneyCommand(this, config.getSection("Commands.Addmoney")));
        cmd.register("reducemoney", new ReduceMoneyCommand(this, config.getSection("Commands.Reducemoney")));
        cmd.register("pay", new PayCommand(this, config.getSection("Commands.Pay")));
        cmd.register("topmoney", new TopMoneyCommand(this, config.getSection("Commands.Topmoney")));
        cmd.register("lecoreload", new LecoReloadCommand(this, config.getSection("Commands.Lecoreload")));
    }

    @Override
    public void onDisable() {
        this.provider.close();
    }

    public void reload() {
        Language.init(this);
    }
}
