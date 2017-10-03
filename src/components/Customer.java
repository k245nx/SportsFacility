package components;

/**
 * Un cliente che ha un nome utente ed appartiene ad una categoria cliente
 * @author Roberto Tarullo, Pasquale Turi
 */
public class Customer {
    /**
     * Crea un nuovo cliente
     * @param username il nome utente del cliente
     * @param type la categoria al quale il cliente appartiene (es. Studente, Pensionato)
     */
    public Customer(String username, String type){
        this.username = username;
        this.type = type;
    }
    
    /**
     * Ritorna il nome utente del cliente
     * @return il nome utente del cliente
     */
    public String getUsername(){
        return username;
    }
    
    /**
     * Ritorna la categoria al quale il cliente appartiene
     * @return  la categoria del cliente
     */
    public String getType(){
        return type;
    }
    
    private final String username;
    private final String type;
}
