package net.lldv.llamaeconomy.components.universalclient.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;

/**
 * @author LlamaDevelopment
 * @project UniversalClient
 * @website http://llamadevelopment.net/
 */
public class CollectionFields {

    private final LinkedList<Field> columns = new LinkedList<>();

    public CollectionFields(String name, String type) {
        this.columns.add(new Field(name, type, 0, 0));
    }

    public CollectionFields(String name, String type, int size) {
        this.columns.add(new Field(name, type, size, 0));
    }

    public CollectionFields(String name, String type, int sizeFrom, int sizeTo) {
        this.columns.add(new Field(name, type, sizeFrom, sizeTo));
    }

    public CollectionFields append(String name, String type) {
        this.columns.add(new Field(name, type, 0, 0));
        return this;
    }

    public CollectionFields append(String name, String type, int size) {
        this.columns.add(new Field(name, type, size, 0));
        return this;
    }

    public CollectionFields append(String name, String type, int sizeFrom, int sizeTo) {
        this.columns.add(new Field(name, type, sizeFrom, sizeTo));
        return this;
    }

    public LinkedList<Field> get() {
        return this.columns;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Field {

        private final String name, type;
        private final int sizeFrom, sizeTo;

    }

    public static class Type {
        public final static String INT = "INT";
        public final static String TINYINT = "TINYINT";
        public final static String SMALLINT = "SMALLINT";
        public final static String MEDIUMINT = "MEDIUMINT";
        public final static String BIGINT = "BIGINT";
        public final static String LONG = "BIGINT";
        public final static String FLOAT = "FLOAT";
        public final static String DOUBLE = "DOUBLE";
        public final static String CHAR = "CHAR";
        public final static String VARCHAR = "VARCHAR";
        public final static String BLOB = "BLOB";
        public final static String TEXT = "TEXT";
        public final static String TINYBLOB = "TINYBLOB";
        public final static String TINYTEXT = "TINYTEXT";
        public final static String MEDIUMBLOB = "MEDIUMBLOB";
        public final static String MEDIUMTEXT = "MEDIUMTEXT";
        public final static String LONGBLOB = "LONGBLOB";
        public final static String LONGTEXT = "LONGTEXT";
    }

}
