import java.util.ArrayList;
import java.util.List;

public class Cart {
    public static List<Product> items = new ArrayList<>();

    public static void addItem(Product item) {
        items.add(item);
    }

    public static List<Product> getItems() {
        return items;
    }

    public static void clearCart() {
        items.clear();
    }
}
