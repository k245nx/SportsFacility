package components;

import components.exceptions.PostoIndisponibileException;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.NoSuchElementException;

/**
    Modalità cliente per la superclasse SportsFacility
    Qui l'utente può:
    1. visualizzare tutte le partite programmate in una determinata settimana o tutte le partite che si svolgono in un determinato stadio;
    2. visualizzare la lista delle partite non ancora iniziate consentendo la loro visualizzazione:
        a. in ordine cronologico,
        b. in ordine crescente rispetto all’identificativo di stadio,
        c. in ordine lessicografico crescente rispetto al nome delle squadre che si affrontano;
    3. accedere ai dettagli di una determinata partita e avere la possibilità di
        a. prenotare un biglietto nello stadio in cui sarà disputata;
        b. acquistare un biglietto per cui si è effettuata una prenotazione;
        c. acquistare un biglietto direttamente (senza averlo prenotato).
    4. visualizzare le prenotazioni effettuate;
    5. visualizzare gli acquisti effettuati;
    6. cancellare la prenotazione di una partita;
    
    @author Roberto Tarullo, Pasquale Turi
*/
public class SportsFacilityCustomer extends SportsFacility{
    /**
     * Costruisce la sottoclasse struttura sportiva per la modalità cliente
     * @param path Il percorso alla cartella in cui vengono salvati i cambiamenti
     * @param user Identificativo del cliente
     * @param customer Categoria a cui appartiene il cliente
     */
    public SportsFacilityCustomer(String path, String user, String customer){
        super(path); // carica da file stadi, partite, sconti e utenti registrati
        username = user;
        customerType = customer;
    }
    
    /**
     * Visualizza tutte le partite che si svolgono in una determina settimana
     * @param day Giorno della data dell'estremo sinistro (incluso) dell'intervallo di ricerca
     * @param month Mese della data dell'estremo sinistro dell'intervallo di ricerca
     * @param year Anno della data dell'estremo sinistro dell'intervallo di ricerca
     * @return lista delle partite che si svolgono nella settimana a partire dai sette giorni successivi alla data specificata
     */
    public ArrayList<Match> getWeekMatches(int day, int month, int year) throws IllegalArgumentException{
        if(day < 1 || day > 31 || month < 1 || month > 12)
            throw new IllegalArgumentException("Invalid format for given date");
        ArrayList<Match> nextWeekMatches = new ArrayList<>(); // Istanzia un ArrayList di tipo Match
	GregorianCalendar date = new GregorianCalendar(year, month-1, day); // month-1 perchè i mesi nella classe GregorianCalendar sono indicizzati da 0 a 11
	GregorianCalendar week = (GregorianCalendar) date.clone(); // Si copia la data data ricevuta in input
	week.add(Calendar.DATE, 7); // Dalla data ricevuta in input si aggiungono 7 giorni ricavando così l'intervallo destro di ricerca
	
	for(int i=0; i < matches.size(); i++){
            if(matches.get(i).getDate().compareTo(date) > 0 && matches.get(i).getDate().compareTo(week) < 0)
		nextWeekMatches.add(matches.get(i));
        }
        return nextWeekMatches;
    }
    
    /**
     * Visualizza tutte le partite che si svolgono in un determinato stadio
     * @param stadiumObject riferimento allo stadio
     * @return lista delle partite che si svolgono nello stadio specificato
     */
    public ArrayList<Match> getStadiumMatches(Stadium stadiumObject) throws NullPointerException{
        if(stadiumObject == null)
            throw new NullPointerException("Argument stadium object can't point to null");
	ArrayList<Match> stadiumMatches = new ArrayList<>(); // Istanzia un ArrayList di tipo Stadium
	
	for(int i=0; i < matches.size(); i++){ // Si prendono le partite che si svolgono nello stadio specificato
            if(stadiumObject.equals(matches.get(i).getStadium()))
                stadiumMatches.add(matches.get(i));
	}
	return stadiumMatches;
    }
    
