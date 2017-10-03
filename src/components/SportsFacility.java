package components;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

/**
    Una struttura sportiva che tiene traccia di una lista di stadi, partite, biglietti e sconti.
    I metodi sono suddivisi in due sottoclassi, ciascuna utilizzabile rispettivamente in modalità cliente e gestore.
    @author Roberto Tarullo, Pasquale Turi
*/
public abstract class SportsFacility{
    /**
     * Costruisce la superclasse struttura sportiva generica
     * @param path Il percorso alla cartella in cui vengono salvati i cambiamenti
     */
    protected SportsFacility(String path){
        // inizializzazione variabili
        this.path = path;
        for(int i=0; i<partOfDayDiscounts.length; i++) 
            partOfDayDiscounts[i] = new PartOfDayDiscount(i+1, 0);
        for(int i=0; i<dayOfWeekDiscounts.length; i++)
            dayOfWeekDiscounts[i] = new PartOfDayDiscount(i, 0);
        
	loadStadiums(); // carica stadi da file
        loadMatches();// carica partite da file
        loadDiscounts();// carica sconti da file
        loadUsers(); // carica clienti da file
        loadTickets(); // carica i biglietti di tutti i clienti da file
        
        
    }
    
    /**
     * Accede ai dettagli di una determinata partita sotto forma di stringa in linguaggio naturale
     * @param matchObject il riferimento alla partita di cui si vogliono conoscere i dettagli
     * @param discount la percentuale di sconto sulla partita, se non è scontata equivale a 0
     * @return dettagli della partita nel formato "data + ora + squadra in casa + squadra ospite + nome stadio + prezzo migliore + eventuale percentuale di sconto"
     */
    protected String getMatchInfo(Match matchObject, double discount){
	String info = matchObject.toString() + " (€" + new DecimalFormat("##.##").format(getBestPrice(matchObject, discount)) + ")";
	if(discount>0) // se sono attivi sconti aggiungi alla stringa la percentuale di sconto
        	info += " (-" + new DecimalFormat("##.#").format(discount) + "%)";
        return info;
    }
    
    /**
     * @param matchObject il riferimento alla partita di cui si vuole calcolare il prezzo
     * @param discount la percentuale di sconto sulla partita, se non è scontata equivale a 0
     * @return il prezzo della partita con l'eventuale sconto applicato
     */
    protected double getBestPrice(Match matchObject, double discount) throws IllegalArgumentException{
        if(discount>0)
            return matchObject.getPrice() - (matchObject.getPrice() / 100 * discount);
        else if(discount==0)
            return matchObject.getPrice();
        else
            throw new IllegalArgumentException("Percentage discount can not be negative");
    }
    
