package components;

import components.exceptions.ScontoMiglioreException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.NoSuchElementException;
/** 
    Modalità gestore per la superclasse SportsFacility
    Qui l'utente può:
        1. inserire partite di calcio nel calendario delle partite programmate;
        2. visualizzare le partite in base alla capienza degli stadi o in ordine cronologico;
        3. attivare politiche di sconto sui biglietti;
        4. assegnare un prezzo alle partite che si svolgono in un determinato stadio (il costo sarà lo stesso per tutti i settori);
        5. aumentare o ridurre la capienza degli stadi;
        6. visualizzare l’incasso totale e per ogni stadio.
    
    @author Roberto Tarullo, Pasquale Turi
 */
public class SportsFacilityManager extends SportsFacility{
    /**
     * Costruisce la sottoclasse struttura sportiva per la modalità gestore
     * @param path Il percorso alla cartella in cui vengono salvati i cambiamenti
     */
    public SportsFacilityManager(String path){ // correggere eccezioni
        super(path); // carica da file stadi, partite, sconti e utenti registrati
    }
    
    /**
     * Accede ai dettagli di una determinata partita sotto forma di stringa in linguaggio naturale
     * @param matchObject il riferimento alla partita di cui si vogliono conoscere i dettagli
     * @return dettagli della partita nel formato "data + ora + squadra in casa + squadra ospite + nome stadio + prezzo migliore + eventuale percentuale di sconto"
     */
    public String getMatchInfo(Match matchObject) throws NullPointerException{
        if(matchObject == null)
            throw new NullPointerException("Argument object can't point to null");
	return super.getMatchInfo(matchObject, 0);
    }
    
    /**
     * Inserisce una partita di calcio nel calendario delle partite programmate
     * @param stadiumObject Il riferimento all'oggetto stadio in cui si svolge la partita da aggiungere
     * @param day Il giorno in cui si svolge la partita
     * @param month Il mese in cui si svolge la partita
     * @param year L'anno in cui si svolge la partita
     * @param hour L'ora in cui si svolge la partita
     * @param minute Il minuto in cui si svolge la partita
     * @param home La squadra che gioca in casa
     * @param guest La squadra che gioca come ospite
     * @param price Il prezzo base della partita
     * @throws IllegalArgumentException Se esiste un'altra partita che si svolge nel dato stadio alla data ora o se si immette una data non valida
     */
    public void addMatch(Stadium stadiumObject, int day, int month, int year, int hour, int minute, String home, String guest, double price) throws IllegalArgumentException, NullPointerException, NoSuchElementException{
        GregorianCalendar newMatchDate = new GregorianCalendar(year, month-1, day, hour, minute);
	if(day < 1 || day > 31 || month < 1 || month > 12 || hour < 0 || hour > 23 || minute < 0 || minute > 59 || price < 0 || newMatchDate.compareTo(new GregorianCalendar()) < 0)
            throw new IllegalArgumentException("Invalid date or time");
        
        for(int i=0; i<matches.size(); i++){
            if(matches.get(i).getDate().equals(newMatchDate) && matches.get(i).getStadium().equals(stadiumObject))
               throw new IllegalArgumentException("Another match takes place in given stadium, date and time");
        }
        matches.add(new Match(stadiumObject, newMatchDate, home, guest, price, stadiumObject.getCapacity()));
        saveMatches();
    }
    
    /**
     * Aggiunge un nuovo stadio alla gestione sportiva
     * @param name il nome dello stadio
     * @param capacity la capacità dello stadio
     * @throws IllegalArgumentException se si inserisce un valore non valido per la capacità
     */
    public void addStadium(String name, int capacity) throws IllegalArgumentException{
        stadiums.add(new Stadium(name, stadiums.size()+1, capacity));
        saveStadiums();
    }
	
    /**
     * Visualizza tutte le partite in base alla capienza degli stadi
     * @return la lista delle partite ordinate
     */
    public ArrayList<Match> getCapacitySortedMatches(){
        ArrayList<Match> sorted = new ArrayList<>();
	for(int i=0; i < matches.size(); i++)
            sorted.add(matches.get(i));
	
	int i, j; 
	Match tmp;
	for(i=1; i < sorted.size(); i++){ // INSERTIONSORT
            tmp = sorted.get(i);
            for(j = i-1; (j >= 0) && (sorted.get(j).getStadium().getCapacity() > tmp.getStadium().getCapacity()); j--)
                sorted.set(j+1, sorted.get(j));
            sorted.set(j+1, tmp);
	}
	return sorted;
    }
    