    /**
     * Visualizza tutte le partite non ancora iniziate in ordine rispetto alla data in cui si svolgono
     * @return lista delle partite ordinate
     */
    public ArrayList<Match> getUpcomingMatches(){
        int i, j;
	Match tmp;
	GregorianCalendar now = new GregorianCalendar();
	ArrayList<Match> sorted = new ArrayList<>(); 
        
	for(i = 0; i < matches.size(); i++){
            if(matches.get(i).getDate().compareTo(now) > 0) // se la partita non è ancora iniziata
		sorted.add(matches.get(i));
	}	
	for(i=1; i < sorted.size(); i++){ // INSERTIONSORT
            tmp = sorted.get(i);
            for(j = i-1; (j >= 0) && (sorted.get(j).getDate().compareTo(tmp.getDate()) > 0); j--)
                sorted.set(j+1, sorted.get(j));
            sorted.set(j+1, tmp);
	}
	return sorted;
    }
    
    /**
     * Visualizza tutte le partite non ancora iniziate in ordine rispetto all'id dello stadio in cui si svolgono
     * @return lista delle partite ordinate
     */
    public ArrayList<Match> getStadiumSortedMatches() {
        int i, j;
	Match tmp;
        ArrayList<Match> sorted = getUpcomingMatches();
		
	for(i=1; i < sorted.size(); i++){ // INSERTIONSORT
            tmp = sorted.get(i);
            for(j = i-1; (j >= 0) && (sorted.get(j).getStadium().getID() > tmp.getStadium().getID()); j--)
                sorted.set(j+1, sorted.get(j));
            sorted.set(j+1, tmp);
        }	
	return sorted;
    }
    
    /**
     * Visualizza tutte le partite non ancora iniziate in ordine lessicografico
     * @return lista delle partite ordinate
     */
    public ArrayList<Match> getLexicographicallySortedMatches(){
        int i, j;
	Match tmp;
	ArrayList<Match> sorted = getUpcomingMatches();
		
	for(i=1; i < sorted.size(); i++){ // INSERTIONSORT
            tmp = sorted.get(i);
            for(j = i-1; (j >= 0) && (sorted.get(j).getHomeTeam().compareTo(tmp.getHomeTeam()) > 0); j--)
		sorted.set(j+1, sorted.get(j));
            sorted.set(j+1, tmp);
	}
	return sorted;
    }
    
    /**
     * Accede ai dettagli di una determinata partita sotto forma di stringa in linguaggio naturale
     * @param matchObject il riferimento alla partita di cui si vogliono conoscere i dettagli
     * @return dettagli della partita nel formato "data + ora + squadra in casa + squadra ospite + nome stadio + prezzo migliore + eventuale percentuale di sconto"
     */
    public String getMatchInfo(Match matchObject) throws NullPointerException{
        if(matchObject == null)
            throw new NullPointerException("Argument object can't point to null");
	return super.getMatchInfo(matchObject, getBestDiscount(matchObject));
    }
    
    /**
     * Calcola il prezzo migliore della partita per il cliente
     * @param matchObject la partita di cui si vuole conoscere il miglior prezzo
     * @return il prezzo migliore della partita
     */
    public double getBestPrice(Match matchObject) throws NullPointerException{
        if(matchObject == null)
            throw new NullPointerException("Argument object can't point to null");
        return super.getBestPrice(matchObject, getBestDiscount(matchObject));
    }
    
    /**
     * Ritorna la percentuale migliore tra gli sconti in vigore considerando la categoria del cliente che chiama il metodo
     * @param matchObject il riferimento alla partita per cui si vuole conoscere lo sconto migliore
     * @return la percentuale di sconto più alta della partita per il cliente
     */
    public double getBestDiscount(Match matchObject) throws NullPointerException{
        if(matchObject == null)
            throw new NullPointerException("Argument object can't point to null");
        boolean found = false;
        double min = super.getBestPartialDiscount(matchObject);
        
	for(int i=0; !found && i < customersDiscounts.size(); i++){ // scorri l'array dei clienti scontati
            if(((String)customersDiscounts.get(i).getDiscounted()).equalsIgnoreCase(customerType)){ // se l'utente che usa il programma fa parte della categoria clienti scontata
                found = true;
                if(customersDiscounts.get(i).getDiscount() > min)
                    min = customersDiscounts.get(i).getDiscount();
            }
        }
        return min;
    }
    
