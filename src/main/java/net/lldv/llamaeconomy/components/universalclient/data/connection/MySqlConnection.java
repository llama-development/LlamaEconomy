package net.lldv.llamaeconomy.components.universalclient.data.connection;

import lombok.Getter;
import net.lldv.llamaeconomy.components.universalclient.data.CollectionFields;
import net.lldv.llamaeconomy.components.universalclient.data.UDocument;
import net.lldv.llamaeconomy.components.universalclient.data.UDocumentSet;
import net.lldv.llamaeconomy.components.universalclient.data.clientdetails.ClientDetails;
import net.lldv.llamaeconomy.components.universalclient.data.clientdetails.MySqlDetails;
import net.lldv.llamaeconomy.components.universalclient.simplesqlclient.MySqlClient;
import net.lldv.llamaeconomy.components.universalclient.simplesqlclient.objects.SqlColumn;
import net.lldv.llamaeconomy.components.universalclient.simplesqlclient.objects.SqlDocument;
import net.lldv.llamaeconomy.components.universalclient.simplesqlclient.objects.SqlDocumentSet;

import java.util.HashSet;
import java.util.Set;

/**
 * @author LlamaDevelopment
 * @project UniversalClient
 * @website http://llamadevelopment.net/
 */
public class MySqlConnection extends Connection {

    @Getter
    private MySqlClient client;

    @Override
    public void connect(final ClientDetails details) throws Exception {
        final MySqlDetails info = (MySqlDetails) details;

        this.client = new MySqlClient(
                info.getHost(),
                info.getPort(),
                info.getUser(),
                info.getPassword(),
                info.getDatabase()
        );
    }

    @Override
    public void disconnect() {
        this.client.close();
    }

    @Override
    public void createCollection(String name, CollectionFields columns) {
        this.client.createTable(name, convertTo(columns));
    }

    @Override
    public void createCollection(String collection, String primaryKey, CollectionFields fields) {
        this.client.createTable(collection, primaryKey, convertTo(fields));
    }

    @Override
    public void update(String collection, UDocument search, UDocument updates) {
        this.client.update(collection, convertTo(search), convertTo(updates));
    }

    @Override
    public void update(String collection, String searchKey, final Object searchValue, UDocument updates) {
        this.update(collection, new UDocument(searchKey, searchValue), updates);
    }

    @Override
    public void insert(String collection, UDocument values) {
        this.client.insert(collection, convertTo(values));
    }

    @Override
    public void delete(String collection, UDocument search) {
        this.client.delete(collection, convertTo(search));
    }

    @Override
    public void delete(String collection, String key, Object value) {
        this.delete(collection, new UDocument(key, value));
    }

    @Override
    public UDocumentSet find(String collection, UDocument search) {
        final Set<UDocument> set = new HashSet<>();
        final SqlDocumentSet ss = search != null ? this.client.find(collection, convertTo(search)) : this.client.find(collection);
        for (SqlDocument document : ss.getAll()) set.add(convertFrom(document));
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

    private SqlColumn convertTo(final CollectionFields fields) {
        final SqlColumn column = new SqlColumn();
        fields.get().forEach((f) -> {
            if (f.getSizeFrom() != 0 && f.getSizeTo() != 0) {
                column.append(f.getName(), f.getType(), f.getSizeFrom(), f.getSizeTo());
            } else if (f.getSizeFrom() != 0) {
                column.append(f.getName(), f.getType(), f.getSizeFrom());
            } else {
                column.append(f.getName(), f.getType());
            }
        });
        return column;
    }

    private SqlDocument convertTo(final UDocument document) {
        SqlDocument doc = new SqlDocument();
        document.getAll().forEach(doc::append);
        return doc;
    }

    private UDocument convertFrom(final SqlDocument document) {
        UDocument doc = new UDocument();
        document.getAll().forEach(doc::append);
        return doc;
    }

}
