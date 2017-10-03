package components;

/**
 * Uno sconto da applicare sulle partite se visualizzate da cliente appartenente ad una data categoria
 * @author Roberto Tarullo, Pasquale Turi
 */
public class CustomerDiscount implements Discount{
    /**
     * Crea uno sconto per tipo cliente
     * @param customer la categoria cliente che si vuole scontare
     * @param discount la percentuale di sconto
     */
    protected CustomerDiscount(String customer, double discount){
        this.customer = customer;
        this.discount = discount;
    }
    
    /**
     * Ritorna la categoria cliente su cui è stato applicato lo sconto
     * @return la categoria cliente
     */
    public String getCustomer(){
        return customer;
    }
    
    /**
     * Modifica la percentuale dello sconto
     * @param newDiscount nuova percentuale di sconto
     */
    public void setDiscount(double newDiscount){
        discount = newDiscount;
    }
    
    /**
     * Ritorna lo sconto attualmente applicato alla categoria clienti
     * @return lo sconto attuale
     */
    public double getDiscount(){
        return discount;
    }
    
    
    /**
     * Ritorna la categoria cliente su cui è stato applicato lo sconto
     * @return la categoria cliente come oggetto generico
     */
    public Object getDiscounted(){
        return customer;
    }
    
    private final String customer;
    private double discount;
}