    /**
     * Prenota un biglietto per una partita
     * @param matchObject il riferimento alla partita per cui si vuole effettuare una prenotazione
     * @param position la posizione nello stadio del posto da prenotare
     * @throws PostoIndisponibileException se non c'è un posto disponibile o se la data di scadenza è trascorsa
     */
    public void preorder(Match matchObject, SeatPosition position) throws PostoIndisponibileException, NullPointerException, IllegalArgumentException{
	GregorianCalendar now = new GregorianCalendar();
	GregorianCalendar deadline = (GregorianCalendar) matchObject.getDate().clone();
	deadline.add(Calendar.HOUR, -12); // sottrai 12 ore alla data della partita per ottenere la data di scadenza per prenotare
        
        if(now.compareTo(deadline) > 0) // controlla se la partita è stata già svolta
            throw new PostoIndisponibileException("Match has already been taken place");
        if(matchObject.getAvailable() == 0) // nessun posto disponibile
            throw new PostoIndisponibileException("No free seats left");
        for(int i=0; i<tickets.size(); i++){ // controlla se il posto è occupato
            if(tickets.get(i).getMatch().equals(matchObject) && tickets.get(i).getPosition().equals(position))
                throw new PostoIndisponibileException("Seat has already been occupied");
        }
        tickets.add(new Ticket(matchObject, position, username)); // si tiene traccia del nuovo biglietto
        matchObject.setOccupied(true); // si occupa un posto della partita
        saveTickets(); // salva i cambiamenti sui biglietti
        saveMatches(); // salva i cambiamenti sulle partite
    }

    /**
     * Acquista un biglietto senza averlo prima prenotato
     * @param matchObject riferimento alla partita per cui si vuole acquistare un biglietto
     * @param position la posizione nello stadio del posto da acquistare
     * @throws PostoIndisponibileException se non c'è un posto libero o se la data di scadenza è trascorsa
     */
    public void order(Match matchObject, SeatPosition position) throws PostoIndisponibileException, NullPointerException, IllegalArgumentException{
        GregorianCalendar now = new GregorianCalendar();
	GregorianCalendar deadline = (GregorianCalendar) matchObject.getDate().clone();
	deadline.add(Calendar.HOUR, -12); // sottrai 12 ore alla data della partita per ottenere la data di scadenza per ordinare
        
        if(now.compareTo(deadline) > 0) // controlla se la partita si è già svolta
            throw new PostoIndisponibileException("Match has already been taken place");
        if(matchObject.getAvailable() == 0) // nessun posto disponibile
            throw new PostoIndisponibileException("No free seats left");
        for(int i=0; i<tickets.size(); i++){  // controlla se il posto è disponibile
            if(tickets.get(i).getMatch().equals(matchObject) && tickets.get(i).getPosition().equals(position))
                throw new PostoIndisponibileException("Seat has already been occupied");
        }
        tickets.add(new Ticket(matchObject, getBestPrice(matchObject, getBestDiscount(matchObject)), position, username));
        matchObject.setOccupied(true);
        saveTickets();
        saveMatches();
    }
    
    /**
     * Paga un biglietto per cui si e' effettuata la prenotazione
     * @param matchObject il riferimento alla partita prenotata che si vuole pagare
     * @param position il posto prenotato per cui si vuole acquistare un biglietto
     * @throws NoSuchElementException se il biglietto prenotato della partita non è stato trovato
     */
    public void pay(Match matchObject, SeatPosition position) throws NoSuchElementException{
        boolean found = false;
        for(int i=0; !found && i<tickets.size(); i++) {
            if(matchObject.equals(tickets.get(i).getMatch()) && tickets.get(i).getPosition().equals(position) && !tickets.get(i).isOrdered() && tickets.get(i).getCustomer().equalsIgnoreCase(username)){
                found = true;
                tickets.get(i).setOrdered(super.getBestPrice(tickets.get(i).getMatch(), getBestDiscount(tickets.get(i).getMatch())));
                saveTickets();
            }
        }
        if(!found)
            throw new NoSuchElementException("Preordered ticket for given match does not exists");
    }
    
