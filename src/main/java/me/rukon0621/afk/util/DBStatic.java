package me.rukon0621.afk.util;

import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DBStatic {

    private static String url = "jdbc:mysql://172.20.0.102:3306/";
    private static final String admin = "admin";
    private static final String password = "7259";
    private static final Map<String, Connection> connections = new HashMap<>();

    public static void setUrl(String url) {
        DBStatic.url = url;
    }

    @Nullable
    public static Connection getConnection(String name) {
        try {
            if (connections.containsKey(name)) return connections.get(name);
            DriverManager.setLoginTimeout(4);
            connections.put(name, DriverManager.getConnection(url + name, admin, password));
            return connections.get(name);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void close(String name) {
        if (connections.containsKey(name)) {
            try {
                connections.get(name).close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeAll() {
        for (Connection connection : connections.values()) {
            try {
                connection.close();
            } catch (SQLException ignored) {}
        }
    }

}
