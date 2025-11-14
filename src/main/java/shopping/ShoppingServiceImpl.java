package shopping;

import customer.Customer;
import product.Product;
import product.ProductDao;

import java.util.List;
import java.util.Map;

/**
 * Реализация {@link ShoppingService}
 *
 * @author vpyzhyanov
 * @since 16.05.2023
 */
public class ShoppingServiceImpl implements ShoppingService {

    private final ProductDao productDAO;

    public ShoppingServiceImpl(ProductDao productDAO) {
        this.productDAO = productDAO;
    }

    @Override
    public Cart getCart(Customer customer) {
        return new Cart(customer);
    }

    @Override
    public List<Product> getAllProducts() {
        return productDAO.getAll();
    }

    @Override
    public Product getProductByName(String name) {
        return productDAO.getByName(name);
    }

    @Override
    public boolean buy(Cart cart) throws BuyException {
        if (cart.getProducts().isEmpty()) {
            return false;
        }
        validateCanBuy(cart);
        cart.getProducts().forEach((product, count) -> {
            product.subtractCount(count);
            productDAO.save(product);
        });
        return true;
    }

    /**
     * Проверить, что покупка может быть совершена
     * @throws BuyException - если покупка не может быть совершена
     */
    private static void validateCanBuy(Cart cart) throws BuyException {
        for (Map.Entry<Product, Integer> entry : cart.getProducts().entrySet()) {
            Product product = entry.getKey();
            Integer count = entry.getValue();
            if (product.getCount() < count) {
                throw new BuyException("В наличии нет необходимого количества товара '%s'"
                        .formatted(product.getName()));
            }
        }
    }
}
