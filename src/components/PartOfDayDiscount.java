package components;

/**
 * Sconto per mattina, pomeriggio o sera
 * @author Roberto Tarullo, Pasquale Turi
 */
public class PartOfDayDiscount implements Discount{
    /**
     * Crea uno sconto per una fascia giornaliera
     * @param partOfDay la fascia giornaliera da scontare
     * @param discount la percentuale di sconto da applicare
     */
    protected PartOfDayDiscount(int partOfDay, double discount){
        this.partOfDay = partOfDay;
        this.discount = discount;
    }
    
    /**
     * Ritorna la fascia giornaliera soggetta a sconto
     * @return 
     */
    public Object getDiscounted(){
        return partOfDay;
    }
    
    /**
     * Ritorna lo sconto attualmente applicato alla fascia giornaliera
     * @return lo sconto attuale
     */
    public double getDiscount(){
        return discount;
    }
    
    /**
     * Modifica la percentuale dello sconto
     * @param newDiscount nuova percentuale di sconto
     */
    public void setDiscount(double newDiscount){
        discount = newDiscount;
    }
    
    private final int partOfDay;
    private double discount;
    public static final int MORNING = 1;
    public static final int AFTERNOON = 2;
    public static final int EVENING = 3;
}
