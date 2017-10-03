package components;

/**
 * Uno sconto da applicare su una singola partita
 * @author Roberto Tarullo, Pasquale Turi
 */
public class MatchDiscount implements Discount{
    /**
     * Crea uno sconto per una partita
     * @param discountedMatch la partita da scontare
     * @param discount la percentuale di sconto da applicare
     */
    protected MatchDiscount(Match discountedMatch, double discount){
        match = discountedMatch;
        this.discount = discount;
    }
    
    /**
     * Ritorna la partita soggetta a sconto
     * @return la partita scontata
     */
    public Match getMatch(){
        return match;
    }
    
    /**
     * Modifica la percentuale dello sconto
     * @param newDiscount nuova percentuale di sconto
     */
    public void setDiscount(double newDiscount){
        discount = newDiscount;
    }
    
    /**
     * Ritorna lo sconto attualmente applicato alla partita
     * @return lo sconto attuale
     */
    public double getDiscount(){
        return discount;
    }
    
    /**
     * Ritorna la partita soggetta a sconto 
     * @return la partita scontata come oggetto generico
     */
    public Object getDiscounted(){
        return match;
    }
    
    private final Match match;
    private double discount;
}
