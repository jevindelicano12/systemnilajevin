import java.sql.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * SQLite Database Manager for Brewise Coffee Shop
 * Handles all data persistence operations using SQLite
 */
public class DatabaseManager {
    private static final String DATABASE_URL = "jdbc:sqlite:brewise_coffee.db";
    private static final String DRIVER = "org.sqlite.JDBC";
    private static boolean initialized = false;

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("❌ SQLite JDBC Driver not found!");
            System.out.println("📌 Please ensure sqlite-jdbc-3.x.x.jar is in the classpath");
        }
    }

    /**
     * Initialize database schema on first run
     */
    public static void initializeDatabase() {
        if (initialized) return;
        
        try (Connection conn = getConnection()) {
            Statement stmt = conn.createStatement();
            
            // Products table
            stmt.execute("CREATE TABLE IF NOT EXISTS products (" +
                "id TEXT PRIMARY KEY, " +
                "category TEXT NOT NULL, " +
                "name TEXT NOT NULL, " +
                "flavour TEXT, " +
                "description TEXT, " +
                "price REAL NOT NULL, " +
                "available BOOLEAN DEFAULT 1, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")");

            // Add-ons table
            stmt.execute("CREATE TABLE IF NOT EXISTS addons (" +
                "id TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")");

            // Inventory table
            stmt.execute("CREATE TABLE IF NOT EXISTS inventory (" +
                "id TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "unit TEXT NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "reorder_threshold REAL NOT NULL, " +
                "last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")");

            // Orders table
            stmt.execute("CREATE TABLE IF NOT EXISTS orders (" +
                "order_code TEXT PRIMARY KEY, " +
                "total_price REAL NOT NULL, " +
                "transaction_id TEXT, " +
                "paid BOOLEAN DEFAULT 0, " +
                "status TEXT DEFAULT 'pending', " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "completed_at TIMESTAMP" +
                ")");

            // Order Items table (for multi-item orders)
            stmt.execute("CREATE TABLE IF NOT EXISTS order_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_code TEXT NOT NULL, " +
                "product_name TEXT NOT NULL, " +
                "quantity INTEGER NOT NULL, " +
                "sugar_level TEXT, " +
                "addons TEXT, " +
                "item_price REAL NOT NULL, " +
                "FOREIGN KEY(order_code) REFERENCES orders(order_code)" +
                ")");

            // Sales History table
            stmt.execute("CREATE TABLE IF NOT EXISTS sales_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date TEXT NOT NULL, " +
                "order_code TEXT NOT NULL, " +
                "subtotal REAL NOT NULL, " +
                "tax REAL NOT NULL, " +
                "total REAL NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")");

            // Cashier Accounts table
            stmt.execute("CREATE TABLE IF NOT EXISTS cashier_accounts (" +
                "id TEXT PRIMARY KEY, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "status TEXT DEFAULT 'active', " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")");

            // Transaction Counter table
            stmt.execute("CREATE TABLE IF NOT EXISTS transaction_counter (" +
                "id INTEGER PRIMARY KEY CHECK (id = 1), " +
                "counter INTEGER DEFAULT 0" +
                ")");

            stmt.close();
            initialized = true;
            System.out.println("✅ Database initialized successfully");
        } catch (SQLException e) {
            System.out.println("❌ Database initialization failed: " + e.getMessage());
        }
    }

    /**
     * Get database connection
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    /**
     * Save all store data to database
     */
    public static void saveStore(Store store) {
        try (Connection conn = getConnection()) {
            // Clear existing data
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("DELETE FROM order_items");
                stmt.execute("DELETE FROM orders");
                stmt.execute("DELETE FROM products");
                stmt.execute("DELETE FROM addons");
                stmt.execute("DELETE FROM inventory");
                stmt.execute("DELETE FROM sales_history");
            }

            // Save products
            saveProducts(conn, store.getProducts());

            // Save add-ons
            saveAddOns(conn, store.getAllAddOns());

            // Save inventory
            saveInventory(conn, store.getInventory());

            // Save pending orders
            savePendingOrders(conn, store.getPendingOrders());

            // Save sales history
            saveSalesHistory(conn, store.getSalesHistory());

            System.out.println("✅ Data saved to database successfully");
        } catch (SQLException e) {
            System.out.println("❌ Failed to save store: " + e.getMessage());
        }
    }

    /**
     * Load all store data from database
     */
    public static Store loadStore() {
        Store store = new Store(false); // don't initialize default data
        
        try (Connection conn = getConnection()) {
            loadProducts(conn, store);
            loadAddOns(conn, store);
            loadInventory(conn, store);
            loadPendingOrders(conn, store);
            loadSalesHistory(conn, store);
            System.out.println("✅ Data loaded from database successfully");
        } catch (SQLException e) {
            System.out.println("❌ Failed to load store: " + e.getMessage());
        }
        
        return store;
    }

    // ===== SAVE OPERATIONS =====

    private static void saveProducts(Connection conn, List<Product> products) throws SQLException {
        String sql = "INSERT INTO products (id, category, name, flavour, description, price, available) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Product p : products) {
                pstmt.setString(1, p.id);
                pstmt.setString(2, p.category);
                pstmt.setString(3, p.name);
                pstmt.setString(4, p.flavour);
                pstmt.setString(5, p.description);
                pstmt.setDouble(6, p.price);
                pstmt.setBoolean(7, p.available);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private static void saveAddOns(Connection conn, List<AddOn> addOns) throws SQLException {
        String sql = "INSERT INTO addons (id, name, price) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (AddOn a : addOns) {
                pstmt.setString(1, a.id);
                pstmt.setString(2, a.name);
                pstmt.setDouble(3, a.price);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private static void saveInventory(Connection conn, Map<String, InventoryItem> inventory) throws SQLException {
        String sql = "INSERT INTO inventory (id, name, unit, quantity, reorder_threshold) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Map.Entry<String, InventoryItem> e : inventory.entrySet()) {
                InventoryItem item = e.getValue();
                pstmt.setString(1, e.getKey());
                pstmt.setString(2, item.name);
                pstmt.setString(3, item.unit);
                pstmt.setDouble(4, item.quantity);
                pstmt.setDouble(5, item.reorderThreshold);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private static void savePendingOrders(Connection conn, Map<String, BrewiseCoffeeShop.Order> orders) throws SQLException {
        String orderSql = "INSERT INTO orders (order_code, total_price, transaction_id, paid, status) VALUES (?, ?, ?, ?, ?)";
        String itemSql = "INSERT INTO order_items (order_code, product_name, quantity, sugar_level, addons, item_price) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement orderStmt = conn.prepareStatement(orderSql);
             PreparedStatement itemStmt = conn.prepareStatement(itemSql)) {
            
            for (Map.Entry<String, BrewiseCoffeeShop.Order> e : orders.entrySet()) {
                BrewiseCoffeeShop.Order order = e.getValue();
                
                // Save order
                orderStmt.setString(1, order.orderCode);
                orderStmt.setDouble(2, order.totalPrice);
                orderStmt.setString(3, order.transactionId);
                orderStmt.setBoolean(4, order.paid);
                orderStmt.setString(5, order.paid ? "completed" : "pending");
                orderStmt.addBatch();
                
                // Save order items
                if (order.items != null && !order.items.isEmpty()) {
                    for (OrderItem item : order.items) {
                        itemStmt.setString(1, order.orderCode);
                        itemStmt.setString(2, item.product.name);
                        itemStmt.setInt(3, item.quantity);
                        itemStmt.setString(4, item.sugarLevel);
                        
                        // Serialize add-ons
                        StringBuilder addonsStr = new StringBuilder();
                        for (AddOn addon : item.getAddOns()) {
                            if (addonsStr.length() > 0) addonsStr.append(",");
                            addonsStr.append(addon.name);
                        }
                        itemStmt.setString(5, addonsStr.toString());
                        itemStmt.setDouble(6, item.getTotal());
                        itemStmt.addBatch();
                    }
                }
            }
            orderStmt.executeBatch();
            itemStmt.executeBatch();
        }
    }

    private static void saveSalesHistory(Connection conn, List<Store.SalesRecord> records) throws SQLException {
        String sql = "INSERT INTO sales_history (date, order_code, subtotal, tax, total) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Store.SalesRecord r : records) {
                pstmt.setString(1, r.date);
                pstmt.setString(2, r.orderCode);
                pstmt.setDouble(3, r.subtotal);
                pstmt.setDouble(4, r.tax);
                pstmt.setDouble(5, r.total);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    // ===== LOAD OPERATIONS =====

    private static void loadProducts(Connection conn, Store store) throws SQLException {
        String sql = "SELECT * FROM products";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Product p = new Product(
                    rs.getString("id"),
                    rs.getString("category"),
                    rs.getString("name"),
                    rs.getString("flavour"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    0
                );
                p.available = rs.getBoolean("available");
                store.getProducts().add(p);
            }
        }
    }

    private static void loadAddOns(Connection conn, Store store) throws SQLException {
        String sql = "SELECT * FROM addons";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                store.getAllAddOns().add(new AddOn(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getDouble("price")
                ));
            }
        }
    }

    private static void loadInventory(Connection conn, Store store) throws SQLException {
        String sql = "SELECT * FROM inventory";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                store.getInventory().put(
                    rs.getString("id"),
                    new InventoryItem(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("unit"),
                        rs.getDouble("quantity"),
                        rs.getDouble("reorder_threshold")
                    )
                );
            }
        }
    }

    private static void loadPendingOrders(Connection conn, Store store) throws SQLException {
        String orderSql = "SELECT * FROM orders WHERE status = 'pending'";
        String itemSql = "SELECT * FROM order_items WHERE order_code = ?";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(orderSql)) {
            
            while (rs.next()) {
                String orderCode = rs.getString("order_code");
                List<OrderItem> items = new ArrayList<>();
                String firstProductName = "";
                
                // Load items for this order
                try (PreparedStatement itemStmt = conn.prepareStatement(itemSql)) {
                    itemStmt.setString(1, orderCode);
                    try (ResultSet itemRs = itemStmt.executeQuery()) {
                        while (itemRs.next()) {
                            // Find the product from store
                            String productName = itemRs.getString("product_name");
                            Product product = null;
                            for (Product p : store.getProducts()) {
                                if (p.name.equals(productName)) {
                                    product = p;
                                    break;
                                }
                            }
                            
                            if (product != null && firstProductName.isEmpty()) {
                                firstProductName = product.name;
                            }
                            
                            if (product != null) {
                                OrderItem item = new OrderItem(product, itemRs.getInt("quantity"));
                                item.setSugarLevel(itemRs.getString("sugar_level"));
                                items.add(item);
                            }
                        }
                    }
                }
                
                // Create order with loaded items
                String drinkName = firstProductName.isEmpty() ? "" : firstProductName;
                BrewiseCoffeeShop.Order order = new BrewiseCoffeeShop.Order(
                    drinkName,
                    items.isEmpty() ? 0 : items.get(0).quantity,
                    items.isEmpty() ? "" : items.get(0).sugarLevel,
                    new ArrayList<>(),  // empty addOns for new constructor style
                    new HashMap<>(),     // empty addOnsQty
                    rs.getDouble("total_price"),
                    0
                );
                order.items = items;  // Set the items list for multi-item orders
                order.orderCode = orderCode;
                order.transactionId = rs.getString("transaction_id");
                order.paid = rs.getBoolean("paid");
                
                store.getPendingOrders().put(orderCode, order);
            }
        }
    }

    private static void loadSalesHistory(Connection conn, Store store) throws SQLException {
        String sql = "SELECT * FROM sales_history ORDER BY created_at DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                store.getSalesHistory().add(new Store.SalesRecord(
                    rs.getString("date"),
                    rs.getString("order_code"),
                    rs.getDouble("subtotal"),
                    rs.getDouble("tax"),
                    rs.getDouble("total")
                ));
            }
        }
    }

    // ===== UTILITY METHODS =====

    /**
     * Get transaction counter for auto-incrementing transaction IDs
     */
    public static int getNextTransactionCounter() {
        try (Connection conn = getConnection()) {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT counter FROM transaction_counter WHERE id = 1")) {
                int counter = 0;
                if (rs.next()) {
                    counter = rs.getInt("counter") + 1;
                }
                
                // Update counter
                try (PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT OR REPLACE INTO transaction_counter (id, counter) VALUES (1, ?)")) {
                    pstmt.setInt(1, counter);
                    pstmt.executeUpdate();
                }
                return counter;
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Error getting transaction counter: " + e.getMessage());
            return 1;
        }
    }

    /**
     * Get sales summary for a date range
     */
    public static Map<String, Object> getSalesSummary(String startDate, String endDate) {
        Map<String, Object> summary = new HashMap<>();
        try (Connection conn = getConnection()) {
            String sql = "SELECT COUNT(*) as count, SUM(total) as total FROM sales_history WHERE date BETWEEN ? AND ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, startDate);
                pstmt.setString(2, endDate);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        summary.put("orderCount", rs.getInt("count"));
                        summary.put("totalRevenue", rs.getDouble("total"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Error getting sales summary: " + e.getMessage());
        }
        return summary;
    }

    /**
     * Get top products by sales
     */
    public static List<Map<String, Object>> getTopProducts(int limit) {
        List<Map<String, Object>> topProducts = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "SELECT product_name, SUM(quantity) as total_qty, SUM(item_price) as total_revenue " +
                         "FROM order_items GROUP BY product_name ORDER BY total_revenue DESC LIMIT ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, limit);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> product = new HashMap<>();
                        product.put("name", rs.getString("product_name"));
                        product.put("quantity", rs.getInt("total_qty"));
                        product.put("revenue", rs.getDouble("total_revenue"));
                        topProducts.add(product);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Error getting top products: " + e.getMessage());
        }
        return topProducts;
    }

    /**
     * Get all completed orders
     */
    public static List<Map<String, Object>> getCompletedOrders() {
        List<Map<String, Object>> orders = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM orders WHERE status = 'completed' ORDER BY completed_at DESC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Map<String, Object> order = new HashMap<>();
                    order.put("orderCode", rs.getString("order_code"));
                    order.put("totalPrice", rs.getDouble("total_price"));
                    order.put("transactionId", rs.getString("transaction_id"));
                    order.put("createdAt", rs.getString("created_at"));
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Error getting completed orders: " + e.getMessage());
        }
        return orders;
    }

    /**
     * Database health check
     */
    public static boolean healthCheck() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeQuery("SELECT 1");
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Database health check failed: " + e.getMessage());
            return false;
        }
    }
}