    /**
     * Visualizza una lista dei biglietti per cui si è effettuata una prenotazione
     * @return Lista dei biglietti prenotati
     */
    public ArrayList<Ticket> getPreorders(){
	ArrayList<Ticket> preorderedTickets = new ArrayList<>();
	for(int i=0; i < tickets.size(); i++){
            if(!tickets.get(i).isOrdered() && tickets.get(i).getCustomer().equalsIgnoreCase(username))
                preorderedTickets.add(tickets.get(i));
        }
	return preorderedTickets;
    }
	
    /**
     * Visualizza una lista dei biglietti per cui si è effettuato un acquisto
     * @return Lista dei biglietti acquistati
     */
    public ArrayList<Ticket> getOrders(){
	ArrayList<Ticket> orderedTickets = new ArrayList<>();
	for(int i=0; i < tickets.size(); i++){
            if(tickets.get(i).isOrdered() && tickets.get(i).getCustomer().equalsIgnoreCase(username))
		orderedTickets.add(tickets.get(i));
	}
	return orderedTickets;
    }
	
    /**
     * Annulla la prenotazione di una partita
     * @param matchObject il riferimento alla partita di cui si è prenotato un biglietto
     * @param position la posizione dl posto di cui si vuole cancellare la prenotazione
     * @throws NoSuchElementException se il biglietto non è stato trovato o se non è stato prenotato
     */
    public void cancelPreorder(Match matchObject, SeatPosition position) throws NoSuchElementException{
        boolean found = false;
	for(int i=0; !found; i++) { // scorri tutti i biglietti
            // se il biglietto è quello cercato e se il biglietto è prenotato
            if(tickets.get(i).getMatch().equals(matchObject) && tickets.get(i).getPosition().equals(position) && !tickets.get(i).isOrdered() && tickets.get(i).getCustomer().equalsIgnoreCase(username)){ 
                found = true;
		tickets.get(i).getMatch().setOccupied(false); // libera posto
		tickets.remove(i); // rimuovi traccia del biglietto
		saveTickets();
		saveMatches();
            }
	}
	if(!found)
             throw new NoSuchElementException("Preordered ticket for selected match does not exists");
    }
    
    /**
     * Salva i cambiamenti alle partite sul file tickets_nome_cognome.dat
     */
    private void saveTickets(){
        try{
            PrintWriter out = new PrintWriter(path + "tickets_" + username + ".dat");
            out.println(username);
            out.println();
            for(int i=0; i < tickets.size(); i++){ // giorno + mese + anno + squadra1 + squadra2 + orderprice (se acquistato)
                if(tickets.get(i).getCustomer().equalsIgnoreCase(username)){
                    SeatPosition position = tickets.get(i).getPosition();
                    out.println(position.getRow());
                    out.println(position.getGrandstand());
                    out.println(position.getNumber());
                    out.println(tickets.get(i).getMatch().getDate().get(Calendar.DATE));
                    out.println(tickets.get(i).getMatch().getDate().get(Calendar.MONTH));
                    out.println(tickets.get(i).getMatch().getDate().get(Calendar.YEAR));
                    out.println(tickets.get(i).getMatch().getDate().get(Calendar.HOUR_OF_DAY));
                    out.println(tickets.get(i).getMatch().getDate().get(Calendar.MINUTE));
                    out.println(tickets.get(i).getMatch().getStadium().getID());
                    out.println(tickets.get(i).isOrdered());
                    if(tickets.get(i).isOrdered()) // se acquistato aggiungi il prezzo d'acquisto
                        out.println(tickets.get(i).getOrderPrice());
                    out.println();
                }
            }
            out.close();
        }
        catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }
    }
    
    private final String customerType; // categoria del cliente che sta usando il programma
    private final String username; // nome del cliente che sta usando il programma
}