package net.lldv.llamaeconomy.components.provider;

import cn.nukkit.Server;
import cn.nukkit.utils.Config;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import net.lldv.llamaeconomy.LlamaEconomy;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Proxma
 * @project LlamaEconomy
 * @website http://llamadevelopment.net/
 */
public class MongoDBProvider extends BaseProvider {

    private MongoClient client;
    private MongoCollection<Document> collection;

    public MongoDBProvider(LlamaEconomy plugin) {
        super(plugin);
    }

    public void init() {
        Config config = this.getPlugin().getConfig();
        this.client = new MongoClient(new MongoClientURI(config.getString("mongodb.uri")));
        this.collection = this.client.getDatabase(config.getString("mongodb.database")).getCollection("money");
        this.getPlugin().setProviderError(false);
    }

    public void close() {
        this.client.close();
    }

    public void saveAll(boolean async) { };

    public boolean hasAccount(String id) {
        final Document doc = this.collection.find(new Document("_id", id)).first();
        return doc != null;
    }

    public void createAccount(String id, double money) {
        final Document newDoc = new Document("_id", id)
                .append("money", money);
        this.collection.insertOne(newDoc);
    }

    public double getMoney(String id) {
        final Document doc = this.collection.find(new Document("_id", id)).first();
        assert doc != null;
        return doc.getDouble("money");
    }

    public void setMoney(String id, double money) {
        this.collection.updateOne(new Document("_id", id), new Document("$set", new Document("money", money)));
    }

    public Map<String, Double> getAll() {
        final Map<String, Double> map = new HashMap<>();

        for (Document doc : this.collection.find())
            map.put(doc.getString("_id"), doc.getDouble("money"));

        return map;
    }

    public String getName() {
        return "MongoDB";
    }

}
