package net.lldv.llamaeconomy.components.universalclient;

import lombok.Getter;
import net.lldv.llamaeconomy.components.universalclient.data.Collection;
import net.lldv.llamaeconomy.components.universalclient.data.CollectionFields;
import net.lldv.llamaeconomy.components.universalclient.data.UDocument;
import net.lldv.llamaeconomy.components.universalclient.data.UDocumentSet;
import net.lldv.llamaeconomy.components.universalclient.data.clientdetails.ClientDetails;
import net.lldv.llamaeconomy.components.universalclient.data.connection.Connection;
import net.lldv.llamaeconomy.components.universalclient.data.connection.MongoDbConnection;
import net.lldv.llamaeconomy.components.universalclient.data.connection.MySqlConnection;
import net.lldv.llamaeconomy.components.universalclient.data.connection.YamlConnection;

/**
 * @author LlamaDevelopment
 * @project UniversalClient
 * @website http://llamadevelopment.net/
 */
public class UniversalClient {

    private Connection connection;
    @Getter
    private boolean errored;
    @Getter
    private Exception exception;

    public UniversalClient(Type type, ClientDetails details) {
        try {
            switch (type) {
                case Yaml:
                    this.connection = new YamlConnection();
                    this.connection.connect(details);
                    break;
                case MySql:
                    this.connection = new MySqlConnection();
                    this.connection.connect(details);
                    break;
                case MongoDB:
                    this.connection = new MongoDbConnection();
                    this.connection.connect(details);
                    break;
            }
            this.errored = false;
        } catch (Exception exception) {
            this.errored = true;
            this.exception = exception;
        }
    }

    public void disconnect() {
        this.connection.disconnect();
    }

    public void insert(final String collection, final UDocument document) {
        this.connection.insert(collection, document);
    }

    /* This method is only for MySql *poor sql noises* */
    public void createCollection(String name, CollectionFields columns) {
        this.connection.createCollection(name, columns);
    }

    /* This method is only for MySql *poor sql noises* */
    public void createCollection(String collection, String primaryKey, CollectionFields fields) {
        this.connection.createCollection(collection, primaryKey, fields);
    }

    public void update(String collection, UDocument search, UDocument updates) {
        this.connection.update(collection, search, updates);
    }

    public void update(String collection, String searchKey, final Object searchValue, UDocument updates) {
        this.connection.update(collection, searchKey, searchValue, updates);
    }

    public void delete(String collection, UDocument search) {
        this.connection.delete(collection, search);
    }

    public void delete(String collection, String key, Object value) {
        this.connection.delete(collection, key, value);
    }

    public UDocumentSet find(String collection, UDocument search) {
        return this.connection.find(collection, search);
    }

    public UDocumentSet find(String collection, String key, Object value) {
        return this.connection.find(collection, key, value);
    }

    public UDocumentSet find(final String collection) {
        return this.connection.find(collection);
    }

    public Collection getCollection(final String name) {
        return this.connection.getCollection(name);
    }

    public enum Type {
        Yaml, MySql, MongoDB
    }

}
