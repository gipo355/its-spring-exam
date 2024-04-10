package com.example.itsspringexam;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

class OrderNeeds {
    private UUID articleId;
    private String articleName;
    private int quantityNeeded;

    public OrderNeeds(UUID articleId, int quantityNeeded, String articleName) {
        this.articleId = articleId;
        this.quantityNeeded = quantityNeeded;
        this.articleName = articleName;
    }

    public UUID getArticleId() {
        return articleId;

    }

    public int getQuantityNeeded() {
        return quantityNeeded;
    }

    public String getArticleName() {
        return articleName;
    }

}

class Article {
    private UUID id;
    private String name;
    private String type;
    private int quantity;
    private double cost;

    public Article(UUID id, String name, int quantity, String type) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.type = type;
        this.cost = 0;
    }

    public UUID getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getCost() {
        return cost;
    }

}

public class DatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=its_java_exam;user=sa;password=Admin123456;encrypt=true;trustServerCertificate=true";

    public static void main(String[] args) {
        Connection conn = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            System.out.println("Connecting to database...");

            conn = DriverManager.getConnection(DB_URL);

            System.out.println("Connected to the database successfully...");

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static Order getOrder(UUID orderId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Order order = null;
        try {
            // Register JDBC driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            // Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL);
            // Execute a query to fetch the order by its id
            String sql = "SELECT * FROM Orders WHERE Id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, orderId.toString());
            rs = pstmt.executeQuery();
            // Check if the order exists
            if (rs.next()) {

                order = new Order(UUID.fromString(rs.getString("Id")), UUID.fromString(rs.getString("Article_id")),
                        rs.getInt("Quantity_to_produce"), rs.getBoolean("Produced"), rs.getDouble("TotalCost"));

            }

            return order;
        } catch (SQLException se) {
            se.printStackTrace();
            throw new RuntimeException(se);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static UUID insertNewOrder(OrderDetails orderDetails) {

        UUID orderId = null;

        String sql = "INSERT INTO Orders (Article_id, Quantity_to_produce, Produced) OUTPUT INSERTED.ID VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);

                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            System.out.println("Connecting to database...");

            pstmt.setString(1, orderDetails.getArticleId().toString());

            pstmt.setInt(2, orderDetails.getQuantityToProduce());

            pstmt.setBoolean(3, orderDetails.isProduced());

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                orderId = UUID.fromString(rs.getString(1));

                System.out.println("A new order was inserted successfully with OrderID: " + orderId);
            }
        } catch (SQLException se) {
            LOGGER.severe(se.getMessage());

            throw new RuntimeException(se);
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());

            throw new RuntimeException(e);
        }

        return orderId;
    }

    public static ArrayList<Article> displayArticles() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        ArrayList<Article> arrayList = new ArrayList<Article>();
        try {
            // Register JDBC driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            // Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL);
            // Execute a query to fetch all articles
            String sql = "SELECT * FROM Articles";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            // Display the articles

            while (rs.next()) {
                UUID articleId = UUID.fromString(rs.getString("Id"));
                String articleName = rs.getString("Name");
                int quantity = rs.getInt("Quantity");
                Article article = new Article(articleId, articleName, quantity, rs.getString("Type"));

                arrayList.add(article);
            }

            return arrayList;
        } catch (SQLException se) {
            se.printStackTrace();

            throw new RuntimeException(se);
        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void calculateOrderNeeds(UUID orderId) {

        Connection conn = null;

        PreparedStatement pstmt = null;

        try {

            // Register JDBC driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Open a connection
            System.out.println("Connecting to database...");

            conn = DriverManager.getConnection(DB_URL);

            // First, delete any existing needs for the order
            String deleteSql = "DELETE FROM Needs WHERE Order_id = ?";

            pstmt = conn.prepareStatement(deleteSql);

            pstmt.setString(1, orderId.toString());

            pstmt.executeUpdate();

            // Get the quantity for the order, find all the childs article needed to produce
            // it, and insert child articles into the needs
            // each child article will have a quantity needed to produce the order
            // which is the child quantity * the quantity to produce

            // First, get the quantity to produce for the order
            String selectSql = "SELECT Quantity_to_produce FROM Orders WHERE Id = ?";

            pstmt = conn.prepareStatement(selectSql);

            pstmt.setString(1, orderId.toString());

            ResultSet rs = pstmt.executeQuery();

            int quantityToProduce = 0;

            if (rs.next()) {
                quantityToProduce = rs.getInt("Quantity_to_produce");
            }

            // get the child article from Links.Article_id_child where
            // Links.Article_id_parent = Orders.Article_id
            // we need the Links.Need_coefficient * Orders.Quantity_to_produce to insert
            // into Needs
            String selectChildSql = "SELECT Article_id_child, Need_coefficient FROM Links" +
                    " WHERE Article_id_father = (SELECT Article_id FROM Orders WHERE Id = ?)";

            pstmt = conn.prepareStatement(selectChildSql);

            pstmt.setString(1, orderId.toString());

            rs = pstmt.executeQuery();

            while (rs.next()) {

                UUID articleId = UUID.fromString(rs.getString("Article_id_child"));

                int needCoefficient = rs.getInt("Need_coefficient");

                int quantityNeeded = needCoefficient * quantityToProduce;

                // Insert the needs for the order
                String insertSql = "INSERT INTO Needs (Order_id, Article_id, Quantity_needed) VALUES (?, ?, ?)";

                pstmt = conn.prepareStatement(insertSql);

                pstmt.setString(1, orderId.toString());

                pstmt.setString(2, articleId.toString());

                pstmt.setInt(3, quantityNeeded);

                pstmt.executeUpdate();
            }

        } catch (SQLException se) {
            se.printStackTrace();

            throw new RuntimeException(se);
        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(e);
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static ArrayList<OrderNeeds> displayOrderNeeds(UUID orderId) {

        Connection conn = null;

        PreparedStatement pstmt = null;

        ResultSet rs = null;

        ArrayList<OrderNeeds> orderNeeds = new ArrayList<OrderNeeds>();

        try {
            // Register JDBC driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Open a connection
            System.out.println("Connecting to database...");

            conn = DriverManager.getConnection(DB_URL);

            // Execute a query to fetch the needs of the order
            // String sql = "SELECT Article_id, Quantity_needed FROM Needs WHERE Order_id =
            // ?";
            // add the article name to the query
            String sql = "SELECT Needs.Article_id, Needs.Quantity_needed, Articles.Name" +
                    " FROM Needs" +
                    " JOIN Articles ON Needs.Article_id = Articles.Id" +
                    " WHERE Order_id = ?";

            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, orderId.toString());

            rs = pstmt.executeQuery();

            // Display the needs of the order
            while (rs.next()) {
                UUID articleId = UUID.fromString(rs.getString("Article_id"));

                int quantityNeeded = rs.getInt("Quantity_needed");

                String articleName = rs.getString("Name");

                orderNeeds.add(new OrderNeeds(articleId, quantityNeeded, articleName));

                System.out.println("Article ID: " + articleId + ", Article Name: " + articleName + ", Quantity needed: "
                        + quantityNeeded);
            }

            return orderNeeds;

        } catch (SQLException se) {
            se.printStackTrace();

            return null;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void processOrder(UUID orderId) {

        Connection conn = null;

        PreparedStatement pstmt = null;

        ResultSet rs = null;
        try {
            // Register JDBC driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Open a connection
            System.out.println("Connecting to database...");

            conn = DriverManager.getConnection(DB_URL);

            // Check if the order has already been processed
            String checkSql = "SELECT Produced FROM Orders WHERE Id = ?";

            pstmt = conn.prepareStatement(checkSql);

            pstmt.setString(1, orderId.toString());

            rs = pstmt.executeQuery();

            if (rs.next() && rs.getBoolean("Produced")) {
                System.out.println("The order has already been processed.");

                throw new RuntimeException("The order has already been processed.");
            }

            // steps for the query
            // 1. reduce Articles.Quantity by Needs.Quantity_needed where Articles.id =
            // Needs.Article_id
            // on Orders.Article_id where Links.Article_id_child = Articles.id
            // can be multiple childs

            String checkNeedsSql = "SELECT Article_id, Quantity_needed FROM Needs WHERE Order_id = ?";

            pstmt = conn.prepareStatement(checkNeedsSql);

            pstmt.setString(1, orderId.toString());

            rs = pstmt.executeQuery();

            while (rs.next()) {
                UUID articleId = UUID.fromString(rs.getString("Article_id"));

                int quantityNeeded = rs.getInt("Quantity_needed");

                // remove the quantity needed from the stock
                String decreaseStockSql = "UPDATE Articles SET Quantity = Quantity - ? WHERE Id = ?";

                pstmt = conn.prepareStatement(decreaseStockSql);

                pstmt.setInt(1, quantityNeeded);

                pstmt.setString(2, articleId.toString());

                pstmt.executeUpdate();

            }

            // update the order to indicate that it has been Produced
            String updateOrderSql = "UPDATE Orders SET Produced = 1 WHERE Id = ?";
            pstmt = conn.prepareStatement(updateOrderSql);
            pstmt.setString(1, orderId.toString());
            pstmt.executeUpdate();
            System.out.println("The order has been processed successfully!");

        } catch (SQLException se) {
            se.printStackTrace();
            throw new RuntimeException(se);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

    }

    public static double updateOrderTotalCost(UUID orderId, double totalCost) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Register JDBC driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            // Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL);
            // Update the totalCost field of the order
            String updateSql = "UPDATE Orders SET TotalCost = ? WHERE Id = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setDouble(1, totalCost);
            pstmt.setString(2, orderId.toString());
            pstmt.executeUpdate();
            System.out.println("Total cost of the order has been updated successfully!");

            return totalCost;
        } catch (SQLException se) {
            se.printStackTrace();
            throw new RuntimeException(se);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
