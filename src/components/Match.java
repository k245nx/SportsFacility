package components;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Una partita ha uno stadio, una data, due squadre, un prezzo e un determinato numero di posti liberi
 * Può essere inoltre clonata, si possono occupare posti e si può modificare il prezzo
 * @author Roberto Tarullo, Pasquale Turi
 */
public class Match{
    /**
    * Costruisce una partita
    * @param stadiumObject lo stadio in cui si svolge la partita
    * @param matchDate la data in cui si svolge la apartita
    * @param homeTeam la squadra che gioca in casa
    * @param guestTeam la squadra ospite
    * @param matchPrice il prezzo base della partita
    * @param seatsAvailable il numero di posti disponibili
    * @throws NullPointerException se uno degli argomenti ha riferimento nullo
    * @throws IllegalArgumentException se il prezzo o i posti disponibili assumono valore negativo
    */
    protected Match(Stadium stadiumObject, GregorianCalendar matchDate, String homeTeam, String guestTeam, double matchPrice, int seatsAvailable) throws NullPointerException, IllegalArgumentException{
        if(stadiumObject == null)
            throw new NullPointerException("Object stadium can't be null");
        if(homeTeam == null)
            throw new NullPointerException("Object home team can't be null");
        if(guestTeam == null)
            throw new NullPointerException("Object guest team can't be null");
        if(matchPrice < 0)
            throw new IllegalArgumentException("Price can't be negative");
        if(seatsAvailable < 0)
            throw new IllegalArgumentException("Available seats can't be negative");
        stadium = stadiumObject;
        date = matchDate;
        home = homeTeam;
        guest = guestTeam;
        price = matchPrice;
        available = seatsAvailable;
    }
	
    /**
    * Ritorna la data come stringa
    * @return la data in formato "DD/MM/YY"
    */
    public String getDateAsString(){
	String dateAsString ="";
	
	if(date.get(Calendar.DATE) < 10) // se il giorno ha una cifra rappresentalo con uno zero davanti
            dateAsString += "0" + date.get(Calendar.DATE);
	else // se ha due cifre stampalo com'è
            dateAsString += (date.get(Calendar.DATE));
	if(date.get(Calendar.MONTH) < 9) // se il mese ha una cifra rappresentalo con uno zero davanti
            dateAsString += "/0" + (date.get(Calendar.MONTH)+1);
	else // se il mese ha due cifre
            dateAsString += "/" + (date.get(Calendar.MONTH)+1);
	dateAsString += "/" + date.get(Calendar.YEAR);
	
	return dateAsString;
    }
	
    /**
    * Ritorna l'ora come stringa
    * @return l'ora in formato "HH:MM"
    */
    public String getTimeAsString(){
        String timeAsString = "";
	if(date.get(Calendar.MINUTE) < 10)
            timeAsString += date.get(Calendar.HOUR_OF_DAY) + ":" + "0" + date.get(Calendar.MINUTE); // aggiungi uno zero se il minuto ha una sola cifra
	else
            timeAsString += date.get(Calendar.HOUR_OF_DAY) + ":" + date.get(Calendar.MINUTE);
	return timeAsString;
    }
	
    /**
    * Occupa o libera un posto
    * @param occupy true se il posto è da occupare, false se da liberare
    */
    public void setOccupied(boolean occupy){ // true per occupare, false per liberare
        if(occupy)
            available--;
        else
            available++;
    }
	
    /**
    * Modifica il prezzo della partita
    * @param sum il nuovo prezzo da impostare
    * @throws IllegalArgumentException se il prezzo ha valore negativo
    */
    public void setPrice(double sum) throws IllegalArgumentException{
        if(sum < 0)
            throw new IllegalArgumentException("Price can not be negative");
        price = sum;
    }
	
    /**
    * Restituisce lo stadio in cui si svolge la partita
    * @return l'oggetto stadio
    */
    public Stadium getStadium(){
        return stadium;
    }
	
    /**
    * Restituisce la data in cui si svolge la partita
    * @return l'oggetto data
    */
    public GregorianCalendar getDate(){
        return date;
    }
	
    /**
    * Restituisce la squadra che gioca in casa
    * @return il nome della squadra
    */
    public String getHomeTeam(){
        return home;
    }
	
    /**
    * Restituisce la squadra che gioca come ospite
    * @return il nome della squadra
    */
    public String getGuestTeam(){
        return guest;
    }
	
    /**
    * Restituisce il prezzo base della partita
    * @return valore del prezzo
    */
    public double getPrice(){
        return price; // ritorna il prezzo base
    }
	
    /**
    * Restituisce il numero di posti liberi
    * @return il numero di posti
    */
    public int getAvailable(){
        return available;
    }
    
    /**
     * Sovrascrittura del metodo equals() della classe Object
     * @param otherMatch match da confrontare
     * @return true se il match da confrontare è uguale, false altrimenti
     */
    @Override
    public boolean equals(Object otherMatch){
        if(otherMatch instanceof Match && ((Match)otherMatch).getStadium().equals(stadium) && ((Match)otherMatch).getDate().equals(date))
            return true;
        return false;
    }
    
    /**
     * Sovrascrittura del metodo toString()
     * @return una breve descrizione della partita
     */
    @Override
    public String toString(){
        String matchAsString = home + " - " + guest + " @" + stadium.toString() + " on " + getDateAsString() + " " + getTimeAsString();
        return matchAsString;
    }
	
    private final Stadium stadium; // stadio in cui si svolge la partita
    private final GregorianCalendar date; // data in cui si svolge la partita
    private final String home; // squadra in casa
    private final String guest; // squadra ospite
    private double price; // prezzo della partita
    private int available; // posti liberi
}
