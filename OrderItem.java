import java.io.Serializable;
import java.util.*;

public class OrderItem implements Serializable {
    private static final long serialVersionUID = 1L;
    Product product;
    int quantity;
    String sugarLevel;
    List<AddOn> addOns;
    Map<AddOn, Integer> addOnQuantities;
    double subtotal;

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.sugarLevel = "Standard";
        this.addOns = new ArrayList<>();
        this.addOnQuantities = new HashMap<>();
        recalculate();
    }

    public void addAddOn(AddOn addon, int qty) {
        if (!addOns.contains(addon)) {
            addOns.add(addon);
        }
        addOnQuantities.put(addon, addOnQuantities.getOrDefault(addon, 0) + qty);
        recalculate();
    }

    public List<AddOn> getAddOns() {
        return new ArrayList<>(addOns);
    }

    public int getAddOnQuantity(AddOn addon) {
        return addOnQuantities.getOrDefault(addon, 0);
    }

    public void setSugarLevel(String level) {
        this.sugarLevel = level;
    }

    public double getTotal() {
        return subtotal;
    }

    public void recalculate() {
        double addonsTotal = 0;
        for (AddOn addon : addOnQuantities.keySet()) {
            addonsTotal += addon.price * addOnQuantities.get(addon);
        }
        this.subtotal = (product.price * quantity) + addonsTotal;
    }
}