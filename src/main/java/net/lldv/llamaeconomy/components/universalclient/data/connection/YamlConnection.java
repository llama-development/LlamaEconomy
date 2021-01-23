package net.lldv.llamaeconomy.components.universalclient.data.connection;

import cn.nukkit.utils.Config;
import lombok.Getter;
import net.lldv.llamaeconomy.components.universalclient.data.UDocument;
import net.lldv.llamaeconomy.components.universalclient.data.UDocumentSet;
import net.lldv.llamaeconomy.components.universalclient.data.clientdetails.ClientDetails;
import net.lldv.llamaeconomy.components.universalclient.data.clientdetails.YamlDetails;

import java.util.*;

/**
 * @author LlamaDevelopment
 * @project UniversalClient
 * @website http://llamadevelopment.net/
 */
public class YamlConnection extends Connection {

    @Getter
    private String folder;

    private final Map<String, Config> files = new HashMap<>();

    @Override
    public void connect(final ClientDetails details) throws Exception {
        final YamlDetails info = (YamlDetails) details;
        this.folder = info.getFolderLocation();
    }

    @Override
    public void disconnect() {
        files.values().forEach(Config::save);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void update(String collection, UDocument search, UDocument updates) {
        List<Map<String, Object>> list = (List<Map<String, Object>>) getFile(collection).get("udocs");
        if (list == null) list = new ArrayList<>();
        List<Map<String, Object>> toUpdate = new ArrayList<>();
        list.forEach(map -> {
            map.forEach((key, val) -> {
                if (key.equals(search.first().getKey())) {
                    if (val.equals(search.first().getValue())) {
                        toUpdate.add(map);
                    }
                }
            });
        });
        updates.getAll().forEach((str, obj) -> {
            toUpdate.forEach((map) -> {
                map.put(str, obj);
            });
        });

        getFile(collection).set("udocs", list);
        getFile(collection).save();
    }

    @Override
    public void update(String collection, String searchKey, final Object searchValue, UDocument updates) {
        this.update(collection, new UDocument(searchKey, searchValue), updates);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void insert(String collection, UDocument values) {
        List<Map<String, Object>> list = (List<Map<String, Object>>) getFile(collection).get("udocs");
        if (list == null) list = new ArrayList<>();
        Map<String, Object> map = values.getAll();
        list.add(map);
        getFile(collection).set("udocs", list);
        getFile(collection).save();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void delete(String collection, UDocument search) {
        List<Map<String, Object>> list = (List<Map<String, Object>>) getFile(collection).get("udocs");
        if (list == null) list = new ArrayList<>();
        List<Map<String, Object>> removeThen = new ArrayList<>();
        list.forEach(map -> {
            map.forEach((key, val) -> {
                if (key.equals(search.first().getKey())) {
                    if (val.equals(search.first().getValue())) {
                        removeThen.add(map);
                    }
                }
            });
        });
        removeThen.forEach(list::remove);
        getFile(collection).set("udocs", list);
        getFile(collection).save();
    }

    @Override
    public void delete(String collection, String key, Object value) {
        this.delete(collection, new UDocument(key, value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public UDocumentSet find(String collection, UDocument search) {
        final Set<UDocument> set = new HashSet<>();
        List<Map<String, Object>> list = (List<Map<String, Object>>) getFile(collection).get("udocs");
        if (list == null) list = new ArrayList<>();
        list.forEach(map -> {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (search != null) {
                    if (entry.getKey().equals(search.first().getKey())) {
                        if (entry.getValue().equals(search.first().getValue())) {
                            final UDocument document = new UDocument();
                            map.forEach(document::append);
                            set.add(document);
                            break;
                        }
                    }
                } else {
                    final UDocument document = new UDocument();
                    map.forEach(document::append);
                    set.add(document);
                    break;
                }
            }
        });
        return new UDocumentSet(set);
    }

    @Override
    public UDocumentSet find(String collection, String key, Object value) {
        return this.find(collection, new UDocument(key, value));
    }

    @Override
    public UDocumentSet find(final String collection) {
        return this.find(collection, null);
    }

    private Config getFile(final String name) {
        if (files.containsKey(name)) return files.get(name);
        else {
            final Config collection = new Config(this.folder + "/" + name + ".yml");
            if (collection.getAll().entrySet().size() <= 0) {
                collection.save();
                collection.reload();
            }
            this.files.put(name, collection);
            return collection;
        }
    }

}
