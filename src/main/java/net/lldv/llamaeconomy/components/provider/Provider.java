package net.lldv.llamaeconomy.components.provider;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.lldv.llamaeconomy.components.universalclient.UniversalClient;
import net.lldv.llamaeconomy.components.universalclient.data.Collection;
import net.lldv.llamaeconomy.components.universalclient.data.CollectionFields;
import net.lldv.llamaeconomy.components.universalclient.data.UDocument;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LlamaDevelopment
 * @project MobEarn
 * @website http://llamadevelopment.net/
 */
@RequiredArgsConstructor
public class Provider {

    @Getter
    private final UniversalClient client;

    private Collection collection;

    public void init() {
        this.collection = this.client.getCollection("money");
        this.collection.createCollection("_id",
                new CollectionFields("_id", CollectionFields.Type.VARCHAR, 32)
                        .append("money", CollectionFields.Type.DOUBLE)
        );
    }

    public void close() {
        this.client.disconnect();
    }

    public boolean hasAccount(String id) {
        return this.collection.find("_id", id).first() != null;
    }

    public void createAccount(String id, double money) {
        this.collection.insert(
                new UDocument("_id", id)
                        .append("money", money)
        );
    }

    public double getMoney(String id) {
        final UDocument doc = this.collection.find("_id", id).first();
        return doc != null ? doc.getDouble("money") : 0;
    }

    public void setMoney(String id, double money) {
        this.collection.update("_id", id, new UDocument("money", money));
    }

    public Map<String, Double> getAll() {
        final Map<String, Double> map = new HashMap<>();
        this.collection.find().getAll().forEach((udoc) -> map.put(udoc.getString("_id"), udoc.getDouble("money")));
        return map;
    }
}
