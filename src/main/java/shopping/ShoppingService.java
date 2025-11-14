package shopping;

import customer.Customer;
import product.Product;

import java.util.List;

/**
 * Сервис покупок
 *
 * @author vpyzhyanov
 * @since 16.05.2023
 */
public interface ShoppingService {

    /**
     * Получить корзину покупателя
     */
    Cart getCart(Customer customer);

    /**
     * Получить все товары
     */
    List<Product> getAllProducts();

    /**
     * Найти товар по имени (полное совпадение)
     */
    Product getProductByName(String name);

    /**
     * Совершить покупку (уменьшает количество доступных товаров и очищает корзину)
     * @param cart корзина
     * @return true - если покупка удалась, false - если что-то пошло не так
     * @throws BuyException - при ошибке покупки
     */
    boolean buy(Cart cart) throws BuyException;
}
