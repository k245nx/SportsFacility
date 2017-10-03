package components;

/**
 * Uno sconto da applicare su un singolo stadio
 * @author Roberto Tarullo, Pasquale Turi
 */
public class StadiumDiscount implements Discount{
    /**
     * Crea uno sconto per uno stadio
     * @param discountedStadium lo stadio da scontare
     * @param discount la percentuale di sconto da applicare
     */
    protected StadiumDiscount(int discountedStadium, double discount){
        stadium = discountedStadium;
        this.discount = discount;
    }
    
    /**
     * Ritorna lo stadio soggetto a sconto
     * @return lo stadio scontato
     */
    public int getStadium(){
        return stadium;
    }
    
    /**
     * Modifica la percentuale dello sconto
     * @param newDiscount nuova percentuale di sconto
     */
    public void setDiscount(double newDiscount){
        discount = newDiscount;
    }
    
    /**
     * Ritorna lo sconto attualmente applicato allo stadio
     * @return lo sconto attuale
     */
    public double getDiscount(){
        return discount;
    }
    
    /**
     * Ritorna lo stadio soggetto a sconto
     * @return lo stadio scontato come oggetto generico
     */
    public Object getDiscounted(){
        return stadium;
    }
    
    private final int stadium;
    private double discount;
}
