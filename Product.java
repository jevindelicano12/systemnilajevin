import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    String id;
    String category;
    String name;
    String flavour;
    String description;
    double price;
    int stock;
    boolean available;  // Track if product is available for ordering
    Map<String, Map<String, Double>> recipe;  // ingredient_id -> (size -> amount)

    Product(String id, String category, String name, String flavour, String description, double price, int stock) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.flavour = flavour;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.available = true;  // Default to available
        this.recipe = new HashMap<>();
    }
}