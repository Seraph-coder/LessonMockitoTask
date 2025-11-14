package shopping;

/**
 * Ошибка покупки
 * @author vpyzhyanov
 * @since 16.05.2023
 */
public class BuyException extends Exception {
    public BuyException(String message) {
        super(message);
    }
}