    /**
     * Visualizza tutte le partite in ordine cronologico
     * @return la lista delle partite ordinate
     */
    public ArrayList<Match> getDateSortedMatches(){
    	ArrayList<Match> sorted = new ArrayList<>();
	for(int i=0; i < matches.size(); i++)
            sorted.add(matches.get(i));
	
	int i, j;
	Match tmp;
	for(i=1; i < sorted.size(); i++){ // INSERTIONSORT
            tmp = sorted.get(i);
            for(j = i-1; (j >= 0) && (sorted.get(j).getDate().compareTo(tmp.getDate()) > 0); j--)
		sorted.set(j+1, sorted.get(j));
            sorted.set(j+1, tmp);
	}
	return sorted;
    }
	
    /**
     * Attiva politiche di sconto per una singola partita
     * @param matchObject il riferimento alla partita da scontare
     * @param p la percentuale di sconto da applicare
     * @throws ScontoMiglioreException se esiste già uno sconto migliore di quello specificato
     * @throws IllegalArgumentException se la percentuale di sconto è un valore negativo
     */
    public void enableDiscount(Match matchObject, double p) throws ScontoMiglioreException, IllegalArgumentException{
        if(p < 0)
            throw new IllegalArgumentException("Discount percentage can not be negative");
        boolean discounted = false;
        for(int i=0; !discounted && i<matchesDiscounts.size(); i++){ // scorri l'array degli stadi scontati, se size()==0 salta il for
            if(matchObject.equals(matchesDiscounts.get(i).getDiscounted())){ // se la partita è già stata scontata in precedenza
		if(p > matchesDiscounts.get(i).getDiscount()){ // se il nuovo sconto è migliore
                    matchesDiscounts.get(i).setDiscount(p); // aggiorna percentuale sconto
                    discounted = true;
		}
		else
                    throw new ScontoMiglioreException();
            }
	}
        if(!discounted) // se non ci sono sconti su singole partite in vigore o se la partita non è stata scontata
            matchesDiscounts.add(new MatchDiscount(matchObject, p)); // memorizza la partita
        saveDiscounts();
    }
	
    /**
     * Attiva politiche di sconto per tutte le partite di uno stadio
     * @param stadiumObject il riferimento allo stadio le cui partite si vogliono scontare
     * @param p la percentuale di sconto da applicare
     * @throws ScontoMiglioreException se esiste già uno sconto migliore di quello specificato
     * @throws IllegalArgumentException se la percentuale di sconto è un valore negativo
     */
    public void enableDiscount(Stadium stadiumObject, double p) throws ScontoMiglioreException, IllegalArgumentException{
        if(p < 0)
            throw new IllegalArgumentException("Discount percentage can not be negative");
        boolean discounted = false;
	for(int i=0; !discounted && i<stadiumsDiscounts.size(); i++){ // scorri l'array delle partite scontate, se size()==0 salta il for
            if(stadiumObject.equals(stadiumsDiscounts.get(i).getDiscounted())){ // se lo stadio è già stata scontato in precedenza
                if(p > stadiumsDiscounts.get(i).getDiscount()){ // se il nuovo sconto è migliore
                    stadiumsDiscounts.get(i).setDiscount(p); // aggiorna percentuale sconto
                    discounted = true;
		}
		else
                    throw new ScontoMiglioreException();
            }
	}
	if(!discounted) // se non ci sono in vigore sconti su stadi o se lo stadio non ha uno sconto
            stadiumsDiscounts.add(new StadiumDiscount(stadiumObject.getID(), p)); // memorizza la partita
	saveDiscounts();
    }

    /**
     * Attiva politiche di sconto per una fascia giornaliera tra mattina (6-12), pomeriggio (12-18) e sera (18-00)
     * @param i Può assumere valore compreso tra 0 e 2, rappresenta la fascia giornaliera da scontare (1 = mattina, 2 = pomeriggio, 3 = sera)
     * @param p la percentuale di sconto da applicare
     * @throws ScontoMiglioreException se esiste già uno sconto migliore di quello specificato
     * @throws IllegalArgumentException se la percentuale di sconto è un valore negativo
     * @throws ArrayIndexOutOfBoundsException se il primo argomento ha valore diverso da 1, 2 o 3
     */
    public void enablePartOfTheDayDiscount(int i, double p) throws ScontoMiglioreException, IllegalArgumentException, ArrayIndexOutOfBoundsException{
        if(p < 0)
            throw new IllegalArgumentException("Discount percentage can not be negative");
        if(i<1 || i>3)
            throw new ArrayIndexOutOfBoundsException("First argument can assume only integers values between 1, 2 or 3");
        if(p > partOfDayDiscounts[i-1].getDiscount()){ // se lo sconto è migliore di quello precedente
            partOfDayDiscounts[i-1].setDiscount(p); // aggiorna percentuale con la nuova
            saveDiscounts();
        }
        else
            throw new ScontoMiglioreException();
    }
        
