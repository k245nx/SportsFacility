package components;

import java.util.NoSuchElementException;

/**
 * Un biglietto ha una partita ed un eventuale prezzo d'acquisto e può assumere due stati: acquistato o prenotato
 * Lo stato si può modificare da ordinato ad acquistato ma non viceversa
 * @author Roberto Tarullo, Pasquale Turi
 */
public class Ticket{
    /**
     * Costruisce un biglietto prenotato
     * @param match il riferimento alla partita il cui biglietto si è prenotato
     * @param position collocazione del posto prenotato
     * @param username nome utente del cliente che ha effettuato la prenotazione
     * @throws NullPointerException se il riferimento alla partita è nullo
     */
    protected Ticket(Match match, SeatPosition position, String username) throws NullPointerException, IllegalArgumentException{ 
        if(match==null)
            throw new NullPointerException("Match can't be null");
        int rows; // numero di file
        int drawnSeats = (31+23)*2;
        for(rows=1; drawnSeats < match.getStadium().getCapacity(); rows++)
            drawnSeats += (31+23)*2 + 8*rows;
        this.match = match;
        ordered = false; // setta lo stato
        seatPosition = position;
        customer = username;
    }
    
    /**
     * Costruisce un biglietto acquistato
     * @param match il riferimento alla partita il cui biglietto si è acquistato
     * @param orderPrice il prezzo migliore nel momento in cui si è acquistato il biglietto
     * @param position collocazione del posto acquistato
     * @param username nome utente del cliente che ha effettuato l'acquisto
     * @throws NullPointerException se il riferimento alla partita è nullo
     * @throws IllegalArgumentException se il prezzo d'acquisto ha valore negativo
     */
    public Ticket(Match match, double orderPrice, SeatPosition position, String username) throws NullPointerException, IllegalArgumentException{
        if(match==null)
            throw new NullPointerException("Match can't be null");
        if(orderPrice<0)
            throw new IllegalArgumentException("Order price can't be negative");
        this.match = match;
	ordered = true;
	this.orderPrice = orderPrice;
        seatPosition = position;
        customer = username;
    }
    
    /**
     * Setta il biglietto come acquistato e memorizza il prezzo d'acquisto
     * @param bestPrice prezzo migliore della partita nel momento in cui si è acquistato il biglietto
     */
    public void setOrdered(double bestPrice) throws IllegalArgumentException{
        if(bestPrice < 0)
            throw new IllegalArgumentException("Order price can't be negative");
        if(!ordered) // se il biglietto era prenotato setta semplicemente come acquistato
            ordered = true;
	else{ // se non era prenotato occupa un posto e setta come acquistato
            match.setOccupied(true);
            ordered = true;
	}
	orderPrice = bestPrice;
    }
    
    /**
     * Restituisce lo stato del biglietto
     * @return lo stato del biglietto, true se acquistato, false se prenotato
     */
    public boolean isOrdered(){ // true se ordinato, false se prenotato
	return ordered;
    }
    
    /**
     * Restituisce la partita del biglietto
     * @return il riferimento alla partita
     */
    public Match getMatch(){
	return match;
    }
    
    /**
     * Restituisce il posto in cui si è prenotato/acquistato il biglietto
     * @return la collocazione del posto in formato numeroFila_collocazioneTribuna_numeroPosto
     */
    public SeatPosition getPosition(){
	return seatPosition;
    }
    
    /**
     * Restituisce il prezzo a cui si è acquistato il biglietto
     * @return il prezzo d'acquisto
     */
    public double getOrderPrice(){
        if(!ordered)
            throw new NoSuchElementException("Can't retrieve order price if ticket is not ordered");
	return orderPrice;
    }
    
    /**
     * Restituisce il nome utente del cliente che ha prenotato o acquistato il biglietto
     * @return 
     */
    public String getCustomer(){
	return customer;
    }
    
    /**
     * Sovrascrittura del metodo equals() della classe Object
     * @param otherTicket biglietto da confrontare
     * @return true se il biglietto confrontato è uguale, false altrimenti
     */
    @Override
    public boolean equals(Object otherTicket){
        if(otherTicket instanceof Ticket && ((Ticket)otherTicket).getMatch() == match && ((Ticket)otherTicket).getCustomer().equalsIgnoreCase(customer) && ((Ticket)otherTicket).getPosition().equals(seatPosition))
            return true;
        return false;
    }
    
    private String customer;
    private Match match;
    private boolean ordered; // true = ordinato, false = prenotato
    private double orderPrice; // prezzo migliore nel momento in cui si è comprato il biglietto. il prezzo continua a variare in base agli sconti se il biglietto è prenotato
    private SeatPosition seatPosition; // collocazione del posto (formato: numeroFila_collocazioneTribuna_numeroPosto)
}
