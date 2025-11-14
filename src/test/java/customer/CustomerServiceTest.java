package customer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

/**
 * <p>Тестирование класса {@link CustomerService}</p>
 * <h3>Способы определения mock объектов для сервиса CustomerService</h3>
 * <h4>1 способ</h4>
 * <b>Через метод {@link Mockito#mock(Class)}</b>
 * <pre class="code"><code class="java">
 *     private CustomerDao customerDaoMock = Mockito.mock(CustomerDao.class);
 *     private CustomerService customerService
 *              = new CustomerService(customerDaoMock); // Обычный конструктор
 * </code></pre>
 *
 * <h4>2 способ</h4>
 * <p><b>Через аннотации &#064;{@link Mock} и &#064;{@link InjectMocks}</b></p>
 * Чтобы этот способ работал, нужно на класс навесить аннотацию <code class="java">@ExtendWith(MockitoExtension.class)</code>
 * <pre class="code"><code class="java">
 *     &#064;Mock
 *     private CustomerDao customerDaoMock;
 *     &#064;InjectMocks
 *     private CustomerService customerService;
 *     // Все зависимые Mock объекты подставятся автоматически
 * </code></pre>
 * <h4>3 способ</h4>
 * <p><b>Через аннотации &#064;{@link Mock} и конструктор (Inject моков через конструктор)</b></p>
 * Чтобы этот способ работал, нужно на класс навесить аннотацию <code class="java">@ExtendWith(MockitoExtension.class)</code>
 * <pre class="code"><code class="java">
 *     private final CustomerDao customerDaoMock; // Обратите внимание, что аннотация тут не нужна
 *     private final CustomerService customerService;
 * <p/>
 *     public CustomerServiceTest(&#064;Mock CustomerDao customerDaoMock) {
 *         this.customerDaoMock = customerDaoMock;
 *         customerService = new CustomerService(customerDaoMock);
 *     }
 * </code></pre>
 *
 * @author Пыжьянов Вячеслав
 */
@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    // Тут применён 3 способ определения моков
    private final CustomerDao customerDaoMock; // Обратите внимание, что аннотация тут не нужна
    private final CustomerService customerService;

    public CustomerServiceTest(@Mock CustomerDao customerDaoMock) {
        this.customerDaoMock = customerDaoMock;
        customerService = new CustomerService(customerDaoMock);
    }

    /**
     * Тестирование добавления покупателя
     */
    @Test
    public void testAddCustomer() throws Exception {
        // Подготовка
        Customer customer = new Customer(0, "11-11-11");

        Mockito.when(customerDaoMock.save(Mockito.eq(customer)))
                .thenReturn(Boolean.TRUE);

        // Действия и проверки
        Assertions.assertTrue(customerService.addCustomer(customer));

        Mockito.verify(customerDaoMock, Mockito.times(1))
                .exists(Mockito.eq("11-11-11"));
        // Лишняя проверка: проверяем, что других вызовов мока не было.
        Mockito.verify(customerDaoMock, Mockito.never())
                .delete(Mockito.any(Customer.class));
    }

    /**
     * Тестирование отсутствия сохранения при добавлении покупателя с таким же телефоном
     */
    @Test
    public void testNotSaveCustomerWithSamePhone() throws Exception {
        // Подготовка
        Mockito.when(customerDaoMock.exists(Mockito.any(String.class)))
                .thenReturn(Boolean.TRUE);

        Customer customer = new Customer(0, "11-11-11");

        // Действия и проверки
        Assertions.assertFalse(customerService.addCustomer(customer));
    }

    /**
     * Тестирование корректной обработки ошибки, возникшей в БД.
     * <p>Показательный пример: Кинуть исключение из mock объекта
     * и проверить, что оно обработано в нашем сервисе</p>
     */
    @Test
    public void testAddCustomerThrowsException() {
        // Подготовка
        Mockito.when(customerDaoMock.save(Mockito.any(Customer.class)))
                .thenThrow(RuntimeException.class);

        // Действия и проверки
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            Customer customer = new Customer(0, "11-11-11");
            customerService.addCustomer(customer);
        });
        // Проверка сообщения об ошибке (что это человекочитаемая ошибка)
        Assertions.assertEquals("Не удалось добавить покупателя",
                exception.getMessage());
    }

    /**
     * Показательный пример: более детальная проверка аргумента вызываемой функции.
     * <p>Можем научиться отличать объекты, которые равны по equals</p>
     */
    @Test
    public void testArgThatExample() throws Exception {
        // Подготовка
        // Создаём покупателей, у которых одинаковые идентификаторы, т.е. они равны по equals
        Customer customer1 = new Customer(1, "11-11-11");
        Customer customer2 = new Customer(1, "22-22-22");

        // Действия и проверки
        customerService.addCustomer(customer1);

        // Задача: проверить что вызвался метод сохранения именно для customer1, но не для customer2
        // Такая проверка пройдёт (но этого нам не надо):
        Mockito.verify(customerDaoMock).save(Mockito.eq(customer2));

        /*
         * Проверка проходит из-за реализации метода equals, но изменить её мы не можем,
         * т.к не тесты определяют функциональность, а функциональность определяет тесты.
         * Чтобы эту ситуацию обойти, мы должны сравнить покупателей именно по номеру телефонов.
         * Сделать это можно так:
         */
        Mockito.verify(customerDaoMock, Mockito.times(1))
                .save(Mockito.argThat(customer ->
                        customer.getPhone().equals(customer1.getPhone())));
        Mockito.verify(customerDaoMock, Mockito.never())
                .save(Mockito.argThat(customer ->
                        customer.getPhone().equals(customer2.getPhone())));
        // Теперь мы уверены, что метод save вызвался именно с нужным покупателем
    }

    /**
     * Показательный пример: использование класса Answer, для установки id.
     * <p>Эмулируем поведение базы данных, а именно генерацию идентификатора</p>
     */
    @Test
    public void testAddCustomerWithId() throws Exception {
        // Подготовка
        /* Использование Answer для установки идентификатора клиента,
         * который передается в качестве параметра в mock метод.
         */
        Mockito.when(customerDaoMock.save(Mockito.any(Customer.class)))
                .thenAnswer((Answer<Boolean>) invocation -> {
                    Object[] arguments = invocation.getArguments();
                    if (arguments != null && arguments.length > 0 && arguments[0] != null) {
                        Customer customer = (Customer) arguments[0];
                        customer.setId(1);
                        return Boolean.TRUE;
                    }
                    return Boolean.FALSE;
                });

        Customer customer = new Customer(0, "11-11-11");

        // Действия и проверки
        Assertions.assertTrue(customerService.addCustomer(customer));
        Assertions.assertTrue(customer.getId() > 0);
    }
}