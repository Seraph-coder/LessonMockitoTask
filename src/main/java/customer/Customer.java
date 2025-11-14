package customer;

/**
 * Покупатель
 *
 * @author vpyzhyanov
 * @since 16.05.2022
 */
public class Customer {
    /**
     * Идентификатор
     */
    private long id;
    /**
     * Телефон
     */
    private String phone;

    public Customer(long id, String phone) {
        this.id = id;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Customer customer = (Customer) o;

        return id == customer.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
