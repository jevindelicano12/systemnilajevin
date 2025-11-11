import java.io.Serializable;

public class InventoryItem implements Serializable {
    private static final long serialVersionUID = 1L;
    String id;
    String name;
    String unit;
    double quantity;
    double reorderThreshold;

    InventoryItem(String id, String name, String unit, double quantity, double reorderThreshold) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.quantity = quantity;
        this.reorderThreshold = reorderThreshold;
    }
}