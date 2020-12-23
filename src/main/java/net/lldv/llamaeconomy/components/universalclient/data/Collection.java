package net.lldv.llamaeconomy.components.universalclient.data;

import lombok.RequiredArgsConstructor;
import net.lldv.llamaeconomy.components.universalclient.data.connection.Connection;


/**
 * @author LlamaDevelopment
 * @project UniversalClient
 * @website http://llamadevelopment.net/
 */
@RequiredArgsConstructor
public class Collection {

    private final Connection connection;
    private final String collection;

    public void createCollection(CollectionFields columns) {
        this.connection.createCollection(collection, columns);
    }

    public void createCollection(String primaryKey, CollectionFields fields) {
        this.connection.createCollection(collection, primaryKey, fields);
    }

    public void update(UDocument search, UDocument updates) {
        this.connection.update(collection, search, updates);
    }

    public void update(String searchKey, final Object searchValue, UDocument updates) {
        this.connection.update(collection, searchKey, searchValue, updates);
    }

    public void insert(UDocument values) {
        this.connection.insert(collection, values);
    }

    public void delete(UDocument search) {
        this.connection.delete(collection, search);
    }

    public void delete(String key, Object value) {
        this.connection.delete(collection, key, value);
    }

    public UDocumentSet find(UDocument search) {
        return this.connection.find(collection, search);
    }

    public UDocumentSet find(String key, Object value) {
        return this.connection.find(collection, key, value);
    }

    public UDocumentSet find() {
        return this.connection.find(collection);
    }

}
