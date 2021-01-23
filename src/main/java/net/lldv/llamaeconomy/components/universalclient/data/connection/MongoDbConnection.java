package net.lldv.llamaeconomy.components.universalclient.data.connection;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import net.lldv.llamaeconomy.components.universalclient.data.UDocument;
import net.lldv.llamaeconomy.components.universalclient.data.UDocumentSet;
import net.lldv.llamaeconomy.components.universalclient.data.clientdetails.ClientDetails;
import net.lldv.llamaeconomy.components.universalclient.data.clientdetails.MongoDbDetails;
import org.bson.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * @author LlamaDevelopment
 * @project UniversalClient
 * @website http://llamadevelopment.net/
 */
public class MongoDbConnection extends Connection {

    @Getter
    private MongoClient client;
    @Getter
    private MongoDatabase database;

    @Override
    public void connect(final ClientDetails details) throws Exception {
        final MongoDbDetails info = (MongoDbDetails) details;
        this.client = new MongoClient(new MongoClientURI(info.getUri()));
        this.database = this.client.getDatabase(info.getDb());
    }

    @Override
    public void disconnect() {
        this.client.close();
    }

    @Override
    public void update(String collection, UDocument search, UDocument updates) {
        this.database.getCollection(collection).updateOne(convertTo(search), new Document("$set", convertTo(updates)));
    }

    @Override
    public void update(String collection, String searchKey, final Object searchValue, UDocument updates) {
        this.update(collection, new UDocument(searchKey, searchValue), updates);
    }

    @Override
    public void insert(String collection, UDocument values) {
        this.database.getCollection(collection).insertOne(convertTo(values));
    }

    @Override
    public void delete(String collection, UDocument search) {
        this.database.getCollection(collection).deleteOne(convertTo(search));
    }

    @Override
    public void delete(String collection, String key, Object value) {
        this.delete(collection, new UDocument(key, value));
    }

    @Override
    public UDocumentSet find(String collection, UDocument search) {
        final Set<UDocument> set = new HashSet<>();
        final FindIterable<Document> fi = search != null ? this.database.getCollection(collection).find(convertTo(search)) : this.database.getCollection(collection).find();
        for (Document document : fi) set.add(convertFrom(document));
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

    private Document convertTo(final UDocument document) {
        Document doc = new Document();
        document.getAll().forEach(doc::append);
        return doc;
    }

    private UDocument convertFrom(final Document document) {
        UDocument doc = new UDocument();
        document.forEach(doc::append);
        return doc;
    }

}
