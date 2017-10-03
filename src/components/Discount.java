package components;

/**
 * Interfaccia per gli sconti della gestione sportiva
 * @author Roberto Tarullo, Pasquale Turi
 */
public interface Discount {
    Object getDiscounted();
    double getDiscount();
    void setDiscount(double newDiscount);
}
