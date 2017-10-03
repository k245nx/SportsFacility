package components;

/**
 * La posizione di un posto a sedere
 * @author Roberto Tarullo, Pasquale Turi
 */
public class SeatPosition {
    /**
     * Crea una nuova posizione per un posto a sedere
     * @param row numero della fila
     * @param grandseat collocazione della tribuna
     * @param number numero del posto all'interno della fila
     */
    public SeatPosition(int row, int grandseat, int number) throws IllegalArgumentException{
        if(grandseat < 0 || grandseat > 3 || row < 0 || number < 0)
            throw new IllegalArgumentException("Illegal parameters for seat position");
        this.row = row;
        this.grandseat = grandseat;
        this.number = number;
    }
    
    /**
     * Restituisce il numero della fila del posto
     * @return il numero della fila
     */
    public int getRow(){
        return row;
    }
    
    /**
     * Restituisce la collocazione della tribuna
     * @return la collocazione della tribuna
     */
    public int getGrandstand(){
        return grandseat;
    }
    
    /**
     * Restituisce il numero del posto all'interno della fila
     * @return il numero del posto
     */
    public int getNumber(){
        return number;
    }
    
    @Override
    public String toString(){
        return row + "-" + grandseat + "-" + number;
    }
    
    @Override
    public boolean equals(Object other){
        return other instanceof SeatPosition && ((SeatPosition)other).getRow()==row && ((SeatPosition)other).getGrandstand()==grandseat && ((SeatPosition)other).getNumber()==number;
    }
    
    private final int row;
    private final int grandseat;
    private final int number;
    public static final int NORD = 0;
    public static final int EST = 1;
    public static final int SUD = 2;
    public static final int OVEST = 3;
}