    /**
     * Ritorna la percentuale migliore tra gli sconti in vigore di una data partita non considerando gli sconti sui clienti
     * @param matchObject il riferimento alla partita per cui si vuole conoscere lo sconto migliore
     * @return la percentuale di sconto più alta della partita per il cliente
     */
    protected double getBestPartialDiscount(Match matchObject){
    	double min = 0;
	int i;
        boolean found = false;
	
	// I for scorrono l'array degli sconti, se lo sconto non c'è size() == 0 e il for viene saltato
	for(i=0; !found && i < matchesDiscounts.size(); i++){
            // se la partita è scontata
            if(matchObject.equals(matchesDiscounts.get(i).getDiscounted())){ 
		found = true;
                if(matchesDiscounts.get(i).getDiscount() > min) // se lo sconto è migliore di quello attuale
                   min = matchesDiscounts.get(i).getDiscount();
            }
        }
		
	found = false;
	for(i=0; !found && i < stadiumsDiscounts.size(); i++){ // scorri l'array degli stadi scontati
            if(matchObject.getStadium().getID() == (int)stadiumsDiscounts.get(i).getDiscounted()){ // se la partita si svolge nello stadio
                found = true;
                if(stadiumsDiscounts.get(i).getDiscount() > min)
                    min = stadiumsDiscounts.get(i).getDiscount();
            }
        }
		
	found = false;
        int from = 6, to = 11;
	for(i=0; !found && i<partOfDayDiscounts.length; i++){
            // se ci sono attivi sconti sulla fascia giornaliera e se la partita si svolge in quella fascia giornaliera
            if(matchObject.getDate().get(Calendar.HOUR_OF_DAY) >= from && matchObject.getDate().get(Calendar.HOUR_OF_DAY) <= to){
                found = true;
                if(partOfDayDiscounts[i].getDiscount() > min) // se il prezzo scontato è migliore
                    min = partOfDayDiscounts[0].getDiscount();
            }
            from += 6; // 6, 12, 18
            to += 6; // 11, 17, 23
	}
	
        found = false;
	for(i=0; !found && i<dayOfWeekDiscounts.length; i++){
            // se sono attivi sconti sulle partite del giorno della settimana e se la partita si svolge quel giorno
            if(matchObject.getDate().get(Calendar.DAY_OF_WEEK) == i+1){ 
		found = true;
                if(dayOfWeekDiscounts[i].getDiscount() > min) // se il prezzo scontato è migliore
                    min = dayOfWeekDiscounts[i].getDiscount();
            }
        }
        return min;
    }
    
    /**
     * Salva i cambiamenti agli stadi sul file stadiums.dat
     */
    protected void saveStadiums(){
        try{
            PrintWriter out = new PrintWriter(path + "stadiums.dat");

            for(int i=0; i < stadiums.size(); i++){
                out.println(stadiums.get(i).toString());
                out.println(stadiums.get(i).getID());
                out.println(stadiums.get(i).getCapacity());
                out.println(); // aggiungi riga vuota
            }
            out.close();
        }
        catch (IOException e) {
            System.out.println(e.toString());
        }
    }
    
    /**
     * Salva i cambiamenti alle partite sul file matches.dat
     */
    protected void saveMatches(){
        try{
            PrintWriter out = new PrintWriter(path + "matches.dat");

            for(int i=0; i < matches.size(); i++){
                out.println(matches.get(i).getStadium().getID()); // id stadio
                out.println(matches.get(i).getDate().get(Calendar.DATE)); // giorno
                out.println(matches.get(i).getDate().get(Calendar.MONTH)); // mese
                out.println(matches.get(i).getDate().get(Calendar.YEAR)); // anno
                out.println(matches.get(i).getDate().get(Calendar.HOUR_OF_DAY)); // ora
                out.println(matches.get(i).getDate().get(Calendar.MINUTE)); // minuto
                out.println(matches.get(i).getHomeTeam()); // squadra in casa
                out.println(matches.get(i).getGuestTeam());// squadra ospite
                out.println(matches.get(i).getPrice()); // prezzo base
                out.println(matches.get(i).getAvailable()); // posti disponibili
                out.println(); // aggiungi riga vuota
            }
            out.close();
        }
        catch (IOException e) {
            System.out.println(e.toString());
        }
    }
    
