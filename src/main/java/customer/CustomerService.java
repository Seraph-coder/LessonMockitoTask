package customer;

/**
 * Сервис для работы с покупателями
 * @author vpyzhyanov
 * @since 16.05.2022
 */
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    /**
     * Добавить покупателя.<br>
     * Не добавляет если уже есть покупатель с таким же номером телефона.
     * @return true - если покупатель был добавлен, false - иначе.
     * @throws Exception в случае ошибки в БД
     */
    public boolean addCustomer(Customer customer) throws Exception {
        try {
            if(customerDao.exists(customer.getPhone())){
                return false;
            }
            return customerDao.save(customer);
        } catch (Exception e) {
            throw new Exception("Не удалось добавить покупателя", e);
        }
    }
}
