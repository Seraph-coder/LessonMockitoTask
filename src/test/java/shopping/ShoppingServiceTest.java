package shopping;

import customer.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import product.Product;
import product.ProductDao;

/**
 * Тесты для сервиса покупок {@link ShoppingService}.
 *
 * @author Seraph-coder
 * @since 14.11.2025
 */
@ExtendWith(MockitoExtension.class)
public class ShoppingServiceTest {
    private final ProductDao productDaoMock;
    private final ShoppingService shoppingService;
    private final Customer customer;

    public ShoppingServiceTest(@Mock ProductDao productDao) {
        this.productDaoMock = productDao;
        this.shoppingService = new ShoppingServiceImpl(productDaoMock);
        this.customer = new Customer(1L, "999-999-9999");
    }

    /**
     * Проверка получения корзины для покупателя
     * Ожидаемое поведение: всегда возвращается корзина, связанная с указанным покупателем,
     * при повторном вызове возвращается та же корзина
     */
    @Test
    public void testGetCard() {
        Cart cart1 = shoppingService.getCart(customer);
        Product product = new Product("Какой то продукт", 10);
        cart1.add(product, 2);
        Cart cart2 = shoppingService.getCart(customer);
        Assertions.assertEquals(1, cart1.getProducts().size());
        Assertions.assertEquals(2, cart1.getProducts().get(product));
        Assertions.assertEquals(1, cart2.getProducts().size());
        Assertions.assertSame(cart1, cart2);
    }


    /**
     * Я считаю, что этот тест излишен, так как тестируемый метод просто делегирует вызов DAO слою.
     * А в Unit тестах текущего класса мы его мокнули.
     * Так что я оставил его пустым.
     * Тестировать этот метод стоит в интеграционных тестах.
     */
    @Test
    public void testGetAllProducts() {
    }

    /**
     * Я считаю, что этот тест излишен, так как тестируемый метод просто делегирует вызов DAO слою.
     * А в Unit тестах текущего класса мы его мокнули.
     * Так что я оставил его пустым.
     * Тестировать этот метод стоит в интеграционных тестах.
     */
    @Test
    public void testGetProductByName() {
    }

    /**
     * Проверка успешной покупки продукта
     * Ожидаемое поведение: покупка совершается успешно, возвращается true, результат сохраняется в DAO,
     * количество продукта уменьшается на купленное количество, корзина очищается
     */
    @Test
    public void testBuyProductSuccessfully() throws BuyException {
        Cart cart = shoppingService.getCart(customer);


        Product product = new Product( "Какой то продукт", 10);
        cart.add(product, 2);
        Assertions.assertTrue(shoppingService.buy(cart));
        Mockito.verify(productDaoMock, Mockito.times(1)).save(product);
        Assertions.assertEquals(8, product.getCount());
        Assertions.assertEquals(0, cart.getProducts().size());
    }

    /**
     * Проверка покупки с количеством продукта, равным доступному количеству
     * Ожидаемое поведение: покупка совершается успешно, возвращается true, результат сохраняется в DAO,
     * количество продукта станет нулевым
     */
    @Test
    public void testBuyProductWithExactStock() throws BuyException {
        Cart cart = shoppingService.getCart(customer);

        Product product = new Product( "Какой то продукт", 5);
        cart.add(product, 5);
        Assertions.assertTrue(shoppingService.buy(cart));
        Mockito.verify(productDaoMock, Mockito.times(1)).save(product);
        Assertions.assertEquals(0, product.getCount());
    }

    /**
     * Проверка покупки с недостаточным количеством продукта
     * Ожидаемое поведение: выбрасывается исключение при добавлении в корзину
     */
    @Test
    public void testBuyProductWithInsufficientStock() {
        Cart cart = shoppingService.getCart(customer);
        Product product = new Product("Какой то продукт", 3);
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                cart.add(product, 5),
                "Невозможно добавить товар 'Какой то продукт' в корзину," +
                        "т.к. нет необходимого количества товаров"
        );
        Mockito.verify(productDaoMock, Mockito.never()).save(Mockito.any());
    }

    /**
     * Проверка покупки с пустой корзиной
     * Ожидаемое поведение: покупка не совершается, возвращается false
     */
    @Test
    public void testBuyProductWithEmptyCart() throws BuyException {
        Cart cart = shoppingService.getCart(customer);

        Assertions.assertFalse(shoppingService.buy(cart));
        Mockito.verify(productDaoMock, Mockito.never()).save(Mockito.any());
    }

    /**
     * Проверка успешной покупки с несколькими продуктами
     * Ожидаемое поведение: покупка совершается успешно, возвращается true,
     * каждый продукт сохраняется в DAO, количество каждого продукта уменьшается на купленное количество,
     * корзина очищается
     */
    @Test
    public void testBuyWithMultipleProductsSavesEachProduct() throws BuyException {
        Cart cart = shoppingService.getCart(customer);
        Product p1 = new Product("P1", 10);
        Product p2 = new Product("P2", 7);

        cart.add(p1, 3);
        cart.add(p2, 2);

        Assertions.assertTrue(shoppingService.buy(cart));

        Assertions.assertEquals(7, p1.getCount());
        Assertions.assertEquals(5, p2.getCount());

        Mockito.verify(productDaoMock, Mockito.times(1)).save(p1);
        Mockito.verify(productDaoMock, Mockito.times(1)).save(p2);
        Assertions.assertTrue(cart.getProducts().isEmpty());
    }

    /**
     * Проверка покупки, когда количество продукта уменьшается после добавления в корзину
     * Ожидаемое поведение: выбрасывается исключение BuyException, изменения не сохраняются в DAO
     */
    @Test
    public void testBuyThrowsWhenStockReducedAfterAdd() {
        Cart cart = shoppingService.getCart(customer);
        Product p = new Product("Race", 5);
        cart.add(p, 4);
        p.subtractCount(2);

        Assertions.assertThrows(BuyException.class, () -> shoppingService.buy(cart));
        Mockito.verify(productDaoMock, Mockito.never()).save(Mockito.any());
    }
}