    /**
     * Salva i cambiamenti agli sconti sul file discounts.dat
     */
    protected void saveDiscounts(){
        try{
            PrintWriter out = new PrintWriter(path + "discounts.dat");

            out.println(customersDiscounts.size());
            for(int i=0; i < customersDiscounts.size(); i++){
                out.println(customersDiscounts.get(i).getDiscounted());
                out.println(customersDiscounts.get(i).getDiscount());
            }
            out.println();
            out.println(matchesDiscounts.size());
            for(int i=0; i < matchesDiscounts.size(); i++){
                out.println(((Match)matchesDiscounts.get(i).getDiscounted()).getDate().get(Calendar.DATE));
                out.println(((Match)matchesDiscounts.get(i).getDiscounted()).getDate().get(Calendar.MONTH));
                out.println(((Match)matchesDiscounts.get(i).getDiscounted()).getDate().get(Calendar.YEAR));
                out.println(((Match)matchesDiscounts.get(i).getDiscounted()).getDate().get(Calendar.HOUR_OF_DAY));
                out.println(((Match)matchesDiscounts.get(i).getDiscounted()).getDate().get(Calendar.MINUTE));
                out.println(((Match)matchesDiscounts.get(i).getDiscounted()).getStadium().getID());
                out.println(matchesDiscounts.get(i).getDiscount());
            }
            out.println();
            out.println(stadiumsDiscounts.size());
            for(int i=0; i < stadiumsDiscounts.size(); i++){
                out.println(stadiumsDiscounts.get(i).getDiscounted());
                out.println(stadiumsDiscounts.get(i).getDiscount());
            }
            out.println();
            for(int i=0; i < partOfDayDiscounts.length; i++)
                out.println(partOfDayDiscounts[i].getDiscount());
            out.println();
            for(int i=0; i < dayOfWeekDiscounts.length; i++)
                out.println(dayOfWeekDiscounts[i].getDiscount());
            out.println();
            out.close();
        }
        catch (IOException e) {
            System.out.println(e.toString());
        }
    }
    
    /**
     * Carica tutti gli stadi dal file stadiums.dat
     */
    private void loadStadiums(){
	try{
            Scanner fileScanner = new Scanner(new File(path + "stadiums.dat"));
            while(fileScanner.hasNextLine()){
                String stadiumName = fileScanner.nextLine();
                int id = Integer.parseInt(fileScanner.nextLine());
                int capacity = Integer.parseInt(fileScanner.nextLine());
                fileScanner.nextLine(); // linea vuota
                try{
                    stadiums.add(new Stadium(stadiumName, id, capacity));
                }
                catch(IllegalArgumentException e){
                    System.out.println(e.toString());
                }
            }
            fileScanner.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("WARNING: stadiums.dat not found");
        }
    }
    
    /**
     * Carica tutte le partite dal file matches.dat
     */
    private void loadMatches(){
        try{
            Scanner fileScanner = new Scanner(new File(path + "matches.dat"));
            while(fileScanner.hasNextLine()){ // prende tutte le partite
                boolean found = false;
                int id = Integer.parseInt(fileScanner.nextLine());
                int day = Integer.parseInt(fileScanner.nextLine());
                int month = Integer.parseInt(fileScanner.nextLine());
                int year = Integer.parseInt(fileScanner.nextLine());
                int hour = Integer.parseInt(fileScanner.nextLine());
                int minute = Integer.parseInt(fileScanner.nextLine());
                String team1 = fileScanner.nextLine();
                String team2 = fileScanner.nextLine();
                double price = Double.parseDouble(fileScanner.nextLine());
                int available = Integer.parseInt(fileScanner.nextLine());
                fileScanner.nextLine(); // linea vuota
                Stadium s = null;
                for(int i=0; !found; i++){ // trova lo stadio corrispondente alla partita tra quelli memorizzati
                    if(stadiums.get(i).getID() == id){
                         s = stadiums.get(i);
                         found = true;
                    }
                }
                try{
                    matches.add(new Match(s, new GregorianCalendar(year, month, day, hour, minute), team1, team2, price, available));
                }
                catch (IllegalArgumentException e){
                    System.out.println(e.toString());
                }
            }
            fileScanner.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("WARNING: matches.dat not found");
        }
    }
    
