package net.lldv.llamaeconomy.components.universalclient.simplesqlclient;

import net.lldv.llamaeconomy.components.universalclient.simplesqlclient.objects.SqlColumn;
import net.lldv.llamaeconomy.components.universalclient.simplesqlclient.objects.SqlDocument;
import net.lldv.llamaeconomy.components.universalclient.simplesqlclient.objects.SqlDocumentSet;

import java.sql.*;
import java.util.*;

/**
 * @author LlamaDevelopment
 * @project SimpleSQLClient
 * @website http://llamadevelopment.net/
 */
public class MySqlClient {

    private Connection connection;
    private final String host, port, username, password, database;

    public MySqlClient(String host, String port, String user, String password, String database) throws Exception {
        this.host = host;
        this.port = port;
        this.username = user;
        this.password = password;
        this.database = database;
        this.connect();
    }

    public void connect() throws Exception {
        this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useGmtMillisForDatetimes=true&serverTimezone=GMT", username, password);
    }

    public void close() {
        try {
            this.connection.close();
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }

    public void createTable(String name, SqlColumn columns) {
        try {
            StringBuilder columnsStringBuilder = new StringBuilder();

            for (String type : columns.get()) {
                columnsStringBuilder.append(type).append(", ");
            }

            String columnsString = columnsStringBuilder.substring(0, columnsStringBuilder.length() - 2);
            String statement = "CREATE TABLE IF NOT EXISTS " + name + "(" + columnsString + ");";

            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }

    public void createTable(String name, String primaryKey, SqlColumn columns) {
        try {
            StringBuilder columnsStringBuilder = new StringBuilder();

            for (String type : columns.get()) {
                columnsStringBuilder.append(type).append(", ");
            }

            String columnsString = columnsStringBuilder.toString();
            String statement = "CREATE TABLE IF NOT EXISTS " + name + "(" + columnsString + "PRIMARY KEY (" + primaryKey + "));";

            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }

    public void update(String table, SqlDocument search, SqlDocument updates) {
        this.update(table, search.first().getKey(), search.first().getValue(), updates);
    }

    @SuppressWarnings("unchecked")
    public void update(String table, String searchKey, final Object searchValue, SqlDocument updates) {
        try {

            Object valueSearch = searchValue;
            if (valueSearch instanceof String) valueSearch = "'" + valueSearch + "'";

            StringBuilder updateBuilder = new StringBuilder();

            for (Map.Entry<String, Object> update : updates.getAll().entrySet()) {

                Object data = update.getValue();

                if (data instanceof List) {
                    final StringBuilder newData = new StringBuilder("LIST|");
                    List<String> list = (List<String>) data;
                    list.forEach((str) -> newData.append(str).append("|"));
                    data = newData.substring(0, newData.length() - 1);
                }

                if (data instanceof String) data = "'" + data + "'";

                updateBuilder.append(update.getKey()).append(" = ").append(data).append(", ");
            }

            String update = updateBuilder.substring(0, updateBuilder.length() - 2);
            String statement = "UPDATE " + table + " SET " + update + " WHERE " + searchKey + " = " + valueSearch + ";";

            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void insert(String table, SqlDocument values) {
        try {
            StringBuilder valueNamesBuilder = new StringBuilder("(");
            StringBuilder valueDataBuilder = new StringBuilder("(");

            for (Map.Entry<String, Object> insert : values.getAll().entrySet()) {

                Object data = insert.getValue();

                if (data instanceof List) {
                    final StringBuilder newData = new StringBuilder("LIST|");
                    List<String> list = (List<String>) data;
                    list.forEach((str) -> newData.append(str).append("|"));
                    data = newData.substring(0, newData.length() - 1);
                }

                if (data instanceof String) data = "'" + data + "'";

                valueNamesBuilder.append(insert.getKey()).append(", ");
                valueDataBuilder.append(data).append(", ");
            }

            String valueNames = valueNamesBuilder.substring(0, valueNamesBuilder.length() - 2);
            valueNames = valueNames + ")";

            String valueData = valueDataBuilder.substring(0, valueDataBuilder.length() - 2);
            valueData = valueData + ")";


            String statementString = "INSERT INTO " + table + " " + valueNames + " VALUES " + valueData + ";";
            PreparedStatement statement = connection.prepareStatement(statementString);
            statement.executeUpdate();
            statement.close();
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }

    public void delete(String table, SqlDocument search) {
        this.delete(table, search.first().getKey(), search.first().getValue());
    }

    // DELETE FROM requests WHERE RID = ?");
    public void delete(String table, String key, Object value) {
        try {
            if (value instanceof String) value = "'" + value + "'";
            String statement = "DELETE FROM " + table + " WHERE " + key + " = " + value + ";";

            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.executeUpdate();
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }

    public SqlDocumentSet find(String table, SqlDocument search) {
        return this.find(table, search.first().getKey(), search.first().getValue());
    }

    public SqlDocumentSet find(String table, String key, Object value) {
        try {
            if (value instanceof String) value = "'" + value + "'";
            String statement = "SELECT * FROM " + table + " WHERE " + key + " = " + value + ";";

            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData meta = resultSet.getMetaData();

            Set<SqlDocument> set = new HashSet<>();
            while (resultSet.next()) {
                Map<String, Object> map = new HashMap<>();

                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    final String name = meta.getColumnName(i);
                    final Object obj = resultSet.getObject(i);
                    if (obj instanceof String) {
                        String str = obj.toString();
                        if (str.startsWith("LIST|")) {
                            List<String> list = new ArrayList<>();
                            String[] array = str.split("\\|");
                            boolean first = true;
                            for (String s : array) {
                                if (!first) {
                                    list.add(s);
                                } else {
                                    first = false;
                                }
                            }
                            map.put(name, list);
                        } else map.put(name, resultSet.getObject(i));
                    } else {
                        map.put(name, resultSet.getObject(i));
                    }
                }

                set.add(new SqlDocument(map));
            }

            preparedStatement.close();
            return new SqlDocumentSet(set);

        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public SqlDocumentSet find(String table) {
        try {
            String statement = "SELECT * FROM " + table + ";";

            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData meta = resultSet.getMetaData();

            Set<SqlDocument> set = new HashSet<>();
            while (resultSet.next()) {
                Map<String, Object> map = new HashMap<>();

                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    final String name = meta.getColumnName(i);
                    final Object obj = resultSet.getObject(i);
                    if (obj instanceof String) {
                        String str = obj.toString();
                        if (str.startsWith("LIST|")) {
                            List<String> list = new ArrayList<>();
                            String[] array = str.split("\\|");
                            boolean first = true;
                            for (String s : array) {
                                if (!first) {
                                    list.add(s);
                                } else {
                                    first = false;
                                }
                            }
                            map.put(name, list);
                        } else map.put(name, resultSet.getObject(i));
                    } else {
                        map.put(name, resultSet.getObject(i));
                    }
                }

                set.add(new SqlDocument(map));
            }

            preparedStatement.close();
            return new SqlDocumentSet(set);
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

}
