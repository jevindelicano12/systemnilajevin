import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AddOn implements Serializable {
    private static final long serialVersionUID = 1L;
    String id;
    String name;
    double price;
    Map<String, Double> recipe;  // ingredient_id -> amount

    AddOn(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.recipe = new HashMap<>();
    }
}