    /**
     * Attiva politiche di sconto per tutte le partite che si svolgono in uno o più giorni della settimana
     * @param i Può assumere valore compreso tra 0 e 6, rappresenta il giorno della settimana da scontare (0 = domenica, 1 = lunedì, ... , 6 = sabato)
     * @param p la percentuale di sconto da applicare
     * @throws ScontoMiglioreException se esiste già uno sconto migliore di quello specificato
     * @throws IllegalArgumentException se la percentuale di sconto è un valore negativo
     * @throws ArrayIndexOutOfBoundsException se il primo argomento ha valore non compreso tra 0 e 6
     */
    public void enableDayDiscount(int i, double p) throws ScontoMiglioreException, IllegalArgumentException, ArrayIndexOutOfBoundsException{
        if(p < 0)
            throw new IllegalArgumentException("Discount percentage can not be negative");
        if(i<0 || i>6)
            throw new ArrayIndexOutOfBoundsException("First argument can assume only integers values between 0 and 6");
	if(p > dayOfWeekDiscounts[i].getDiscount()){ // se lo sconto è migliore di quello precedente
            dayOfWeekDiscounts[i].setDiscount(p); // aggiorna percentuale con la nuova
            saveDiscounts();
        }
	else
            throw new ScontoMiglioreException();
    }

    /**
     * Attiva politiche di sconto per una categoria di clienti (es. studenti, pensionati, bambini, etc.)
     * @param customer Categoria di cliente che si vuole scontare
     * @param p la percentuale di sconto da applicare
     * @throws ScontoMiglioreException se esiste già uno sconto migliore di quello specificato
     * @throws IllegalArgumentException se la percentuale di sconto è un valore negativo
     */
    public void enableDiscount(String customer, double p) throws ScontoMiglioreException, IllegalArgumentException{
        if(p < 0)
            throw new IllegalArgumentException("Discount percentage can not be negative");
        boolean discounted = false;
        for(int i=0; !discounted && i<customersDiscounts.size(); i++){ // scorri l'array dei clienti scontati, se size()==0 salta il for
            if(((String)customersDiscounts.get(i).getDiscounted()).equalsIgnoreCase(customer)){ // se il cliente è già stato scontato in precedenza
		if(p > customersDiscounts.get(i).getDiscount()){ // se il nuovo sconto è migliore
                    discounted = true;
                    customersDiscounts.get(i).setDiscount(p); // aggiorna percentuale sconto
		}
		else
                    throw new ScontoMiglioreException();
            }
	}
	if(!discounted)// se non ci sono in vigore sconti su clienti o se il cliente non ha uno sconto
            customersDiscounts.add(new CustomerDiscount(customer, p)); // memorizza il cliente
        saveDiscounts();
    }
	
    /**
     * Assegna un prezzo alle partite che si svolgono in un determinato stadio (sarà comunque soggetta ad eventuali sconti)
     * @param stadiumObject il riferimento allo stadio le cui partite devono essere soggette al cambiamento di prezzo
     * @param newPrice nuovo prezzo da assegnare alle partite dello stadio
     * @throws IllegalArgumentException se il nuovo prezzo ha valore negativo
     * @throws NoSuchElementException se nessuna partita si svolge nel dato stadio
     */
    public void setPrice(Stadium stadiumObject, double newPrice) throws IllegalArgumentException, NoSuchElementException{
        int count = 0;
	for(int i=0; i < matches.size(); i++){ // scorre tutte le partite
            if(matches.get(i).getStadium().equals(stadiumObject)){ // controlla quali si svolgono in un determinato stadio
                matches.get(i).setPrice(newPrice); // Setta il nuovo prezzo
		count++;
            }
	}
	if(count == 0) // se c'è almeno una partita che si svolge nello stadio specificato
            throw new NoSuchElementException("Can't find any matches in given stadium");
        saveMatches();
    }
    
