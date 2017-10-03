package components;

/**
 * Uno stadio ha un nome, un numero identificativo e un determinato numero di posti a sedere
 * @author Roberto Tarullo, Pasquale Turi
 */
public class Stadium{
    /**
     * Crea uno stadio
     * @param stadiumName il nome
     * @param stadiumID identificativo univoco
     * @param stadiumCapacity il numero di posti a sedere
     * @throws NullPointerException se l'ggetto stadio punta ad un riferimento nullo
     * @throws IllegalArgumentException se la capacità dello stadio ha valore non valido
     */
    protected Stadium(String stadiumName, int stadiumID, int stadiumCapacity) throws NullPointerException, IllegalArgumentException{
        if(stadiumName==null)
            throw new NullPointerException("Stadium name can't be null");
        if(stadiumID < 0)
            throw new IllegalArgumentException("Stadium ID can't be negative");
        if(stadiumCapacity <=0)
            throw new IllegalArgumentException("Invalid value for stadium capacity");
	name = stadiumName;
	id = stadiumID;
        capacity = stadiumCapacity;
    }
    
    /**
     * Modifica la capacità dello stadio
     * @param n numero di posti a sedere
     * @throws IllegalArgumentException se la capacità è negativa
     */
    public void setCapacity(int n) throws IllegalArgumentException{
        if(n <= 0)
            throw new IllegalArgumentException("Invalid value for stadium capacity");
	capacity = n;
    }
    /**
     * Restituisce il nome dello stadio
     * @return il nome dello stadio
     */
    @Override
    public String toString(){
        return name;
    }
    
    /**
     * Restituisce l'id dello stadio
     * @return l'identificativo numerico
     */
    public int getID(){
    	return id;
    }
    
    /**
     * Restituisce il numero di posti a sedere totali dello stadio
     * @return capacità dello stadio
     */
    public int getCapacity(){
    	return capacity;
    }
    
    /**
     * Sovrascrittura del metodo equals() della classe Object
     * @param otherStadium stadio da confrontare
     * @return true se lo stadio da confrontare è uguale, false altrimenti
     */
    @Override
    public boolean equals(Object otherStadium){
        if(otherStadium instanceof Stadium && ((Stadium)otherStadium).getID() == id)
            return true;
        return false;
    }
    
    private String name;
    private int id;
    private int capacity;
}