    /**
     * Carica tutti gli sconti dal file discounts.dat
     */
    private void loadDiscounts(){
        try{
            Scanner fileScanner = new Scanner(new File(path + "discounts.dat"));
            while(fileScanner.hasNextLine()){ // prende tutti gli sconti in vigore
                int n = Integer.parseInt(fileScanner.nextLine()); // numero clienti soggetti a sconto
                for(int i=0; i<n; i++){
                    String customerDiscounted = fileScanner.nextLine();
                    double perc = Double.parseDouble(fileScanner.nextLine());
                    customersDiscounts.add(new CustomerDiscount(customerDiscounted, perc));
                }
                fileScanner.nextLine(); // linea vuota
                
                // prende tutti gli sconti sulle partite
                n = Integer.parseInt(fileScanner.nextLine()); // numero partite soggette a sconto
                for(int i=0; i<n; i++){
                    boolean found = false;
                    int day = Integer.parseInt(fileScanner.nextLine());
                    int month = Integer.parseInt(fileScanner.nextLine());
                    int year = Integer.parseInt(fileScanner.nextLine());
                    int hour = Integer.parseInt(fileScanner.nextLine());
                    int minute = Integer.parseInt(fileScanner.nextLine());
                    int id = Integer.parseInt(fileScanner.nextLine());
                    double perc = Double.parseDouble(fileScanner.nextLine());
                    for(int j=0; !found; j++){ // trova lo stadio corrispondente allo sconto
                        if(matches.get(j).getStadium().getID() == id && matches.get(j).getDate().get(Calendar.DATE) == day && matches.get(j).getDate().get(Calendar.MONTH) == month && matches.get(j).getDate().get(Calendar.YEAR) == year && matches.get(j).getDate().get(Calendar.HOUR_OF_DAY) == hour && matches.get(j).getDate().get(Calendar.MINUTE) == minute){
                            matchesDiscounts.add(new MatchDiscount(matches.get(j), perc));
                            found = true;
                        }
                    }
                }
                fileScanner.nextLine(); // linea vuota
                
                // prende tutti gli sconti sugli stadi
                n = Integer.parseInt(fileScanner.nextLine()); // numero stadi soggetti a sconto
                for(int i=0; i<n; i++){
                    boolean found = false;
                    int id = Integer.parseInt(fileScanner.nextLine());
                    double perc = Double.parseDouble(fileScanner.nextLine());
                    for(int j=0; !found; j++){
                        if(stadiums.get(j).getID() == id){
                            stadiumsDiscounts.add(new StadiumDiscount(stadiums.get(j).getID(), perc));
                            found = true;
                        }
                    }
                }
                fileScanner.nextLine(); // linea vuota
                
                // prende tutte le fascie giornaliere soggette a sconto
                for(int i=0; i<partOfDayDiscounts.length; i++){
                    double perc = Double.parseDouble(fileScanner.nextLine());
                    partOfDayDiscounts[i].setDiscount(perc);
                }
                fileScanner.nextLine(); // linea vuota
                
                // prende tutti i giorni della settimana soggetti a sconto
                for(int i=0; i<dayOfWeekDiscounts.length; i++){
                    double perc = Double.parseDouble(fileScanner.nextLine());
                    dayOfWeekDiscounts[i].setDiscount(perc);
                }
                fileScanner.nextLine(); // linea vuota
            }
            fileScanner.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("WARNING: discounts.dat not found");
        }
    }
    
