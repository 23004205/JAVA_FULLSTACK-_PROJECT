
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class ApiServer {

    public static void main(String[] args) {

        try {
            HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
            );

            server.createContext("/api/transactions", exchange -> {
                try {
                    handleTransactions(exchange);
                } catch (Exception e) {
                    sendResponse(exchange, 500,
                        "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
                }
            });

            server.start();

            System.out.println("Java API Server Started!");
            System.out.println("Server running on port 8080");
            System.out.println("Frontend API: http://localhost:8080/api/transactions");

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    // Handle GET, POST, PUT and DELETE requests
    private static void handleTransactions(HttpExchange exchange)
            throws IOException, SQLException {

        addCorsHeaders(exchange);

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if (method.equals("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        // Connect to the existing MySQL database
        try (Connection con = DBConnection.connectDatabase()) {

            // GET: Display all transactions
            if (method.equals("GET") &&
                path.equals("/api/transactions")) {

                getTransactions(exchange, con);
            }

            // POST: Add a transaction
            else if (method.equals("POST") &&
                     path.equals("/api/transactions")) {

                addTransaction(exchange, con);
            }

            // PUT: Update a transaction
            else if (method.equals("PUT") &&
                     path.startsWith("/api/transactions/")) {

                int id = getIdFromPath(path);
                updateTransaction(exchange, con, id);
            }

            // DELETE: Delete a transaction
            else if (method.equals("DELETE") &&
                     path.startsWith("/api/transactions/")) {

                int id = getIdFromPath(path);
                deleteTransaction(exchange, con, id);
            }

            else {
                sendResponse(exchange, 404,
                    "{\"error\":\"Endpoint not found\"}");
            }
        }
    }

    // GET: Read transactions from MySQL
    private static void getTransactions(
            HttpExchange exchange, Connection con)
            throws SQLException, IOException {

        String sql = "SELECT * FROM transactions ORDER BY id DESC";

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                if (!first) {
                    json.append(",");
                }

                first = false;

                json.append("{");
                json.append("\"id\":").append(rs.getInt("id")).append(",");
                json.append("\"title\":\"")
                    .append(escapeJson(rs.getString("title"))).append("\",");
                json.append("\"amount\":")
                    .append(rs.getBigDecimal("amount")).append(",");
                json.append("\"type\":\"")
                    .append(escapeJson(rs.getString("type"))).append("\",");
                json.append("\"category\":\"")
                    .append(escapeJson(rs.getString("category"))).append("\",");
                json.append("\"date\":\"")
                    .append(rs.getDate("transaction_date")).append("\"");
                json.append("}");
            }
        }

        json.append("]");

        sendResponse(exchange, 200, json.toString());
    }

    // POST: Insert a transaction into MySQL
    private static void addTransaction(
            HttpExchange exchange, Connection con)
            throws IOException, SQLException {

        Map<String, String> data = readFormData(exchange);

        String sql = "INSERT INTO transactions " +
            "(title, amount, type, category, transaction_date) " +
            "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, required(data, "title"));
            ps.setBigDecimal(2,
                new java.math.BigDecimal(required(data, "amount")));
            ps.setString(3, required(data, "type"));
            ps.setString(4, required(data, "category"));
            ps.setDate(5, Date.valueOf(required(data, "date")));

            int rows = ps.executeUpdate();

            if (rows > 0) {
                sendResponse(exchange, 201,
                    "{\"message\":\"Transaction added successfully\"}");
            } else {
                sendResponse(exchange, 400,
                    "{\"error\":\"Transaction was not added\"}");
            }
        }
    }

    // PUT: Update an existing transaction
    private static void updateTransaction(
            HttpExchange exchange, Connection con, int id)
            throws IOException, SQLException {

        Map<String, String> data = readFormData(exchange);

        String sql = "UPDATE transactions SET " +
            "title = ?, amount = ?, type = ?, category = ?, " +
            "transaction_date = ? WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, required(data, "title"));
            ps.setBigDecimal(2,
                new java.math.BigDecimal(required(data, "amount")));
            ps.setString(3, required(data, "type"));
            ps.setString(4, required(data, "category"));
            ps.setDate(5, Date.valueOf(required(data, "date")));
            ps.setInt(6, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                sendResponse(exchange, 200,
                    "{\"message\":\"Transaction updated successfully\"}");
            } else {
                sendResponse(exchange, 404,
                    "{\"error\":\"Transaction not found\"}");
            }
        }
    }

    // DELETE: Remove a transaction from MySQL
    private static void deleteTransaction(
            HttpExchange exchange, Connection con, int id)
            throws SQLException, IOException {

        String sql = "DELETE FROM transactions WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                sendResponse(exchange, 200,
                    "{\"message\":\"Transaction deleted successfully\"}");
            } else {
                sendResponse(exchange, 404,
                    "{\"error\":\"Transaction not found\"}");
            }
        }
    }

    // Read form data sent by JavaScript
    private static Map<String, String> readFormData(
            HttpExchange exchange) throws IOException {

        InputStream input = exchange.getRequestBody();

        String body = new String(
            input.readAllBytes(), StandardCharsets.UTF_8
        );

        Map<String, String> data = new HashMap<>();

        for (String pair : body.split("&")) {

            String[] parts = pair.split("=", 2);

            String key = URLDecoder.decode(
                parts[0], StandardCharsets.UTF_8
            );

            String value = parts.length > 1
                ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                : "";

            data.put(key, value);
        }

        return data;
    }

    // Check that required form values are present
    private static String required(
            Map<String, String> data, String key) {

        String value = data.get(key);

        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Missing required field: " + key
            );
        }

        return value.trim();
    }

    // Get transaction ID from URL
    private static int getIdFromPath(String path) {

        String idText = path.substring(
            path.lastIndexOf("/") + 1
        );

        return Integer.parseInt(idText);
    }

    // Add CORS headers so the browser can communicate with Java
    private static void addCorsHeaders(HttpExchange exchange) {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin", "*"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods",
            "GET, POST, PUT, DELETE, OPTIONS"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers", "Content-Type"
        );
    }

    // Send a response to the browser
    private static void sendResponse(
            HttpExchange exchange, int status, String response)
            throws IOException {

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
            "Content-Type", "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    // Escape special characters in JSON strings
    private static String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}
