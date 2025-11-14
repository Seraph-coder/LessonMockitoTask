package shopping;

import customer.Customer;
import product.Product;

import java.util.*;

/**
 * Корзина
 *
 * @author vpyzhyanov
 * @since 16.05.2023
 */
public class Cart {
    /**
     * Покупатель, которому принадлежит корзина
     */
    private final Customer customer;
    /**
     * Товар -> выбранное количество
     */
    private final Map<Product, Integer> products = new LinkedHashMap<>();

    // пакетный доступ сделан специально, т.к. корзиной должен управлять только этот пакет
    Cart(Customer customer) {
        this.customer = customer;
    }

    /**
     * Все товары в корзине
     */
    public Map<Product, Integer> getProducts() {
        return Collections.unmodifiableMap(products);
    }

    /**
     * Добавить товар в корзину
     */
    public void add(Product product, int count) throws IllegalArgumentException {
        validateCount(product, count);
        products.put(product, count);
    }

    /**
     * Изменить количество товара в корзине
     */
    public void edit(Product product, int count) throws IllegalArgumentException {
        if (products.containsKey(product)) {
            validateCount(product, count);
            products.put(product, count);
        }
    }

    /**
     * Проверить допустимо ли добавить такое количество товара в корзину
     * @throws IllegalArgumentException - если недопустимо
     */
    private static void validateCount(Product product, int count)
            throws IllegalArgumentException {
        if (product.getCount() - count <= 0) {
            throw new IllegalArgumentException(
                    "Невозможно добавить товар '%s' в корзину, т.к. нет необходимого количества товаров"
                            .formatted(product.getName()));
        }
    }

    /**
     * Удалить товар из корзины
     */
    public void remove(Product product) {
        products.remove(product);
    }
}
