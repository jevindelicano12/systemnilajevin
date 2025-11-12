import java.io.*;
import java.util.*;

/**
 * Fallback Persistence Manager - JSON-based (when SQLite unavailable)
 * Provides JSON file persistence if DatabaseManager is not available
 */
public class PersistenceManager {
    private static final String STORE_FILE = "brweise_store.json";
    private static boolean sqliteAvailable = false;  // FORCED TO JSON MODE ✅
    
    // Get consistent file path - always use project directory
    private static File getStoreFile() {
        String projectDir = System.getProperty("user.dir");
        return new File(projectDir, STORE_FILE);
    }

    static {
        // ✅ FORCED JSON MODE - SQLite disabled
        sqliteAvailable = false;
        System.out.println("✅ JSON Database Mode Active - Data saves to " + STORE_FILE);
        System.out.println("📁 Saving to: " + getStoreFile().getAbsolutePath());
    }

    public static void saveStore(Store store) {
        if (sqliteAvailable) {
            DatabaseManager.saveStore(store);
            return;
        }
        try {
            // Create JSON string
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            
            // Save products
            sb.append("\"products\":[");
            List<Product> products = store.getProducts();
            for (int i = 0; i < products.size(); i++) {
                Product p = products.get(i);
                sb.append('{');
                sb.append("\"id\":").append(quote(p.id)).append(',');
                sb.append("\"category\":").append(quote(p.category)).append(',');
                sb.append("\"name\":").append(quote(p.name)).append(',');
                sb.append("\"flavour\":").append(quote(p.flavour)).append(',');
                sb.append("\"description\":").append(quote(p.description)).append(',');
                sb.append("\"price\":").append(p.price).append(',');
                sb.append("\"stock\":").append(p.stock);
                sb.append('}');
                if (i < products.size() - 1) sb.append(',');
            }
            sb.append("],");

            // Save add-ons
            sb.append("\"addOns\":[");
            List<AddOn> addOns = store.getAllAddOns();
            for (int i = 0; i < addOns.size(); i++) {
                AddOn a = addOns.get(i);
                sb.append('{');
                sb.append("\"id\":").append(quote(a.id)).append(',');
                sb.append("\"name\":").append(quote(a.name)).append(',');
                sb.append("\"price\":").append(a.price);
                sb.append('}');
                if (i < addOns.size() - 1) sb.append(',');
            }
            sb.append("],");

            // Save inventory
            sb.append("\"inventory\":{");
            int k = 0;
            Map<String,InventoryItem> inventory = store.getInventory();
            for (Map.Entry<String,InventoryItem> e : inventory.entrySet()) {
                InventoryItem item = e.getValue();
                sb.append(quote(e.getKey())).append(":{");
                sb.append("\"name\":").append(quote(item.name)).append(',');
                sb.append("\"unit\":").append(quote(item.unit)).append(',');
                sb.append("\"quantity\":").append(item.quantity).append(',');
                sb.append("\"threshold\":").append(item.reorderThreshold);
                sb.append('}');
                if (++k < inventory.size()) sb.append(',');
            }
            sb.append("},");

            // Save pending orders
            sb.append("\"pendingOrders\":{");
            k = 0;
            Map<String,BrewiseCoffeeShop.Order> pendingOrders = store.getPendingOrders();
            for (Map.Entry<String,BrewiseCoffeeShop.Order> e : pendingOrders.entrySet()) {
                sb.append(quote(e.getKey())).append(':');
                serializeOrder(sb, e.getValue());
                if (++k < pendingOrders.size()) sb.append(',');
            }
            sb.append("},");

            // Save sales history
            sb.append("\"salesHistory\":[");
            List<Store.SalesRecord> salesHistory = store.getSalesHistory();
            for (int i = 0; i < salesHistory.size(); i++) {
                Store.SalesRecord r = salesHistory.get(i);
                sb.append('{');
                sb.append("\"date\":").append(quote(r.date)).append(',');
                sb.append("\"orderCode\":").append(quote(r.orderCode)).append(',');
                sb.append("\"subtotal\":").append(r.subtotal).append(',');
                sb.append("\"tax\":").append(r.tax).append(',');
                sb.append("\"total\":").append(r.total);
                sb.append('}');
                if (i < salesHistory.size() - 1) sb.append(',');
            }
            sb.append("],");

            // Save cashiers
            sb.append("\"cashiers\":[");
            List<CashierAccount> cashiers = store.getCashiers();
            for (int i = 0; i < cashiers.size(); i++) {
                CashierAccount c = cashiers.get(i);
                sb.append('{');
                sb.append("\"username\":").append(quote(c.username)).append(',');
                sb.append("\"password\":").append(quote(c.password)).append(',');
                sb.append("\"status\":").append(quote(c.status));
                sb.append('}');
                if (i < cashiers.size() - 1) sb.append(',');
            }
            sb.append(']');

            sb.append('}');
            
            // Write to file using consistent path
            File jsonFile = getStoreFile();
            File dir = jsonFile.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(jsonFile))) {
                bw.write(sb.toString());
                bw.flush();
            }
            System.out.println("✅ Store saved to JSON: " + jsonFile.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("⚠️ Failed to save store: " + e.getMessage());
        }
    }

    private static void serializeOrder(StringBuilder sb, BrewiseCoffeeShop.Order order) {
        sb.append('{');
        sb.append("\"orderCode\":").append(quote(order.orderCode)).append(',');
        sb.append("\"drink\":").append(quote(order.drink)).append(',');
        sb.append("\"quantity\":").append(order.quantity).append(',');
        sb.append("\"sugar\":").append(quote(order.sugar)).append(',');
        sb.append("\"totalPrice\":").append(order.totalPrice).append(',');
        sb.append("\"basePricePerUnit\":").append(order.basePricePerUnit).append(',');
        sb.append("\"transactionId\":").append(quote(order.transactionId)).append(',');
        sb.append("\"paid\":").append(order.paid);
        sb.append('}');
    }

    public static Store loadStore() {
        if (sqliteAvailable) {
            return DatabaseManager.loadStore();
        }
        
        File f = getStoreFile();  // Use consistent path
        if (!f.exists()) return null;
        
        try {
            String content = new String(java.nio.file.Files.readAllBytes(f.toPath()), 
                                     java.nio.charset.StandardCharsets.UTF_8);
            Object parsed = SimpleJson.parse(content);
            if (!(parsed instanceof Map)) return null;
            
            Store store = new Store(false); // don't initialize default data
            Map<?,?> root = (Map<?,?>) parsed;
            
            // Load products using setter method (not through copy)
            List<Product> loadedProducts = new ArrayList<>();
            Object productsObj = root.get("products");
            if (productsObj instanceof List) {
                List<?> productsList = (List<?>) productsObj;
                for (Object o : productsList) {
                    if (!(o instanceof Map)) continue;
                    Map<?,?> m = (Map<?,?>) o;
                    Product p = new Product(
                        getString(m, "id"), getString(m, "category"), 
                        getString(m, "name"), getString(m, "flavour"),
                        getString(m, "description"), getDouble(m, "price"), 0
                    );
                    p.available = getBoolean(m, "available");
                    loadedProducts.add(p);
                }
            }
            store.setProducts(loadedProducts);

            // Load add-ons using setter method
            List<AddOn> loadedAddOns = new ArrayList<>();
            Object addOnsObj = root.get("addOns");
            if (addOnsObj instanceof List) {
                for (Object o : (List<?>) addOnsObj) {
                    if (!(o instanceof Map)) continue;
                    Map<?,?> m = (Map<?,?>) o;
                    loadedAddOns.add(new AddOn(
                        getString(m, "id"), getString(m, "name"), 
                        getDouble(m, "price")
                    ));
                }
            }
            store.setAddOns(loadedAddOns);

            // Load inventory using setter method
            Map<String, InventoryItem> loadedInventory = new HashMap<>();
            Object inventoryObj = root.get("inventory");
            if (inventoryObj instanceof Map) {
                Map<?,?> inv = (Map<?,?>) inventoryObj;
                for (Map.Entry<?,?> e : inv.entrySet()) {
                    String id = e.getKey().toString();
                    if (!(e.getValue() instanceof Map)) continue;
                    Map<?,?> m = (Map<?,?>) e.getValue();
                    loadedInventory.put(id, new InventoryItem(
                        id, getString(m, "name"), getString(m, "unit"),
                        getDouble(m, "quantity"), getDouble(m, "threshold")
                    ));
                }
            }
            store.setInventory(loadedInventory);

            // Load pending orders using setter method
            Map<String, BrewiseCoffeeShop.Order> loadedPendingOrders = new HashMap<>();
            Object pendingOrdersObj = root.get("pendingOrders");
            if (pendingOrdersObj instanceof Map) {
                Map<?,?> orders = (Map<?,?>) pendingOrdersObj;
                for (Map.Entry<?,?> e : orders.entrySet()) {
                    String code = e.getKey().toString();
                    if (!(e.getValue() instanceof Map)) continue;
                    Map<?,?> m = (Map<?,?>) e.getValue();
                    BrewiseCoffeeShop.Order order = new BrewiseCoffeeShop.Order(
                        getString(m, "drink"),
                        getInt(m, "quantity"),
                        getString(m, "sugar"),
                        new ArrayList<>(),
                        new HashMap<>(),
                        getDouble(m, "totalPrice"),
                        getDouble(m, "basePricePerUnit")
                    );
                    order.orderCode = code;
                    order.transactionId = getString(m, "transactionId");
                    order.paid = getBoolean(m, "paid");
                    loadedPendingOrders.put(code, order);
                }
            }
            store.setPendingOrders(loadedPendingOrders);

            // Load sales history using setter method
            List<Store.SalesRecord> loadedSalesHistory = new ArrayList<>();
            Object salesHistoryObj = root.get("salesHistory");
            if (salesHistoryObj instanceof List) {
                for (Object o : (List<?>) salesHistoryObj) {
                    if (!(o instanceof Map)) continue;
                    Map<?,?> m = (Map<?,?>) o;
                    loadedSalesHistory.add(new Store.SalesRecord(
                        getString(m, "date"), getString(m, "orderCode"),
                        getDouble(m, "subtotal"), getDouble(m, "tax"),
                        getDouble(m, "total")
                    ));
                }
            }
            store.setSalesHistory(loadedSalesHistory);

            // Load cashiers using setter method
            List<CashierAccount> loadedCashiers = new ArrayList<>();
            Object cashiersObj = root.get("cashiers");
            if (cashiersObj instanceof List) {
                for (Object o : (List<?>) cashiersObj) {
                    if (!(o instanceof Map)) continue;
                    Map<?,?> m = (Map<?,?>) o;
                    String username = getString(m, "username");
                    String password = getString(m, "password");
                    String status = getString(m, "status");
                    CashierAccount cashier = new CashierAccount(username, password, status);
                    loadedCashiers.add(cashier);
                }
            }
            store.setCashiers(loadedCashiers);

            return store;
        } catch (IOException e) {
            System.out.println("⚠️ Failed to load store: " + e.getMessage());
            return null;
        }
    }

    private static String quote(String s) {
        if (s == null) return "null";
        return '"' + s.replace("\\", "\\\\")
                     .replace("\"", "\\\"")
                     .replace("\n", "\\n") + '"';
    }

    private static String getString(Map<?,?> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }

    private static int getInt(Map<?,?> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) return ((Number) value).intValue();
        return 0;
    }

    private static double getDouble(Map<?,?> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) return ((Number) value).doubleValue();
        return 0.0;
    }

    private static boolean getBoolean(Map<?,?> map, String key) {
        Object value = map.get(key);
        if (value instanceof Boolean) return (Boolean) value;
        return false;
    }

    static class SimpleJson {
        static Object parse(String s) {
            Parser p = new Parser(s);
            return p.parseValue();
        }

        static class Parser {
            final String s; int i = 0;
            Parser(String s) { this.s = s; }
            void skip() { while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++; }
            Object parseValue() {
                skip(); if (i >= s.length()) return null;
                char c = s.charAt(i);
                if (c == '{') return parseObject();
                if (c == '[') return parseArray();
                if (c == '"') return parseString();
                if (c == 't' || c == 'f') return parseBoolean();
                if (c == 'n') { parseNull(); return null; }
                return parseNumber();
            }
            void parseNull() { i += 4; }
            Boolean parseBoolean() { if (s.startsWith("true", i)) { i+=4; return Boolean.TRUE; } else { i+=5; return Boolean.FALSE; } }
            Number parseNumber() {
                skip(); int start = i; if (s.charAt(i)=='-') i++;
                while (i < s.length() && (Character.isDigit(s.charAt(i)) || s.charAt(i)=='.' || s.charAt(i)=='e' || s.charAt(i)=='E' || s.charAt(i)=='+' || s.charAt(i)=='-')) i++;
                String tok = s.substring(start, i);
                if (tok.contains(".") || tok.contains("e") || tok.contains("E")) return Double.parseDouble(tok);
                else return Long.parseLong(tok);
            }
            String parseString() {
                StringBuilder sb = new StringBuilder();
                i++; // skip '"'
                while (i < s.length()) {
                    char c = s.charAt(i++);
                    if (c == '"') break;
                    if (c == '\\') {
                        if (i >= s.length()) break;
                        char e = s.charAt(i++);
                        switch (e) {
                            case '"': sb.append('"'); break;
                            case '\\': sb.append('\\'); break;
                            case '/': sb.append('/'); break;
                            case 'b': sb.append('\b'); break;
                            case 'f': sb.append('\f'); break;
                            case 'n': sb.append('\n'); break;
                            case 'r': sb.append('\r'); break;
                            case 't': sb.append('\t'); break;
                            case 'u':
                                String hex = s.substring(i, i+4); i+=4; sb.append((char) Integer.parseInt(hex, 16)); break;
                        }
                    } else sb.append(c);
                }
                return sb.toString();
            }
            Map<String,Object> parseObject() {
                Map<String,Object> map = new LinkedHashMap<>();
                i++; // skip '{'
                skip();
                if (i < s.length() && s.charAt(i) == '}') { i++; return map; }
                while (i < s.length()) {
                    skip(); String key = parseString(); skip(); if (i < s.length() && s.charAt(i) == ':') i++; Object val = parseValue(); map.put(key, val); skip();
                    if (i < s.length() && s.charAt(i) == ',') { i++; continue; }
                    if (i < s.length() && s.charAt(i) == '}') { i++; break; }
                }
                return map;
            }
            List<Object> parseArray() {
                List<Object> list = new ArrayList<>();
                i++; // skip '['
                skip();
                if (i < s.length() && s.charAt(i) == ']') { i++; return list; }
                while (i < s.length()) {
                    Object v = parseValue(); list.add(v); skip();
                    if (i < s.length() && s.charAt(i) == ',') { i++; continue; }
                    if (i < s.length() && s.charAt(i) == ']') { i++; break; }
                }
                return list;
            }
        }
    }
}