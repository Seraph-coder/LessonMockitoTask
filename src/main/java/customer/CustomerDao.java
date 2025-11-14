package customer;

/**
 * Управляет взаимодействием с БД.
 *
 * @author vpyzhyanov
 * @since 16.05.2022
 */
public class CustomerDao {

    /**
     * Сохранить покупателя в БД
     * @return true - если покупатель был сохранён, false - иначе.
     */
    public boolean save(Customer customer) {
        // TODO взаимодействие с БД
        return false;
    }

    /**
     * Существует ли номер телефона в БД
     * @return true - если существует, false - иначе.
     */
    public boolean exists(String phone) {
        // TODO взаимодействие с БД
        return false;
    }

    /**
     * Удалить покупателя из БД
     */
    public void delete(Customer customer) {
        // TODO взаимодействие с БД
    }
}
