package components;

/**
 * Sconto per mattina, pomeriggio o sera
 * @author Roberto Tarullo, Pasquale Turi
 */
public class DayOfWeekDiscount implements Discount{
    /**
     * Crea uno sconto per un giorno della settimana
     * @param day la fascia giornaliera da scontare
     * @param discount la percentuale di sconto da applicare
     */
    protected DayOfWeekDiscount(int day, double discount){
        this.day = day;
        this.discount = discount;
    }
    
    /**
     * Ritorna il giorno della settimana soggetto a sconto
     * @return il giorno della settimana scontato
     */
    public Object getDiscounted(){
        return day;
    }
    
    /**
     * Ritorna lo sconto attualmente applicato al giorno della settimana
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
    
    private final int day;
    private double discount;
    public static final int SUNDAY = 0;
    public static final int MONDAY = 1;
    public static final int TUESDAY = 2;
    public static final int WEDNESDAY = 3;
    public static final int THURSDAY = 4;
    public static final int FRIDAY = 5;
    public static final int SATURDAY = 6;
}