    /**
     * Carica tutti i biglietti acquistati e prenotati dai clienti dai file di tipo tickets_nome_cognome.dat
     */
    private void loadTickets(){
        for(int j=0; j < fileUsers.size(); j++){
            try{
                Scanner fileScanner = new Scanner(new File(path + "tickets_" + fileUsers.get(j) + ".dat"));
                String user = fileScanner.nextLine();
                fileScanner.nextLine();
                while(fileScanner.hasNextLine()){
                    int row = Integer.parseInt(fileScanner.nextLine());
                    int grandseat = Integer.parseInt(fileScanner.nextLine());
                    int number = Integer.parseInt(fileScanner.nextLine());
                    int day = Integer.parseInt(fileScanner.nextLine());
                    int month = Integer.parseInt(fileScanner.nextLine());
                    int year = Integer.parseInt(fileScanner.nextLine());
                    int hour = Integer.parseInt(fileScanner.nextLine());
                    int minute = Integer.parseInt(fileScanner.nextLine());
                    int id = Integer.parseInt(fileScanner.nextLine());
                    boolean ordered = Boolean.parseBoolean(fileScanner.nextLine());
                    boolean found = false;
                    for(int i=0; !found; i++){ // se coincidono la data e l'id stadio 
                        if(matches.get(i).getDate().get(Calendar.DATE) == day && matches.get(i).getDate().get(Calendar.MONTH) == month && matches.get(i).getDate().get(Calendar.YEAR) == year && matches.get(i).getDate().get(Calendar.HOUR_OF_DAY) == hour && matches.get(i).getDate().get(Calendar.MINUTE) == minute && matches.get(i).getStadium().getID() == id){
                            if(ordered){
                                double orderPrice = Double.parseDouble(fileScanner.nextLine());
                                try{
                                    tickets.add(new Ticket(matches.get(i), orderPrice, new SeatPosition(row, grandseat, number), user));
                                }
                                catch(IllegalArgumentException e){
                                    System.out.println(e.toString());
                                }
                            }	
                            else{
                                try{
                                    tickets.add(new Ticket(matches.get(i), new SeatPosition(row, grandseat, number), user));
                                }
                                catch(IllegalArgumentException e){
                                    System.out.println(e.toString());
                                }
                            }
                            found = true;
                        }
                    }
                    fileScanner.nextLine(); // linea vuota
                }
                fileScanner.close();
            }
            catch (FileNotFoundException e) {
                // se un utente registrato non ha biglietti non fare niente
            }
        }
    }
    
    /**
     * Carica tutti i clienti registrati dal file users.dat
     */
    private void loadUsers(){
        fileUsers = new ArrayList<>();
        try{
            Scanner fileScanner = new Scanner(new File(path + "users.dat")); // carica il file degli utenti registrati
            while(fileScanner.hasNextLine()){ // finchè il file finisce
                int n = Integer.parseInt(fileScanner.nextLine()); // numero di utenti registrati
                fileScanner.nextLine(); // riga vuota
                for(int i=0; i<n; i++){
                    fileUsers.add(fileScanner.nextLine()); // nome utente
                    fileScanner.nextLine(); // salta riga
                    fileScanner.nextLine(); // riga vuota
                }
            }
            fileScanner.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("WARNING: users.dat not found");
        }
    }
    
    /**
     * Ritorna una lista di tutti gli stadi
     * @return lista degli stadi
     */
    public ArrayList<Stadium> getStadiums(){
        return stadiums;
    }

    /**
     * Ritorna una lista di tutte le partite
     * @return lista delle partite
     */
    public ArrayList<Match> getMatches(){
        return matches;
    }
    
    /**
     * Ritorna una lista di tutti i biglietti
     * @return lista dei biglietti
     */
    public ArrayList<Ticket> getTickets(){
        return tickets;
    }
    
    protected String path; // path alla cartella in cui verrano salvati i file
    private ArrayList<String> fileUsers = new ArrayList<>(); // lista dei degli utenti registrati
    protected ArrayList<Stadium> stadiums = new ArrayList<>(); // lista degli stadi
    protected ArrayList<Match> matches = new ArrayList<>(); // lista delle partite
    protected ArrayList<Ticket> tickets = new ArrayList<>(); // lista dei biglietti
    protected ArrayList<Discount> customersDiscounts = new ArrayList<>(); // sconti per tipo cliente
    protected ArrayList<Discount> matchesDiscounts = new ArrayList<>(); // sconti per partita
    protected ArrayList<Discount> stadiumsDiscounts = new ArrayList<>(); // sconti per stadio
    protected Discount[] partOfDayDiscounts = new Discount[3]; // fasce giornaliere scontate
    protected Discount[] dayOfWeekDiscounts = new Discount[7];// giorni della settimana scontati
}