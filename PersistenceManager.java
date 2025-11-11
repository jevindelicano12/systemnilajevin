import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PersistenceManager {
    private static final String STORE_FILE = "store.json";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void saveStore(Store store) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(STORE_FILE))) {
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
            sb.append(']');

            sb.append('}');
            pw.write(sb.toString());
        } catch (IOException e) {
            System.out.println("⚠️ Failed to save store: " + e.getMessage());
        }
    }

    private static void serializeOrder(StringBuilder sb, BrewiseCoffeeShop.Order order) {
        sb.append('{');
        sb.append("\"orderCode\":").append(quote(order.orderNumber + "")).append(',');
        sb.append("\"datetime\":").append(quote("")).append(',');
        sb.append("\"location\":").append(quote("")).append(',');
        sb.append("\"contactNumber\":").append(quote("")).append(',');
        sb.append("\"items\":[],");
        sb.append("\"subtotal\":").append(order.totalPrice).append(',');
        sb.append("\"tax\":").append(0.0).append(',');
        sb.append("\"total\":").append(order.totalPrice).append(',');
        sb.append("\"paymentReceived\":").append(0.0).append(',');
        sb.append("\"change\":").append(0.0).append(',');
        sb.append("\"dineOption\":").append(quote("")).append(',');
        sb.append("\"customerComplaint\":").append(quote("")).append(',');
        sb.append("\"cashierName\":").append(quote("")).append(',');
        sb.append("\"paid\":").append(order.paid);
        sb.append('}');
    }

    public static Store loadStore() {
        File f = new File(STORE_FILE);
        if (!f.exists()) return new Store();
        
        try {
            String content = new String(java.nio.file.Files.readAllBytes(f.toPath()), 
                                     java.nio.charset.StandardCharsets.UTF_8);
            Object parsed = SimpleJson.parse(content);
            if (!(parsed instanceof Map)) return new Store();
            
            Store store = new Store(false); // don't initialize default data
            Map<?,?> root = (Map<?,?>) parsed;
            
            // Load products
            Object productsObj = root.get("products");
            if (productsObj instanceof List) {
                for (Object o : (List<?>) productsObj) {
                    if (!(o instanceof Map)) continue;
                    Map<?,?> m = (Map<?,?>) o;
                    store.getProducts().add(new Product(
                        getString(m, "id"), getString(m, "category"), 
                        getString(m, "name"), getString(m, "flavour"),
                        getString(m, "description"), getDouble(m, "price"),
                        getInt(m, "stock")
                    ));
                }
            }

            // Load add-ons
            Object addOnsObj = root.get("addOns");
            if (addOnsObj instanceof List) {
                for (Object o : (List<?>) addOnsObj) {
                    if (!(o instanceof Map)) continue;
                    Map<?,?> m = (Map<?,?>) o;
                    store.getAllAddOns().add(new AddOn(
                        getString(m, "id"), getString(m, "name"), 
                        getDouble(m, "price")
                    ));
                }
            }

            // Load inventory
            Object inventoryObj = root.get("inventory");
            if (inventoryObj instanceof Map) {
                Map<?,?> inv = (Map<?,?>) inventoryObj;
                for (Map.Entry<?,?> e : inv.entrySet()) {
                    String id = e.getKey().toString();
                    if (!(e.getValue() instanceof Map)) continue;
                    Map<?,?> m = (Map<?,?>) e.getValue();
                    store.getInventory().put(id, new InventoryItem(
                        id, getString(m, "name"), getString(m, "unit"),
                        getDouble(m, "quantity"), getDouble(m, "threshold")
                    ));
                }
            }

            // Load pending orders
            Object pendingOrdersObj = root.get("pendingOrders");
            if (pendingOrdersObj instanceof Map) {
                Map<?,?> orders = (Map<?,?>) pendingOrdersObj;
                for (Map.Entry<?,?> e : orders.entrySet()) {
                    String code = e.getKey().toString();
                    if (!(e.getValue() instanceof Map)) continue;
                    Map<?,?> m = (Map<?,?>) e.getValue();
                    Order order = deserializeOrder(store, m);
                    order.orderCode = code;
                    store.getPendingOrders().put(code, new BrewiseCoffeeShop.Order(order.drink, order.quantity, order.sugar, order.addOns, order.addOnsQty, order.totalPrice, order.basePricePerUnit));
                }
            }

            // Load sales history
            Object salesHistoryObj = root.get("salesHistory");
            if (salesHistoryObj instanceof List) {
                for (Object o : (List<?>) salesHistoryObj) {
                    if (!(o instanceof Map)) continue;
                    Map<?,?> m = (Map<?,?>) o;
                    store.getSalesHistory().add(new Store.SalesRecord(
                        getString(m, "date"), getString(m, "orderCode"),
                        getDouble(m, "subtotal"), getDouble(m, "tax"),
                        getDouble(m, "total")
                    ));
                }
            }

            return store;
        } catch (IOException e) {
            System.out.println("⚠️ Failed to load store: " + e.getMessage());
            return new Store();
        }
    }

    private static Order deserializeOrder(Store store, Map<?,?> m) {
        Order order = new Order();
        order.location = getString(m, "location");
        order.contactNumber = getString(m, "contactNumber");
        order.datetime = LocalDateTime.parse(getString(m, "datetime"), DATE_FORMATTER);
        order.subtotal = getDouble(m, "subtotal");
        order.tax = getDouble(m, "tax");
        order.total = getDouble(m, "total");
        order.paymentReceived = getDouble(m, "paymentReceived");
        order.change = getDouble(m, "change");
        order.dineOption = getString(m, "dineOption");
        order.customerComplaint = getString(m, "customerComplaint");
        order.cashierName = getString(m, "cashierName");
        order.paid = getBoolean(m, "paid");

        // Load items
        Object itemsObj = m.get("items");
        if (itemsObj instanceof List) {
            for (Object io : (List<?>) itemsObj) {
                if (!(io instanceof Map)) continue;
                Map<?,?> im = (Map<?,?>) io;
                
                // Get product reference from store
                Map<?,?> productMap = (Map<?,?>) im.get("product");
                String productId = getString(productMap, "id");
                Product product = store.findProduct(productId);
                if (product == null) continue;

                // Create order item
                OrderItem item = new OrderItem(product, getInt(im, "quantity"));
                item.setSugarLevel(getString(im, "sugarLevel"));

                // Load add-ons
                Object addOnsObj = im.get("addOns");
                if (addOnsObj instanceof List) {
                    for (Object ao : (List<?>) addOnsObj) {
                        if (!(ao instanceof Map)) continue;
                        Map<?,?> am = (Map<?,?>) ao;
                        String addonId = getString(am, "id");
                        AddOn addon = store.findAddOn(addonId);
                        if (addon == null) continue;
                        item.addAddOn(addon, getInt(am, "quantity"));
                    }
                }

                order.addItem(item);
            }
        }

        return order;
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