    /**
     * Aumenta o riduci la capienza di uno stadio
     * @param stadiumObject lo stadio di cui si vuole modificare la capienza
     * @param diff intero positivo o negativo che viene sommato alla capacità attuale dello stadio
     * @throws IllegalArgumentException se i posti da rimuovere superano quelli liberi o se sono prenotati/occupati
     */
    public void setCapacity(Stadium stadiumObject, int diff) throws IllegalArgumentException{
	for(int i=0; i<matches.size(); i++){ // per tutte le partite che si svolgono nello stadio modificato
            if(matches.get(i).getStadium().equals(stadiumObject) && matches.get(i).getDate().compareTo(new GregorianCalendar()) > 0){ // se la partita si svolgerà nello stadio da modificare
		if(diff >= 0){ // se si aggiungono posti
                    for(int j=0; j<diff; j++) // rendi disponibili diff posti
			matches.get(i).setOccupied(false);
		}
		else{ // se si rimuovono posti
                    if(Math.abs(diff) > matches.get(i).getAvailable()) // se i posti da rimuovere superano quelli disponibili
			throw new IllegalArgumentException("Seats to be deleted can not be more than those available");
                    int seatsToDelete = Math.abs(diff);
                    int rows; // numero di file
                    int drawnSeats = (31+23)*2; // numero minimo di posti da rappresentare
                    for(rows=1; drawnSeats < matches.get(i).getStadium().getCapacity(); rows++)
                        drawnSeats += (31+23)*2 + 8*rows; // numero delle sedie rappresentate
                    int nullSeats = drawnSeats - matches.get(i).getStadium().getCapacity(); // posti vuoti dell'ultima fila
                    
                    for(int row = rows-1; row>=0 && seatsToDelete>0; row--){ // controlla tutte le file
                        int ovestSeats = 23+2*row;
                        int estSeats = ovestSeats;
                        int nordSeats = 31+2*row;
                        int sudSeats = nordSeats;
                        
                        // se è il primo ciclo
                        if(row == rows-1 && nullSeats>0){ 
                            if(nullSeats<=ovestSeats)
                                ovestSeats -= nullSeats;
                            else if(nullSeats<=ovestSeats+sudSeats){
                                sudSeats -= (nullSeats-ovestSeats);
                                ovestSeats = 0;
                            }
                            else if(nullSeats<=ovestSeats+sudSeats+estSeats){
                                estSeats -= (nullSeats-ovestSeats-sudSeats);
                                sudSeats = ovestSeats= 0;
                            }
                            else{
                                nordSeats -= (nullSeats-ovestSeats-sudSeats-estSeats);
                                estSeats = sudSeats = ovestSeats = 0;
                            }
                        }
                        
                        for(int grandstand = 3; seatsToDelete>0 && grandstand>=0; grandstand--){ // per ogni tribuna
                            int n;
                            if(grandstand==3)
                                n = ovestSeats;
                            else if(grandstand==2)
                                n = sudSeats;
                            else if(grandstand==1)
                                n = estSeats;
                            else
                                n = nordSeats;
                            
                            for(int seatNumber = n-1; seatsToDelete>0 && seatNumber >= 0; seatNumber--){ // per tutti i posti della fila nella tribuna
                                for(int k=0; k<tickets.size(); k++){ // per ogni biglietto della partita
                                    SeatPosition seatToDelete = new SeatPosition(row, grandstand, seatNumber);
                                    if(tickets.get(k).getPosition().equals(seatToDelete) && tickets.get(k).getMatch().equals(matches.get(i)))
                                       throw new IllegalArgumentException("Cannot delete seat " + tickets.get(k).getPosition().toString() + " in match " + tickets.get(k).getMatch() + " occupied by " + tickets.get(k).getCustomer());
                                }
                                seatsToDelete--;
                            }
                        }
                    }
                    for(int j=0; j < Math.abs(diff); j++) // rendi indisponibili diff posti
                        matches.get(i).setOccupied(true);
		}
            }
	}
	stadiumObject.setCapacity(stadiumObject.getCapacity() + diff); // aggiungi diff posti allo stadio
	saveMatches();
	saveStadiums();
    }
    
    /**
     * Visualizza l'incasso totale
     * @return la somma dei prezzi a cui sono stati acquistati i biglietti
     */
    public double getIncome(){
	double sum = 0;
	for(int i=0; i<tickets.size(); i++){
            if(tickets.get(i).isOrdered())
		sum += tickets.get(i).getOrderPrice();
        }
	return sum;
    }
		
    /**
     * Visualizza l'incasso per ogni stadio
     * @param stadiumObject riferimento allo stadio di cui si vogliono controllare i biglietti acquistati
     * @return la somma dei prezzi a cui sono stati acquistati i biglietti per uno stadio
     */
    public double getStadiumIncome(Stadium stadiumObject){
    	double sum = 0;
	for(int i=0; i<tickets.size(); i++){
            if(tickets.get(i).isOrdered() && tickets.get(i).getMatch().getStadium().equals(stadiumObject))
		sum += tickets.get(i).getOrderPrice();
	}
	return sum;
    }
}
